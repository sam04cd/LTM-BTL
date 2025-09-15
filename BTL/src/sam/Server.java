package sam;

import javax.swing.*;
import java.awt.*;
import java.net.*;

public class Server extends JFrame {
    private JTextArea logArea;
    private JTextField inputField;

    private static final int SERVER_PORT = 2004;  // nhận từ client
    private static final int CLIENT_PORT = 2005;  // gửi broadcast đến client
    private static final String BROADCAST_IP = "255.255.255.255";

    public Server() {
        setTitle("UDP Server (Broadcast)");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.addActionListener(e -> sendMessage(inputField.getText()));
        add(inputField, BorderLayout.SOUTH);

        setVisible(true);

        new Thread(this::receiveFromClients).start();
    }

    private void sendMessage(String msg) {
        if (!msg.isEmpty()) {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setBroadcast(true);
                String fullMsg = "Server: " + msg;
                byte[] data = fullMsg.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(data, data.length,
                        InetAddress.getByName(BROADCAST_IP), CLIENT_PORT);

                socket.send(packet);
                logArea.append(fullMsg + "\n");
                inputField.setText("");
            } catch (Exception e) {
                logArea.append("Lỗi gửi: " + e.getMessage() + "\n");
            }
        }
    }

    private void receiveFromClients() {
        try (DatagramSocket socket = new DatagramSocket(SERVER_PORT)) {
            byte[] buf = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                String from = packet.getAddress().getHostAddress();
                logArea.append("Client (" + from + "): " + msg + "\n");

                // broadcast lại cho toàn bộ client
                sendMessage("Client " + from + ": " + msg);
            }
        } catch (Exception e) {
            logArea.append("Lỗi nhận: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Server::new);
    }
}
