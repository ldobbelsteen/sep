package org.lukos.controller.response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link ListObituaryResponse}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class ListObituaryResponseTest {

    /** @utp.description Tests whether the constructor initializes the variables correctly. */
    @Test
    public void constructorTest() {
        ListObituaryResponse response = new ListObituaryResponse(new ArrayList<>());
        assertEquals(new ArrayList<>(), response.getPlayers(), "PlayerGroup should be empty");
        assertNull(response.getMessage(), "Message should be null");
    }

    /** @utp.description Tests whether the function {@code getPlayers()} returns the right value. */
    @Test
    public void getPlayersTest() {
        ListObituaryResponse response1 = new ListObituaryResponse(new ArrayList<>());
        assertEquals(new ArrayList<>(), response1.getPlayers(), "PlayerGroup should be empty");

        List<ObituaryPlayerEntry> simplePlayerEntries = new ArrayList<>();
        simplePlayerEntries.add(new ObituaryPlayerEntry(1, new ObituaryEntry(new ArrayList<>(), "")));
        ListObituaryResponse response2 = new ListObituaryResponse(simplePlayerEntries);
        assertEquals(1, response2.getPlayers().size(), "Players size should be 1");

        List<ObituaryPlayerEntry> entries = response2.getPlayers();
        ObituaryPlayerEntry entry1 = entries.get(0);
        assertEquals(1, entry1.id(), "ID should be 1");
        assertEquals(PlayerStatus.DECEASED, entry1.getPlayerStatus(), "PlayerStatus should be DECEASED.");
        ObituaryEntry obituaryEntry = entry1.obituaryEntry();
        assertEquals(new ArrayList<>(), obituaryEntry.roles(), "Roles should be empty");
        assertEquals("", obituaryEntry.deathNote(), "DeathNote should be empty");
    }
}
