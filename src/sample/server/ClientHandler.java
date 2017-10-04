package sample.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Scanner;

/**
 *
 */
public class ClientHandler implements Runnable{
    Thread thread;
    Socket socket;
    String name;
    LocalTime lastHeartbeat;
    Scanner in;
    PrintWriter out;
    ChatRoom room;


    public ClientHandler(Socket socket, ChatRoom room) {
        this.socket = socket;
        this.room= room;
        thread= new Thread(this);
        thread.start();
    }

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
        try {
            System.out.println("Closing socket...");
            room.removeClient(this);
            socket.close();
            //TODO how can i close the whole instance of the ClientHandler?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerInChatRoom() {
        ChatMessage message = new ChatMessage(in.nextLine());
        System.out.println(message.getMessage());
        boolean registerSuccessful= false;
        do{
            if(message.isValid() && message.getType().equalsIgnoreCase("JOIN")) {
                name= message.getClientName();
                if(room.register(this)){
                    registerSuccessful=true;
                }
            } else {
                out.println(ServerToClientMessages.ERROR_NAME_NOT_UNIQUE);
                message= new ChatMessage(in.nextLine());
            }
        }while(!registerSuccessful);
        //accept client
    }


    private void handleConversationUntilQUIT() {
        String messageRaw = in.nextLine();
        printInputToConsole(messageRaw); //DEBUGGING
        ChatMessage message = new ChatMessage(messageRaw);
        System.out.println("message received by clientHandler, processing...");
        while(!message.getType().equals("QUIT")){
            if(message.getType().equals("DATA")){
                //send to chatroom
                room.forwardMessageToRoom(message.getMessage());
            } else if(message.getType()=="IMAV"){
                lastHeartbeat = LocalTime.now();
            } else{
                out.println(ServerToClientMessages.ERROR_INVALID_MESSAGE);
            }
            messageRaw = in.nextLine();
            printInputToConsole(messageRaw); //DEBUGGING
            message = new ChatMessage(messageRaw);
        }
        System.out.println("Client decided to quit");
    }

    public void forwardMessageToClient(String message) {
        out.println(message);
        printOutputToConsole(message);
    }

    public String getName() {
        return name;
    }

    public boolean isAlive(){
        int timeSinceLastAlive = LocalTime.now().getMinute()-lastHeartbeat.getMinute();
        System.out.println(name+"'s last IMAV was "+timeSinceLastAlive+" mins ago.");
        if(timeSinceLastAlive<2){
            return true;
        }
        return false;
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
