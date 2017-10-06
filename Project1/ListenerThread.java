import javafx.application.Platform;
import java.io.*;
import java.net.Socket;

public class ListenerThread extends Thread
{
    public volatile static boolean endThread = false;
    private Socket socket = null;

    public ListenerThread(Socket socket)
    {
        super("Chat Listener Thread");
        this.socket = socket;
    }

    /**
     * Override the run() function that's in the Thread class.
     * This overriden function handles the communication between client and server
     */
    public void run()
    {
        try
        {
            // Objects needed for receiving and reading the server's messages.
            String rMessage;
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader in = new BufferedReader(inputStreamReader);

            // The main loop of execution.
            // This executes when the servers sends a message
            // and the thread has not received a flag to terminate.
            while((rMessage = in.readLine()) != null
                    && !endThread)
            {
                // NOTE: The variable sent to the
                // Application GUI thread has to be final.
                final String message = rMessage;

                // Displays the received messages.
                Platform.runLater(() ->
                        ChatClient.controller.setMessages(message));

                // Catches the Unavailable Username error.
                if(message.equals("Name is taken."))
                {
                    Platform.runLater(() ->
                            ChatClient.controller.displayError("Unavailable username",
                                    "The username you entered is already taken."));
                    endThread = true;
                }

                // Catches the Inactivity error.
                if(message.equals("Connection idles for 1 minute, closing connection."))
                {
                    Thread.sleep(5000);
                    Platform.runLater(() ->
                            ChatClient.controller.displayError("Connection Timed Out",
                                    "You were idle for 1 minute, "
                                            + "your connection is now closed."));
                    endThread = true;
                }
            }
            ChatClient.disconnect();
        }
        catch (IOException e)
        {
            System.err.println("ERROR: Connection to the server is lost.");
        }
        catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
        }
    }
}
