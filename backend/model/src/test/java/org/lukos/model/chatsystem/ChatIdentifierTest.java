package org.lukos.model.chatsystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for {@link ChatIdentifier}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 15-04-2022
 */
public class ChatIdentifierTest {

    /** @utp.description Tests whether the {@code equals} function is comparing identifiers correctly. */
    @Test
    public void equalsTest() {
        int id = 1;
        ChatType type = ChatType.GOSSIP;
        ChatIdentifier identifier = new ChatIdentifier(1, type);
        assertTrue(identifier.equals(new ChatIdentifier(id, type)));
        assertFalse(identifier.equals(new ChatIdentifier(id + 1, type)));
        assertFalse(identifier.equals(new ChatIdentifier(id, ChatType.CULT)));
        assertFalse(identifier.equals(new ChatStatus(id, type, false)));
    }
}
