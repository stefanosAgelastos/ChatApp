package sample.server;

import java.util.ArrayList;

public class ChatRoom {

    private ArrayList<ClientHandler> activeClientHandlers = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    Thread checkIMAV = new Thread(()->{
        while (true) {
            ArrayList<ClientHandler> deadClientHandlers = new ArrayList<>();
            for (ClientHandler c : activeClientHandlers) {
                if (!c.isAlive()) {
                    deadClientHandlers.add(c);
                }
            }
            for (ClientHandler c : deadClientHandlers) {
                list.remove(c.getName());
                activeClientHandlers.remove(c);
            }
            if (!deadClientHandlers.isEmpty()) {
                forwardList();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ChatRoom checked IMAV");
        }
    });

    public ChatRoom() {
        checkIMAV.start();
    }

    synchronized public boolean register(ClientHandler newClient) {
        for(ClientHandler c: activeClientHandlers){
            if(c.getName().equalsIgnoreCase(newClient.getName())){
                return false;
            }
            ServerMain a = new ServerMain();
        }
        newClient.forwardMessageToClient(ServerToClientMessages.J_OK);
        activeClientHandlers.add(newClient);
        list.add(newClient.getName());
        forwardList();
        return true;
    }

    //TODO should this be synchronised? probably yes.
    synchronized public void forwardMessageToRoom(String message) {
        for(ClientHandler c: activeClientHandlers){
            c.forwardMessageToClient(message);
        }
    }

    synchronized public void removeClient(ClientHandler clientToQuit) {
        activeClientHandlers.remove(clientToQuit);
        list.remove(clientToQuit.getName());
        forwardList();
    }

    private void forwardList() {
        String currentList = "LIST";
        for(String name: list){
            currentList = currentList + " " + name;
        }
        for(ClientHandler c: activeClientHandlers){
            c.forwardMessageToClient(currentList);
        }
    }
}
