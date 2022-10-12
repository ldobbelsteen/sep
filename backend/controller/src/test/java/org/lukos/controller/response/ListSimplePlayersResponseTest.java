package org.lukos.controller.response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link ListSimplePlayersResponse}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class ListSimplePlayersResponseTest {

    /** @utp.description Tests whether the constructor initializes the variables correctly. */
    @Test
    public void constructorTest() {
        ListSimplePlayersResponse response = new ListSimplePlayersResponse(new ArrayList<>());
        assertEquals(new ArrayList<>(), response.getPlayers(), "PlayerGroup should be empty");
        assertNull(response.getMessage(), "Message should be null");
    }

    /** @utp.description Tests whether the function {@code getPlayers()} returns the right value. */
    @Test
    public void getPlayersTest() {
        ListSimplePlayersResponse response1 = new ListSimplePlayersResponse(new ArrayList<>());
        assertEquals(new ArrayList<>(), response1.getPlayers(), "PlayerGroup should be empty");

        List<SimplePlayerEntry> simplePlayerEntries = new ArrayList<>();
        simplePlayerEntries.add(new SimplePlayerEntry(1, PlayerStatus.ALIVE, "Henk"));
        simplePlayerEntries.add(new SimplePlayerEntry(2, PlayerStatus.DECEASED, "Jan"));
        ListSimplePlayersResponse response2 = new ListSimplePlayersResponse(simplePlayerEntries);
        assertEquals(2, response2.getPlayers().size(), "Players size should be 2");

        List<SimplePlayerEntry> entries = response2.getPlayers();
        SimplePlayerEntry entry1 = entries.get(0);
        assertEquals(1, entry1.id(), "ID should be 1");
        assertEquals(PlayerStatus.ALIVE, entry1.playerStatus(), "Status should be Alive");
        assertEquals("Henk", entry1.name(), "Name should be Henk");

        SimplePlayerEntry entry2 = entries.get(1);
        assertEquals(2, entry2.id(), "ID should be 2");
        assertEquals(PlayerStatus.DECEASED, entry2.playerStatus(), "Status should be Deceased");
        assertEquals("Jan", entry2.name(), "Name should be Jan");
    }
}
