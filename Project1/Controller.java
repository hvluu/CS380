import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller
{
    @FXML private TextField messageTextField;
    @FXML private ListView<String> chatWindowListView;
    @FXML private Label statusLabel;
    @FXML private TextField usernameTextField;
    private ObservableList<String> messages = FXCollections.observableArrayList();

    /**
     * Switching between the Login and Chat windows.
     */
    public void switchWindows()
    {
        if(ChatClient.stage.getScene().equals(ChatClient.loginScreen))
        {
            ChatClient.stage.setScene(ChatClient.chatScreen);
            ChatClient.stage.setResizable(true);
        }
        else
        {
            ChatClient.stage.setScene(ChatClient.loginScreen);
            ChatClient.stage.setResizable(false);
        }
        ChatClient.stage.centerOnScreen();
    }

    /**
     * Logging in function
     */
    public void login()
    {
        ChatClient.connect();
        ChatClient.send(usernameTextField.getText());
        switchWindows();
    }

    /**
     * Logging out function.
     * After logging out, the thread is also killed
     */
    public void logout()
    {
        switchWindows();
        messages.clear();
        ListenerThread.endThread = true;
        ChatClient.disconnect();
    }

    
    public void displayError(String title, String message)
    {
        logout();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Sends a message to the server when the user
     * either clicks on the Send button or presses
     * the Enter key while the Message TextField is focused.
     */
    public void send()
    {
        String message = messageTextField.getText();

        // Trimming the message and checking if the
        // String is not empty after trimming it
        // prevents blank messages from being sent.
        if(!message.trim().isEmpty())
        {
            ChatClient.send(message);
            messageTextField.clear();
        }
    }

    /**
     * Displays the messages received from the server.
     * @param message - The last received message.
     */
    public void setMessages(String message)
    {
        messages.add(message);
        chatWindowListView.setItems(messages);
    }

    /**
     * Displays the status messages located in the status bar.
     * @param status - The status message.
     */
    public void status(String status)
    {
        statusLabel.setText("Status: " + status + ".");
    }
}
