package org.placeholder.gimbalcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by XZman on 19/10/2017.
 */

public class UDPClient {
    private UDPClient() {
    }

    public static void sendDatagram(String address, int port, byte[] data, int timeout) throws Exception {
        InetAddress iNetAddress = InetAddress.getByName(address);

        final DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(timeout);

        final DatagramPacket packet = new DatagramPacket(data, data.length, iNetAddress, port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread());
                try {
                    socket.send(packet);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.currentThread().destroy();
            }
        }).start();
        // TO-DO: solve OutOfMemory error !!!!
    }

    // unused
    public static byte[] receiveDatagram(int port, byte[] data) throws Exception {
        DatagramSocket socket = new DatagramSocket(port);

        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);

        return packet.getData();
    }
}