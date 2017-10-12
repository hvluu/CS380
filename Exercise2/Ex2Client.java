import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ex2Client
{
    private static Socket socket;

    public static void main(String[] args)
    {
        connect();
    }

    /**
     * Connects the client to the server and
     * creates a thread.
     */
    public static void connect()
    {
        String hostName = "18.221.102.182";
        int portNumber = 38102;

        try
        {
            socket = new Socket(hostName, portNumber);
            new Connection(socket).start();
            System.out.println("Connected to server.");
        }
        catch (UnknownHostException e)
        {
            System.err.println("ERROR: Unknown host " + hostName + ".");
        }
        catch (IOException e)
        {
            System.err.println("ERROR: Could not connect to " + hostName + ".");
        }
    }

    /**
     * Disconnects the client from the server.
     */
    public static void disconnect()
    {
        try
        {
            socket.close();
            System.out.println("Disconnected from server.");
        }
        catch (IOException e)
        {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}