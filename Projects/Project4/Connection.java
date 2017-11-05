

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Connection extends Thread {

    public volatile static boolean endThread = false;
    private Socket socket = null;

    public Connection(Socket socket) {
        super("Connecting Thread");
        this.socket = socket;
    }

    /**
     * Override run() function in the Thread class. The function handles the
     * communication between the server and the client.
     */
    @Override
    public void run() {
        try {
            socket.getInputStream();
            socket.getOutputStream();

            ipv6();
            Ipv6Client.disconnect();
        } catch (IOException e) {
            System.err.println("ERROR: Connection lost.");
        }
    }

    private void ipv6() throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        for (int i = 0; i < 12; i++) {
            int dataLength = (int) Math.pow(2, i + 1);
            byte[] packet = new byte[dataLength + 40];

            System.out.println("\nData length: " + (dataLength));

            byte[] data = new byte[dataLength];
            new Random().nextBytes(data);

            packet[0] = (0b0110 << 4);
            packet[1] = 0x0;
            packet[2] = 0x0;
            packet[3] = 0x0;

            short temp = (short) (dataLength);
            byte second = (byte) ((temp >>> 8) & 0xFF);
            byte first = (byte) (temp & 0xFF);
            packet[4] = second;
            packet[5] = first;

            packet[6] = 0x11;

            packet[7] = 0x14;

            for (int k = 8; k < 18; k++) {
                packet[k] = 0b0;
            }

            packet[18] = (byte) 0xFF;
            packet[19] = (byte) 0xFF;
            packet[20] = 0b01111111;
            packet[21] = 0b0;
            packet[22] = 0b0;
            packet[23] = 0b01;

            for (int j = 24; j < 34; j++) {
                packet[j] = 0b0;
            }

            packet[34] = (byte) 0xFF;
            packet[35] = (byte) 0xFF;
            packet[36] = (byte) 0x12;
            packet[37] = (byte) 0xDD;
            packet[38] = (byte) 0x66;
            packet[39] = (byte) 0xB6;

            for (int m = 40; m < packet.length; m++) {
                packet[m] = data[(m - 40)];
            }
            outputStream.write(packet);
            System.out.print("Response: 0x");
            for (int l = 0; l < 4; l++) {
                System.out.printf("%x", inputStream.read());
            }
            System.out.println();
        }
    }
}