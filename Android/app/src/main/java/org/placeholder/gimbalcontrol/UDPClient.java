package org.placeholder.gimbalcontrol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by XZman on 19/10/2017.
 */

public class UDPClient {

    private static DatagramSocket socket = null;
    private static DatagramSocket socket2 = null;

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

    private static void initSocket2(int port) {
        if (socket2 == null) {
            try {
                socket2 = new DatagramSocket(port);
            }
            catch (SocketException e) {
                e.printStackTrace();
            }
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
    public static int receiveDatagram(int port, byte[] data) throws Exception {
        initSocket2(port);

        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket2.receive(packet);

        return packet.getLength();
    }
}