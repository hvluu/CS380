import java.io.*;
import java.net.Socket;

public class Threads extends Thread
{
    private Socket socket = null;

    public Threads(Socket socket)
    {
        super("ServerThread for "
                + socket.getInetAddress().getHostAddress());
        this.socket = socket;
    }

    /**
     * The overridden run() function belonging to the Thread class.
     * This is what handles the communication between the server and the client.
     */
    public void run()
    {
        try
        {
            String clientAddress = socket.getInetAddress().getHostAddress();

            // // String to hold messages received and read for the client.
            String message;
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader in = new BufferedReader(inputStreamReader);

            // Objects needed for sending messages to the client.
            OutputStream outputStream = socket.getOutputStream();
            PrintStream out = new PrintStream(outputStream, true, "UTF-8");

            System.out.println("Client connected: " + clientAddress);

            // Welcomes the client.
            // NOTE: This is important because the client is waiting to receive
            // a message in order to be able to send a message to the server.
            out.println("Hi " + clientAddress + ", thanks for connecting!"
                + " If you would like to disconnect just type \"EXIT\".");

            // Execution loop runs when the Client sends a message
            while((message = in.readLine()) != null)
            {
                if(message.toUpperCase().equals("EXIT"))
                    break;

                // Echoes the message back to the client from the Server.
                out.println(message);
            }
            socket.close();
            System.out.println("Client disconnected: " + clientAddress);
        }
        catch (IOException e)
        {
            System.err.println("ERROR: Connection lost with client "
                + socket.getInetAddress().getHostAddress());
        }
    }
}