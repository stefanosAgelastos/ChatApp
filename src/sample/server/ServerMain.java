package sample.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerMain {


    private static ServerSocket server;
    private static ChatRoom room = new ChatRoom();
    private static final int PORT = 1235;

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
    private static void handleNewClient(Socket socket) {
        new ClientHandler(socket, room);
    }
}
