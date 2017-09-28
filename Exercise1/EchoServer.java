import java.io.IOException;
import java.net.ServerSocket;

public final class EchoServer
{
    public static void main(String[] args) throws IOException
    {
        int portNumber = 22222;
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber))
        {
            while (listening)
            {
                // Creates and starts a new thread for the new connected client.
                new Threads(serverSocket.accept()).start();
            }
        }
        catch (IOException e)
        {
            System.err.println("ERROR: Could not listen on port " + portNumber + ".");
            System.exit(-1);
        }
    }
}