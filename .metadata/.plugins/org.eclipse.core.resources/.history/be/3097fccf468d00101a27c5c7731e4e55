//package sam;
//
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//
//public class Server {
//    public static void main(String[] args) {
//        int port = 2004; // cổng lắng nghe
//        byte[] buf = new byte[5000];
//
//        try (DatagramSocket socket = new DatagramSocket(port)) {
//            System.out.println("Đang chờ kết nối đến Client: " + port + " ...");
//            while (true) {
//                DatagramPacket packet = new DatagramPacket(buf, buf.length);
//                socket.receive(packet); // chờ nhận
//                String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
//                String from = packet.getAddress().getHostAddress() + ":" + packet.getPort();
//                System.out.println("Nhận từ " + from + " -> " + msg);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
