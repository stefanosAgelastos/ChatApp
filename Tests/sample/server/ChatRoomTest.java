package sample.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatRoomTest {
    @Test
    public void register() throws Exception {
        ChatRoom room = new ChatRoom();
        assertTrue(room.register(new ClientHandler("John")));
        assertTrue(room.register(new ClientHandler("Alice")));
        assertFalse(room.register(new ClientHandler("john")));
    }

}