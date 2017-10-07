package sample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * This class is the Controller of the Chat application's FXML file.
 *
 */
public class Controller {

    @FXML
    StackPane parentPane;

    //nodes prior to connecting
    @FXML
    TextField serverAddress;
    @FXML
    TextField username;
    @FXML
    Button connectButton;
    @FXML
    AnchorPane connectPane;

    //nodes when in connection
    @FXML
    TextArea chatArea;
    @FXML
    TextField message;
    @FXML
    Button sendButton;
    @FXML
    Button quitButton;
    @FXML
    AnchorPane chatPane;

    //connection fields
    private ConnectionManager connection= new ConnectionManager(this);

    /**
     * triggered when the "Connect" button is pressed on the GUI.
     */
    public void makeConnection() {
        //TODO: validate user input

        //this part attempts to connect with the server
        String address = serverAddress.getText();
        String[] split = address.split(":");
        String IP = split[0];
        int PORT;
        try {
            PORT = Integer.parseInt(split[1]);
            System.out.println("User entered: " + IP + " " + PORT); //TODO remove after testing
            connection.attemptConnection(IP, PORT);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //this part attempts to register on the chat system with the given name
        String name = username.getText();
        if (connection.attemptLogin(name)) {
            System.out.println("Login successful");
            //change user view
            parentPane.getChildren().remove(connectPane);
            chatPane.setOpacity(1);
        } else {
            connection.closeConnection();
            System.out.println("Login UNsuccesfull");
            //TODO inform user login was unsuccessful
        }
    }

    /**
     * Triggered when the "Send" button is clicked on the GUI.
     */
    public void sendMessage() {
        connection.sendMessage(message.getText());
        message.clear();
    }

    /**
     * Triggered when the "Disconnect" button is clicked on the GUI.
     *
     */
    public void disconnect(){
        connection.sendQuit();
        parentPane.getChildren().add(connectPane);
        chatPane.setOpacity(0);
    }
}
