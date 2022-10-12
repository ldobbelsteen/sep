package org.lukos.model.chatsystem;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for {@link ChatMessage}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 15-04-2022
 */
public class ChatMessageTest {

    /** @utp.description Tests whether the constructor performs the expected behaviour. */
    @Test
    public void constructorTest() {
        MessageEntry messageEntry = new MessageEntry(1, Instant.now(), "This is a test message.");
        ChatMessage chatMessage = new ChatMessage(3, messageEntry);

        assertEquals(messageEntry, chatMessage.message, "Message entry should be the same.");
        assertEquals(3, chatMessage.chatId, "ID should be the same.");
    }
}
