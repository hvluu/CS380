
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Random;

public class UdpClient {

    private static double TOTAL_RTT = 0;
    private static Packet IPv4;
    private static int DATA_SIZE = 2;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("18.221.102.182", 38005);
            IPv4 = new Packet(socket);
            OutputStream outputStream = socket.getOutputStream();

            outputStream.write(IPv4.generateHandShake());
            System.out.println("Handshake response: 0x" +receivedResponse(socket));
            int portNumber = getPort(socket);
            System.out.println("Port number received: " + portNumber);

            while(DATA_SIZE <= 4096) {
                System.out.println("\nSending packet with " + DATA_SIZE + " bytes of data");
                double startTime = System.currentTimeMillis();
                byte[] header =  udpHeader(DATA_SIZE, portNumber);
                byte[] udpPack = IPv4.generateUDPPacket(DATA_SIZE, header);
                outputStream.write(udpPack);
                System.out.println("Response: 0x" + receivedResponse(socket));
                double endTime = System.currentTimeMillis();
                System.out.println("RTT: " + (endTime - startTime) + "ms");
                TOTAL_RTT += (endTime - startTime);
                DATA_SIZE *= 2;
            }
            double averageRTT = TOTAL_RTT / 12;
            DecimalFormat format = new DecimalFormat("#.##");
            System.out.println("\nAverage RTT: " + format.format(averageRTT) + "ms");

        } catch (IOException e) {}
    }

    private static byte[] udpHeader(int size, int port) {
        byte[] data = new byte[size];
        new Random().nextBytes(data);

        byte[] packet = new byte[8 + data.length];

        // SOURCE PORT
        packet[0] = 0;
        packet[1] = 0;
        // DESTINATION PORT
        packet[2] = (byte) ((port & 0xFF00) >>> 8);
        packet[3] = (byte) (port & 0x00FF);
        // UDP LENGTH
        packet[4] = (byte) ((packet.length & 0xFF00) >>> 8);
        packet[5] = (byte) (packet.length & 0x00FF);
        // INITIAL CHECK SUM
        packet[6] = 0;
        packet[7] = 0;

        int pos = 0;
        for(int i = 8; i < packet.length; ++i) {
            packet[i] = data[pos++];
        }

        // perform CheckSum
        byte[] checkSum = getUDPCheckSum(packet);
        packet[6] = checkSum[0];
        packet[7] = checkSum[1];

        return packet;

    }

    private static byte[] getUDPCheckSum(byte[] packet) {
        byte[] pseudoHeader = psuedoHeader(packet);
        short checkSum = (short) IPv4.checkSum(pseudoHeader);
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(checkSum);
        return buffer.array();
    }

    public static byte[] psuedoHeader(byte[] header) {

        byte[] source = IPv4.getSourceAddr();
        byte[] destination = IPv4.getDestinationAddr();
        int protocol = 17;
        int length = header.length;

        byte[] packet = new byte[header.length + 12];

        for(int i = 0; i < source.length; ++i) {
            packet[i] = source[i];
        }
        int count = 4;
        for(int k = 0; k < destination.length; ++k) {
            packet[count++] = destination[k];
        }

        packet[8] = 0;
        packet[9] = (byte) protocol;
        packet[10] = (byte)((length & 0xFF00) >>> 8);
        packet[11] = (byte)((length & 0x00FF));
        count = 0;
        for(int i = 12; i < packet.length; ++i) {
            packet[i] = header[count++];
        }

        return packet;
    }

    private static int getPort(Socket socket) {
        try {
            int portNumber = -1;
            InputStream is = socket.getInputStream();
            byte[] received = new byte[2];
            received[0] = (byte) is.read();
            received[1] = (byte) is.read();
            portNumber = ((received[0] & 0xFF) << 8) | (received[1] & 0xFF);
            return portNumber;
        } catch (IOException e) { }
        return -1;
    }

    private static String receivedResponse(Socket socket) {
        try {
            InputStream is = socket.getInputStream();
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 4; ++i) {
                sb.append(Integer.toHexString(is.read()).toUpperCase());
            }
            return sb.toString();
        } catch (IOException e) { }
        return "error";
    }

}