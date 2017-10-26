package org.placeholder.gimbalcontrol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by XZman on 19/10/2017.
 */

public class UDPClient {

    private static DatagramSocket socket = null;

    private UDPClient() {
    }

    private static void initSocket() {
        if (socket == null)
            try {
                socket = new DatagramSocket();
            }
            catch (SocketException e) {
                e.printStackTrace();
            }
    }

    public static void sendDatagram(String address, int port, byte[] data, int timeout) throws Exception {
        initSocket();
        InetAddress iNetAddress = InetAddress.getByName(address);
        socket.setSoTimeout(timeout);

        final DatagramPacket packet = new DatagramPacket(data, data.length, iNetAddress, port);
        socket.send(packet);
    }

    // unused
    public static byte[] receiveDatagram(int port, byte[] data) throws Exception {
        DatagramSocket socket = new DatagramSocket(port);

        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);

        return packet.getData();
    }
}