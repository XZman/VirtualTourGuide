package org.placeholder.gimbalcontrol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by XZman on 19/10/2017.
 */

public class UDPClient {

    // for sending packets
    private static DatagramSocket socket = null;

    // for receiving packets
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

    public static byte[] receiveDatagram(int port, byte[] data) throws Exception {
        initSocket2(port);

        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket2.receive(packet);

        return Arrays.copyOf(data, packet.getLength());
    }

    public static void getBytesFromFloat(final float value, byte[] bits, final int writePos) throws IllegalArgumentException {
        if (bits.length - writePos < 0)
            throw new IllegalArgumentException("arrays length not enough");

        int bytes = Float.floatToIntBits(value);
        bits[writePos] = (byte)(0xFF & (bytes >> 24));
        bits[writePos + 1] = (byte)(0xFF & (bytes >> 16));
        bits[writePos + 2] = (byte)(0xFF & (bytes >> 8));
        bits[writePos + 3] = (byte)(0xFF & bytes);
    }

    public static int getIntFromBytes(byte[] bytes) {
        if (bytes.length < 4)
            throw new IllegalArgumentException("arrays length not enough");
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }
}