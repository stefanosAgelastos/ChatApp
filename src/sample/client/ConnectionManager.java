package sample.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

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

    public ConnectionManager(Controller controller) {
        this.controller = controller;
    }


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

    public boolean attemptLogin(String candidateUsername){
        if(connectionEstablishedFlag){
            System.out.println("Connection established.");
            outputPrinter.println("JOIN "+candidateUsername+", "+serverIP+":"+serverPORT );
        }
        String answer = inputScanner.nextLine();
        System.out.println(answer); //TODO remove after testing
        if(answer.equalsIgnoreCase("J_OK")){ //TODO implement this in a Class?
            this.username = candidateUsername;
            startReceivingThread();
            startIMAVThread();
            return true;
        }
        return false;
    }

    private void startIMAVThread() {
        Thread imav = new Thread(()->{
            while(connectionEstablishedFlag){
                sendToServer("IMAV");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        imav.start();
    }

    private void startReceivingThread() {
        //TODO filter the messages recieved. errors, list etc..
        readInput = new Thread(()->{
            while(connectionEstablishedFlag) {
                if (inputScanner.hasNext()) {
                    String message = inputScanner.nextLine();
                    controller.chatArea.appendText(message + "\n"); //TODO make it call a method in controller
                }
            }
        });
        readInput.start();

    }

    public void sendMessage(String text) {
        System.out.println("sending message to server...");
        sendToServer("DATA "+username+": "+text);
    }

    private synchronized void sendToServer(String protocolMessage){
        outputPrinter.println(protocolMessage);
    }

    public void sendQuit() {
        sendToServer("QUIT");
        closeConnection();
    }

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
