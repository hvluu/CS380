import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TicTacToeClient {

    public static void main(String args[]) throws UnknownHostException, IOException {

        try (Socket socket = new Socket("18.221.102.182", 38006)) {
            System.out.println("Connected to server...");
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            
            //This thread checks for messages and decodes/outputs them
            Thread getMessage = new Thread() {
                public void run() {
                    Object message;
                    while (true) {
                        try {
                            message = inStream.readObject();
                            if (message.getClass().equals(BoardMessage.class)) {
                                BoardMessage bmess = (BoardMessage) message;
                                printBoard(bmess.getBoard());
                                int win = checkWin(bmess.getBoard());
                                switch (win) {
                                    case 1:
                                        System.out.println("You Win!");
                                        outStream.writeObject(new CommandMessage(CommandMessage.Command.EXIT));
                                        System.exit(0);
                                        break;
                                    case 2:
                                        System.out.println("Computer Wins!");
                                        outStream.writeObject(new CommandMessage(CommandMessage.Command.EXIT));
                                        System.exit(0);
                                        break;
                                    case 3:
                                        System.out.println("Tie Game.");
                                        outStream.writeObject(new CommandMessage(CommandMessage.Command.EXIT));
                                        System.exit(0);
                                        break;
                                    default:
                                        break;
                                }
                            } else if (message.getClass().equals(ErrorMessage.class)) {
                                ErrorMessage err = (ErrorMessage) message;
                                System.out.println(err.getError());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter Username: ");
            String username = sc.next();
            outStream.writeObject(new ConnectMessage(username));
            outStream.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
            getMessage.start();
            while (true) {
                System.out.println("Enter the number for where you want to move (-1 if you want to surrender): ");
                int move = sc.nextInt();
                switch (move) {
                    case 1:
                        outStream.writeObject(new MoveMessage((byte) 0, (byte) (0)));
                        break;
                    case 2:
                        outStream.writeObject(new MoveMessage((byte) 0, (byte) (1)));
                        break;
                    case 3:
                        outStream.writeObject(new MoveMessage((byte) 0, (byte) (2)));
                        break;
                    case 4:
                        outStream.writeObject(new MoveMessage((byte) 1, (byte) (0)));
                        break;
                    case 5:
                        outStream.writeObject(new MoveMessage((byte) 1, (byte) (1)));
                        break;
                    case 6:
                        outStream.writeObject(new MoveMessage((byte) 1, (byte) (2)));
                        break;
                    case 7:
                        outStream.writeObject(new MoveMessage((byte) 2, (byte) (0)));
                        break;
                    case 8:
                        outStream.writeObject(new MoveMessage((byte) 2, (byte) (1)));
                        break;
                    case 9:
                        outStream.writeObject(new MoveMessage((byte) 2, (byte) (2)));
                        break;
                    case -1:
                        outStream.writeObject(new CommandMessage(CommandMessage.Command.SURRENDER));
                        break;
                    case 0:
                        outStream.writeObject(new CommandMessage(CommandMessage.Command.EXIT));
                        System.exit(0);
                        break;
                }
            }
        }
    }

    public static void printBoard(byte[][] board) {
        
    }

    public static int checkWin(byte[][] board) {
    }
