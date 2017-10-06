import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient extends Application
{
    private static Socket socket;
    public static Controller controller;
    public static Scene chatScreen;
    public static Scene loginScreen;
    public static Stage stage;

    /*
     * Override the start() method that's in the Application class
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage mainStage) throws Exception
    {
        // Login screen.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginLayout.fxml"));
        loginScreen = new Scene(loader.load(), 500, 500);

        // Chat screen.
        loader = new FXMLLoader(getClass().getResource("ChatLayout.fxml"));
        chatScreen = new Scene(loader.load(), 1280, 720);

        // Saves a reference of the Controller object so
        // the Listener thread can access it.
        controller = loader.getController();

        // Saves a reference of the Stage object so
        // the Controller class can access it.
        // It also sets the stage.
        stage = mainStage;
        stage.setTitle("Chat Client");
        stage.setResizable(false);
        stage.setScene(loginScreen);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
        ListenerThread.endThread = true;
    }

    /**
     * Function to send a message to the server.
     */
    public static void send(String m)
    {
        try
        {
            OutputStream outputStream = socket.getOutputStream();
            PrintStream out = new PrintStream(outputStream, false, "UTF-8");
            out.println(m);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());

            // Updates the statusLabel GUI object with the
            // error message.
            Platform.runLater(() ->
                    controller.setStatus(e.getMessage()));
        }
    }

    /**
     * Function that connects the client to the server and
     * creates a Listener thread.
     */
    public static void connect()
    {
        String hostName = "18.221.102.182";
        int portNumber = 38001;

        try
        {
            socket = new Socket(hostName, portNumber);
            ListenerThread.endThread = false;

            // Creates and starts a new thread to listen for messages.
            new ListenerThread(socket).start();
        }
        catch (UnknownHostException e)
        {
            System.err.println("ERROR: Unknown host " + hostName + ".");
        }
        catch (Exception e)
        {
            System.err.println("ERROR: Could not connect to " + hostName + ".");
        }
    }

    /**
     * Function to disconnect the client from the server.
     */
    public static void disconnect()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }
}