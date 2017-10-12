
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public final class EchoClient {

    public static void main(String[] args) throws IOException {
    	String hostName = "localhost";
    	int portNumber = 22222;

        try (Socket socket = new Socket("localhost", 22222)) {
        	// String to hold messages received and read for Server
        	String message;
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            
            // New scanner to send the message to the Server
            Scanner sc = new Scanner(System.in);
            OutputStream outputStream = socket.getOutputStream();
            PrintStream out = new PrintStream(outputStream, false, "UTF-8");

            // Execution loop only runs when the Server sends a message
            while ((message = br.readLine()) != null)
            {
            	System.out.println("Server> " + message);
            	System.out.println("Client> ");
            	message = sc.nextLine();

            	// Sends the messages to the Server from the Client
            	out.println(message);

            	if (message.toUpperCase().equals("EXIT"))
            		break;
            }
            socket.close();
        }
        catch (UnknownHostException e)
       	{
       		System.err.println("ERROR: Unknown host " + hostName + ".");
       	}
       	catch (Exception e)
       	{
       		System.err.println("ERROR: Could not connect to " + hostName + ".");
       	}
       	finally
       	{
       		System.exit(1);
       	}
    }
}