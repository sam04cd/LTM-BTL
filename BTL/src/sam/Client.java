package sam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_UDP_PORT = 5000;
    private static final int CLIENT_UDP_PORT = 5001;
    private static final int TCP_FILE_PORT = 6000;

    private String username;
    private DatagramSocket recvSocket;
    private JFrame frame;
    private JPanel panelMessages;
    private JScrollPane scrollPane;
    private JTextField input;
    private JTextArea onlineArea;
    private final File downloadsDir = new File("downloads");

    public Client() {
        if (!downloadsDir.exists()) downloadsDir.mkdirs();

        // Try to set FlatLaf if available, otherwise fallback to system LAF
        try {
            Class<?> lafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            LookAndFeel laf = (LookAndFeel) lafClass.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel(laf);
        } catch (ClassNotFoundException cnf) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) { ex.printStackTrace(); }
        } catch (Exception ex) { ex.printStackTrace(); }

        username = JOptionPane.showInputDialog(null, "Nhập tên của bạn:", "Messenger", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) username = "User" + (int)(Math.random()*9999);
        username = username.trim();

        createUI();
        startUdpListener();
        sendUdp("HELLO:" + username);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> sendUdp("BYE:" + username)));
    }

    private void createUI() {
        frame = new JFrame("Messenger • " + username);
        frame.setSize(1000, 700);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(240, 242, 246));

        // Header xanh Messenger
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 106, 255));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("Chat Room", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title);

        JLabel status = new JLabel("● Hoạt động");
        status.setForeground(new Color(150, 255, 150));
        status.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        status.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        header.add(status, BorderLayout.EAST);

        // Khu vực tin nhắn
        panelMessages = new JPanel();
        panelMessages.setLayout(new BoxLayout(panelMessages, BoxLayout.Y_AXIS));
        panelMessages.setBackground(new Color(240, 242, 246));

        scrollPane = new JScrollPane(panelMessages);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        // Sidebar online
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 220, 220)));

        JLabel onlineTitle = new JLabel("Người liên hệ");
        onlineTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        onlineTitle.setForeground(new Color(0, 106, 255));
        onlineTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 0));
        sidebar.add(onlineTitle, BorderLayout.NORTH);

        onlineArea = new JTextArea();
        onlineArea.setEditable(false);
        onlineArea.setBackground(Color.WHITE);
        onlineArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        onlineArea.setForeground(new Color(51, 51, 51));
        sidebar.add(new JScrollPane(onlineArea), BorderLayout.CENTER);

        // Thanh nhập liệu
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftIcons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftIcons.setOpaque(false);

        JButton attachBtn = createIconBtn("📎");
        JButton emojiBtn = createIconBtn("😊");
        JButton likeBtn = createIconBtn("👍");

        attachBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                new Thread(() -> uploadFile(fc.getSelectedFile())).start();
            }
        });

        JPopupMenu emojiMenu = new JPopupMenu();
        String[] emojis = {"😀","😂","😍","🥰","😢","😡","👍","❤️","🔥","🎉","💀","🤩","😘","😴"};
        for (String e : emojis) {
            JMenuItem item = new JMenuItem(e);
            item.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            item.addActionListener(ev -> input.setText(input.getText() + e));
            emojiMenu.add(item);
        }
        emojiBtn.addActionListener(e -> emojiMenu.show(emojiBtn, 0, emojiBtn.getHeight()));

        likeBtn.addActionListener(e -> {
            sendUdp(username + ": 👍");
            addMessageBubble(username, "👍", true);
        });

        leftIcons.add(attachBtn);
        leftIcons.add(emojiBtn);
        leftIcons.add(likeBtn);

        inputPanel.add(leftIcons, BorderLayout.WEST);

        input = new JTextField();
        input.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        input.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        input.addActionListener(e -> sendMessage());
        inputPanel.add(input, BorderLayout.CENTER);

        JButton sendBtn = new JButton("Gửi");
        sendBtn.setBackground(new Color(0, 106, 255));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.setBorderPainted(false);
        sendBtn.setFocusPainted(false);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.addActionListener(e -> sendMessage());
        inputPanel.add(sendBtn, BorderLayout.EAST);

        // Layout
        frame.add(header, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(sidebar, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createIconBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btn.setForeground(new Color(0, 106, 255));
        btn.setBackground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(240, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    private void sendMessage() {
        String text = input.getText().trim();
        if (text.isEmpty()) return;
        addMessageBubble(username, text, true);
        sendUdp(username + ": " + text);
        input.setText("");
    }

    private void addMessageBubble(String name, String message, boolean mine) {
        JPanel wrapper = new JPanel(new FlowLayout(mine ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 8));
        wrapper.setOpaque(false);

        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        Color bubbleColor = mine ? new Color(0, 106, 255) : Color.WHITE;
        Color textColor = mine ? Color.WHITE : new Color(51, 51, 51);

        bubble.setBackground(bubbleColor);
        bubble.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(mine ? new Color(0, 90, 220) : new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Bo góc
        bubble = new RoundedPanel(bubbleColor, mine ? new Color(0, 90, 220) : new Color(220, 220, 220), 22);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(mine ? new Color(200, 230, 255) : new Color(0, 106, 255));

        JLabel msgLabel = new JLabel("<html><div style='max-width: 400px;'>" + escapeHtml(message) + "</div></html>");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        msgLabel.setForeground(textColor);

        bubble.add(nameLabel);
        bubble.add(Box.createVerticalStrut(2));
        bubble.add(msgLabel);

        wrapper.add(bubble);
        panelMessages.add(wrapper);
        panelMessages.add(Box.createVerticalStrut(5));
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }

    // ============== NETWORK (giữ nguyên) ==============
    private void startUdpListener() {
        new Thread(() -> {
            try {
                recvSocket = new DatagramSocket(CLIENT_UDP_PORT);
                byte[] buf = new byte[8192];
                while (true) {
                    DatagramPacket p = new DatagramPacket(buf, buf.length);
                    recvSocket.receive(p);
                    String msg = new String(p.getData(), 0, p.getLength(), StandardCharsets.UTF_8).trim();
                    if (msg.startsWith("ONLINE:")) {
                        String list = msg.substring(7);
                        SwingUtilities.invokeLater(() -> {
                            onlineArea.setText(list.replace(",", "\n"));
                            frame.setTitle("Messenger • " + username + " (" + list.split(",").length + " online)");
                        });
                    } else if (msg.startsWith("FILE:")) {
                        String[] parts = msg.split(":", 3);
                        if (parts.length >= 3) {
                            addMessageBubble("System", "File mới: " + parts[1] + " từ " + parts[2], false);
                        }
                    } else {
                        addMessageBubble("Server", msg, false);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void sendUdp(String text) {
        new Thread(() -> {
            try {
                byte[] data = text.getBytes(StandardCharsets.UTF_8);
                DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(SERVER_IP), SERVER_UDP_PORT);
                DatagramSocket s = new DatagramSocket();
                s.send(p);
                s.close();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void uploadFile(File f) {
        SwingUtilities.invokeLater(() -> addMessageBubble("Bạn", "Đang gửi file: " + f.getName(), true));
        // (giữ nguyên code upload cũ của bạn)
    }

    // RoundedPanel class
    class RoundedPanel extends JPanel {
        private Color bg, border;
        private int radius;
        RoundedPanel(Color bg, Color border, int radius) {
            this.bg = bg; this.border = border; this.radius = radius;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(border);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}