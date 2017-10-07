package sample.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * This class handles a Socket that has established a connection with the Server
 * and cooperates with the ChatRoom object in order to validate that the name is unique,
 * register the client to the ChatRoom,
 * forward messages to the client,
 *
 */
public class ClientHandler implements Runnable{
    private Thread thread;
    private Socket socket;
    private String name;
    private LocalTime lastHeartbeat = LocalTime.now();
    private Scanner in;
    private PrintWriter out;
    private ChatRoom room;

    /**
     * Constructor that sets the socket associated with the new chat client and the reference to the common ChatRoom.
     * It also starts the Thread that will handle the new client.
     * @param socket socket associated with the new chat client
     * @param room reference to the common ChatRoom
     */
    public ClientHandler(Socket socket, ChatRoom room) {
        this.socket = socket;
        this.room= room;
        thread= new Thread(this);
        thread.start();
    }

    /**
     * Set's up Scanner and PrintStream for the client.
     * Handles the clients messages until the end of the session.
     */
    @Override
    public void run() {
        //set up input and output streams
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        registerInChatRoom();
        handleConversationUntilQUIT();
        room.removeClient(this);
        killClient();
    }

    /**
     * this method runs until the client get's registered to the ChatRoom successfully.
     * it first check's that the message is type JOIN and that it has a valid format
     * then it checks if the username is unique by calling the boolean register() in ChatRoom.
     */
    private void registerInChatRoom() {
        while(in.hasNext()){
            ChatMessage message = new ChatMessage(in.nextLine());
            System.out.println(message.getMessage());
            if(message.isValid() && message.getType().equalsIgnoreCase("JOIN")){
                name = message.getClientName();
                if(room.register(this)){
                    break;
                }
                out.println(PROTOCOLMESSAGES.ERROR_NAME_NOT_UNIQUE);
            }else{
                out.println(PROTOCOLMESSAGES.ERROR_INVALID_MESSAGE);
            }
        }
    }

    /**
     * this method runs until it receives a QUIT message.
     * DATA messages get forwarded to the ChatRoom
     * IMAV messages set the lastHeartbeat field to the current server's time
     * any other messages get an J_ERR message as a response.
     */
    private void handleConversationUntilQUIT() {

        while(in.hasNext()){
            String messageRaw = in.nextLine();
            printInputToConsole(messageRaw); //DEBUGGING
            ChatMessage message = new ChatMessage(messageRaw);
            if(message.getType().equals("QUIT")){
                break;
            }else if(message.getType().equals("DATA")){
                //send to chatroom
                room.forwardMessageToRoom(message.getMessage());
            } else if(message.getType()=="IMAV"){
                lastHeartbeat = LocalTime.now();
            } else{
                out.println(PROTOCOLMESSAGES.ERROR_INVALID_MESSAGE);
            }
        }
    }

    /**
     * this method sends the passed String parameter directly to the client.
     * @param protocolMessage must be formatted according to protocol.
     */
    public void forwardMessageToClient(String protocolMessage) {
        out.println(protocolMessage);
        printOutputToConsole(protocolMessage);
    }

    /**
     * returns the username of the client. remember the username field is set during the registerInChatRoom().
     * @return the username of the client.
     */
    public String getName() {
        return name;
    }

    /**
     * calculates minutes past since the lastHeartbeat.
     * @return true if the difference is less than 2 minutes, and false if the difference is 2 minutes or longer.
     */
    public boolean isAlive(){
        int timeSinceLastAlive = LocalTime.now().getMinute()-lastHeartbeat.getMinute();
        System.out.println(name+"'s last IMAV was "+timeSinceLastAlive+" mins ago.");
        if(timeSinceLastAlive<2){
            return true;
        }
        return false;
    }

    /**
     * closes the socket. It stops all connection with the client.
     */
    public void killClient(){
        try {
            System.out.println("Closing socket...");
            socket.close();
            //TODO how can i close the whole instance of the ClientHandler?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //for testing purposes
    public ClientHandler(String name) {
        this.name = name;
    }

    //FOR DEBUGGING
    private void printInputToConsole(String message){
        System.out.println("INPUT FROM CLIENT:" + message);
    }

    //FOR DEBUGGING
    private void printOutputToConsole(String message){
        System.out.println("OUTPUT TO CLIENT:" + message);
    }

}
