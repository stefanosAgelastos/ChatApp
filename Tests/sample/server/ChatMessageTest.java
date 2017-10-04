package sample.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatMessageTest {

    @Test
    public void messageJOIN() throws Exception{
        ChatMessage message = new ChatMessage("JOINstefanos, ip:port");
        assertEquals(true, message.isValid());
        assertEquals("JOIN", message.getType());
        assertEquals("stefanos", message.getClientName());
    }
}