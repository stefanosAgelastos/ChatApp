package sample.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This class is responsible for handling the connection to the Chat server.
 */

public class ConnectionManager {

    private final Controller controller;
    private Socket socket;
    private Scanner inputScanner;
    private PrintWriter outputPrinter;
    private boolean connectionEstablishedFlag = false;
    private String serverIP;
    private int serverPORT;
    private String username;
    private Thread readInput;

    /**
     * this constructor is called by the Application's controller.
     * @param controller pass a reference to the application's Controller.
     */
    public ConnectionManager(Controller controller) {
        this.controller = controller;
    }


    /**
     * Attempts to create a Socket and connect a Scanner and a PrintStream to that Socket.
     * If the connection is established, then it changes the instance's field connectionEstablishedFlag to true.
     * @param IP the IP address of the ServerSocket to connect to.
     * @param PORT PORT of the Server socket to connect to.
     * @throws UnknownHostException if the
     */
    public void attemptConnection(String IP, int PORT) throws UnknownHostException{
        if (!connectionEstablishedFlag) {
            try {
                System.out.println("attempting connection, IP is "+IP);
                System.out.println("attempting connection, PORT is "+PORT);
                InetAddress inetAddress = InetAddress.getByName(IP);
                socket = new Socket(inetAddress, PORT);
                inputScanner = new Scanner(socket.getInputStream());
                outputPrinter = new PrintWriter(socket.getOutputStream(),true );
                connectionEstablishedFlag = true;
                serverIP = IP;
                serverPORT = PORT;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Attempts to send a JOIN message to the chat server with the passed String as a username.
     * If the answer is J_OK, then two methods are called, startReceivingThread(), startIMAVThread().
     * @param candidateUsername the username to register to the Chat server.
     * @return false if there is no established connection, or if the server answers with anything else than J_OK. True if the server sends back a J_OK message.
     */
    public boolean attemptLogin(String candidateUsername){
        if(connectionEstablishedFlag){
            System.out.println("Connection established.");
            outputPrinter.println("JOIN "+candidateUsername+", "+serverIP+":"+serverPORT );
            String answer = inputScanner.nextLine();
            System.out.println(answer); //TODO remove after testing
            if(answer.equalsIgnoreCase("J_OK")){ //TODO implement this in a Class?
                this.username = candidateUsername;
                startReceivingThread();
                startIMAVThread();
                return true;
            }
        }
        return false;
    }


    /**
     * starts a thread that sends IMAV messages to the Server every 50000 ms, while the connectionEstablishedFlag is true.
     * called by the attemptLogin() method, if the Server sends a J_OK message.
     */
    private void startIMAVThread() {
        Thread imav = new Thread(()->{
            while(connectionEstablishedFlag){
                sendToServer("IMAV");
                try {
                    Thread.sleep(50000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        imav.start();
    }

    /**
     * starts a thread that reads messages form the Socket's inputStream and forwards them to the Controller for the User to see.
     * Runs while the connectionEstablishedFlag is true.
     * called by the attemptLogin() method, if the Server sends a J_OK message.
     */
    private void startReceivingThread() {
        //TODO filter the messages received. errors, list etc..
        readInput = new Thread(()->{
            while(connectionEstablishedFlag) {
                if (inputScanner.hasNext()) {
                    String message = inputScanner.nextLine();
                    controller.chatArea.appendText(message + "\n"); //TODO call a method in controller
                }
            }
        });
        readInput.start();

    }

    /**
     * Formats the message according to the protocol: DATA <<user_name>>: <<free text…>>
     * and then calls sendToServer()
     * @param text free text, the message the user want to send to the Chat Server.
     */
    public void sendMessage(String text) {
        System.out.println("sending message to server...");
        sendToServer("DATA "+username+": "+text);
    }

    /**
     * Sends protocol messages to the Chat server
     * @param protocolMessage message that complies to Chat System's protocol format.
     */
    private synchronized void sendToServer(String protocolMessage){
        outputPrinter.println(protocolMessage);
    }

    /**
     * sends QUIT message to the Server and calls the method that closes the Socket
     */
    public void sendQuit() {
        sendToServer("QUIT");
        closeConnection();
    }

    /**
     * closes the Socket and sets the connectionEstablishedFlag field to false.
     */
    public void closeConnection() {
        connectionEstablishedFlag = false;
        try {
            System.out.println("Closing socket for "+username+"...");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
