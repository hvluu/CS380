import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WebServer {

	public static void main(String[] args) {
		try {

			ServerSocket serverSocket = new ServerSocket(8080);

			while (true) {

				Socket socket = serverSocket.accept();

				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				String text;
				String path = br.readLine();
				System.out.println(path);

				String[] getPath = path.split(" ");
				File file = new File("www" + getPath[1]);
				PrintStream out = new PrintStream(socket.getOutputStream(), true, "UTF-8");

				System.out.println("\nChecking if the file exists");
				if (file.exists()) {
					FileInputStream fis = new FileInputStream(file);
                                        
                                        //Can't seem to print it this way
					out.println("HTTP/1.1 200 OK");
					out.println("Content-type: text/html");
					out.println("Content-length: " + file.length() + "\n");
                                        
                                        // Can't print with printstream so print it the regular way
                                        System.out.println("HTTP/1.1 200 OK");
                                        System.out.println("Content-type: text/html");
                                        System.out.println("Content-length: " + file.length() + "\n");
					Scanner scan = new Scanner(file);
					while (scan.hasNext()) {
						out.println(scan.nextLine());
					}

					scan.close();
					fis.close();
				} else {
					File errorFile = new File("www/404 Not Found.html");
					FileInputStream errorFis = new FileInputStream(errorFile);

                                        //Can't seem to print it this way
					out.println("HTTP/1.1 404 Not Found");
					out.println("Content-type: text/html");
					out.println("Content-length: " + errorFile.length() + "\n");
                                        
                                        // Can't print with printstream so print it the regular way
                                        System.out.println("HTTP/1.1 404 Not Found");
                                        System.out.println("Content-type: text/html");
                                        System.out.println("Content-length: " + errorFile.length() + "\n");

					Scanner scanErrorFile = new Scanner(errorFile);
					while (scanErrorFile.hasNext()) {
						out.println(scanErrorFile.nextLine());
					}

					scanErrorFile.close();
					errorFis.close();
				}
				socket.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}