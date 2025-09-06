package hu.csega.common.network;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class BroadcastSender {

    public static void main(String[] args) {
        String message = "Hello! I'm: " + BroadcastReceiver.IDENTIFIER;

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255"); // global broadcast

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, BroadcastReceiver.BROADCAST_PORT);
            socket.send(packet);

            System.out.println("Broadcast message sent: " + message);
            socket.close();
        } catch(Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

}
