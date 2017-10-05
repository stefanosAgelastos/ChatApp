package sample.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TestingClient {

    public static void main(String... args) throws IOException {
        String IP = "192.168.0.13";
        int PORT = 1235;

        InetAddress inetAddress = InetAddress.getByName(IP);
        Socket socket = new Socket(inetAddress, PORT);
        Scanner inputScanner = new Scanner(socket.getInputStream());
        PrintWriter outputPrinter = new PrintWriter(socket.getOutputStream(),true );

        Scanner userInput = new Scanner(System.in);
        Thread receive = new Thread(()->{
            while(inputScanner.hasNext()){
                System.out.println(inputScanner.nextLine());
            }
        });
        receive.start();
        while(userInput.hasNext()){
            outputPrinter.println(userInput.nextLine());
        }
    }
}
