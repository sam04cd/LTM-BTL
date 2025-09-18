package sam;

import javax.swing.*;
import java.awt.*;
import java.net.*;

public class Client extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    private static final int SERVER_PORT = 2004;  // gửi đến server
    private static final int CLIENT_PORT = 2005;  // nhận broadcast từ server
    private static String SERVER_IP;

    public Client(String serverIP) {
        SERVER_IP = serverIP;

        setTitle("UDP Client");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- khu vực hiển thị ---
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // --- khu vực nhập + nút gửi ---
        inputField = new JTextField();
        sendButton = new JButton("Gửi");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // nhấn Enter hoặc nút Gửi đều gửi được
        inputField.addActionListener(e -> sendMessage(inputField.getText()));
        sendButton.addActionListener(e -> sendMessage(inputField.getText()));

        setVisible(true);

        // luồng nhận tin từ server
        new Thread(this::receiveFromServer).start();
    }

    private void sendMessage(String msg) {
        if (!msg.isEmpty()) {
            try (DatagramSocket socket = new DatagramSocket()) {
                byte[] data = msg.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(data, data.length,
                        InetAddress.getByName(SERVER_IP), SERVER_PORT);

                socket.send(packet);
                chatArea.append("Tôi: " + msg + "\n");
                inputField.setText("");
            } catch (Exception e) {
                chatArea.append("Lỗi gửi: " + e.getMessage() + "\n");
            }
        }
    }

    private void receiveFromServer() {
        try {
            DatagramSocket socket = new DatagramSocket(null);
            socket.setReuseAddress(true); // cho phép nhiều client bind cùng port
            socket.bind(new InetSocketAddress(CLIENT_PORT));

            byte[] buf = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                chatArea.append(msg + "\n");
            }
        } catch (Exception e) {
            chatArea.append("Lỗi nhận: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        String ip = JOptionPane.showInputDialog("Nhập IP Server:");
        SwingUtilities.invokeLater(() -> new Client(ip));
    }
}
