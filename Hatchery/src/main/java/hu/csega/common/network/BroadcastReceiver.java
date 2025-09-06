package hu.csega.common.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class BroadcastReceiver {

    public static final int BROADCAST_PORT = 8890;
    public static final int ACCEPT_TASK_PORT = 8891;

    private static String getComputerName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
            return "Hatchery";
        }
    }

    public static final String IDENTIFIER = getComputerName();

    public static void main(String[] args) throws Exception {
        final DatagramSocket responseSocket = new DatagramSocket(ACCEPT_TASK_PORT, InetAddress.getByName("0.0.0.0"));

        Thread acceptTaskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Listening for simple messages on port " + ACCEPT_TASK_PORT + "...");

                    byte[] buffer = new byte[256];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        responseSocket.receive(packet);

                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Received from " + packet.getAddress() + ": " + message);

                        final String responseHostAddress = packet.getAddress().getHostAddress();
                        if("Status?".equals(message))
                            sendMessage(responseSocket, responseHostAddress, "Online.");
                        else if ("Accepting.".equals(message))
                            sendMessage(responseSocket, responseHostAddress, "Status?");
                    }
                } catch(Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        });

        acceptTaskThread.start();

        try (DatagramSocket socket = new DatagramSocket(BROADCAST_PORT, InetAddress.getByName("0.0.0.0"))) {
            socket.setBroadcast(true);

            System.out.println("Listening for broadcast messages on port " + BROADCAST_PORT + "...");

            byte[] buffer = new byte[256];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received from " + packet.getAddress() + ": " + message);

                if(message.contains(IDENTIFIER)) {
                    System.out.println("I'm " + IDENTIFIER + " myself, so I keep quiet.");
                    continue;
                }

                final String responseHostAddress = packet.getAddress().getHostAddress();
                sendMessage(responseSocket, responseHostAddress, "Accepting.");
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        } finally {
            responseSocket.close();
        }
    }

    private static void sendMessage(DatagramSocket responseSocket, String host, String message) throws Exception {
        System.out.println("Sending message to " + host + ": " + message);
        final InetAddress address = InetAddress.getByName(host);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, ACCEPT_TASK_PORT);
        responseSocket.send(packet);
    }

}
