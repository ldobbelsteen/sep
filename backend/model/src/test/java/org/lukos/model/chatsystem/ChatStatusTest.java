package org.lukos.model.chatsystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link ChatStatus}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 15-04-2022
 */
public class ChatStatusTest {

    /** @utp.description Tests whether the {@code equals} function is comparing identifiers correctly. */
    @Test
    public void equalsTest() {
        int id = 1;
        ChatType type = ChatType.GOSSIP;
        boolean isOpen = true;
        ChatStatus status = new ChatStatus(1, type, isOpen);
        assertTrue(status.equals(new ChatStatus(id, type, isOpen)));
        assertFalse(status.equals(new ChatStatus(id + 1, type, isOpen)));
        assertFalse(status.equals(new ChatStatus(id, ChatType.CULT, isOpen)));
        assertFalse(status.equals(new ChatStatus(id, type, !isOpen)));
        assertFalse(status.equals(new ChatIdentifier(id, type)));
    }
}
