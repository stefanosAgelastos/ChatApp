package sample.server;

import java.util.ArrayList;

/**
 * This Class holds an ArrayList with all the active ClientHandlers.
 * It allows ClientHandlers to be added to the list when there is no other ClientHandler with the same name on the list.
 * The class can receive DATA messages form a ClientHandler and forward it to the rest ClientHandlers.
 * It also makes sure to remove any ClientHandler that hasn't received an IMAV message in the last two minutes.
 * For performance reasons there is separate ArrayList holding just the names as a String of each ClientHandler,
 * as to be able to iterate faster through the names.
 */
public class ChatRoom {

    private ArrayList<ClientHandler> activeClientHandlers = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    /**
     * this thread iterates through the activeClientHandlers, and removes any ClientHandler that is not alive.
     * it repeats the same operation every half a minute.
     */
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
                c.killClient();
            }
            if (!deadClientHandlers.isEmpty()) {
                forwardList();
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ChatRoom checked IMAV");
        }
    });

    /**
     * the constructor starts the checkIMAV thread.
     */
    public ChatRoom() {
        checkIMAV.start();
    }

    /**
     * this method receives a ClientHandler as a parameter, and then checks adds it to the activeClientHandlers, only
     * if there is no other ClientHandler with the same name field.
     * @param newClient ClientHandler that attempts to register
     * @return true if registration successful, false if other ClientHandler has the same value in the name field.
     */
    synchronized public boolean register(ClientHandler newClient) {
        for(ClientHandler c: activeClientHandlers){
            if(c.getName().equalsIgnoreCase(newClient.getName())){
                return false;
            }
        }
        newClient.forwardMessageToClient(PROTOCOLMESSAGES.J_OK);
        activeClientHandlers.add(newClient);
        list.add(newClient.getName());
        forwardList();
        return true;
    }

    /**
     * this methods sends the passed parameter String to all activeClientHandlers
     * @param message message must be in a valid protocol format.
     */
    synchronized public void forwardMessageToRoom(String message) {
        for(ClientHandler c: activeClientHandlers){
            c.forwardMessageToClient(message);
        }
    }

    /**
     * this method removes the ClientHandler from the activeClientHandlers field and it's name from the list of names.
     * it then forwards the list to remaining activeClientHandlers
     * @param clientToQuit the ClientHandler to be removed from the ChatRoom.
     */
    synchronized public void removeClient(ClientHandler clientToQuit) {
        activeClientHandlers.remove(clientToQuit);
        list.remove(clientToQuit.getName());
        forwardList();
    }

    /**
     * forwards to all activeClientHandlers, the LIST protocol message.
     */
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
