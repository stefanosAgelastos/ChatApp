package sample.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class loads the Chat System's Server application.
 * A ServerSocket is instantiated and then a loop waits for Sockets to connect.
 * The ServerSocket generates a Socket for each new connection and calls handleNewClient()
 * The field room holds a ChatRoom instance. The reference to the ChatRoom is passed to the ClientHandlers
 * can connect to the same ChatRoom instance.
 */
public class ServerMain {


    private static ServerSocket server;
    private static ChatRoom room = new ChatRoom();
    private static final int PORT = 1235;


    /**
     * application's main method
     * @param args
     */
    public static void main(String[] args){
        System.out.println("Server up!");
        try {
            InetAddress serverIP = InetAddress.getLocalHost();
            System.out.println("Server's IP and PORT is "+serverIP+":"+PORT);
            server = new ServerSocket(PORT);
            do{
                Socket socket = server.accept();
                System.out.println("new socket created");
                handleNewClient(socket);
            } while(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Instantiates a new ClientHandler object, and passes it a reference to the ChatRoom field and the Socket that
     * represents the new Client.
     * @param socket The Socket of the new client.
     */
    private static void handleNewClient(Socket socket) {
        new ClientHandler(socket, room);
    }
}
