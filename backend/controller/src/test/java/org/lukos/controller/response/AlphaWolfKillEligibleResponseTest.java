package org.lukos.controller.response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link AlphaWolfKillEligibleResponse}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class AlphaWolfKillEligibleResponseTest {

    /** @utp.description Tests whether the constructor initializes the variables correctly. */
    @Test
    public void constructorTest() {
        AlphaWolfKillEligibleResponse response = new AlphaWolfKillEligibleResponse(new ArrayList<>());
        assertEquals(new ArrayList<>(), response.getEligible(), "Eligible should be empty");
        assertNull(response.getMessage(), "Message should be null");
    }

    /** @utp.description Tests whether the function {@code getEligible()} returns the right value. */
    @Test
    public void getEligibleTest() {
        AlphaWolfKillEligibleResponse response1 = new AlphaWolfKillEligibleResponse(new ArrayList<>());
        assertEquals(new ArrayList<>(), response1.getEligible(), "Eligible should be empty");

        List<IdEntry> ids = new ArrayList<>();
        ids.add(new IdEntry(1, true));
        ids.add(new IdEntry(2, false));
        AlphaWolfKillEligibleResponse response2 = new AlphaWolfKillEligibleResponse(ids);
        assertEquals(2, response2.getEligible().size(), "Eligible size should be 2");

        List<IdEntry> entries = response2.getEligible();
        IdEntry id1 = entries.get(0);
        assertEquals(1, id1.id(), "ID should be 1");
        assertTrue(id1.isPlayer(), "Should be player");
        IdEntry id2 = entries.get(1);
        assertEquals(2, id2.id(), "ID should be 2");
        assertFalse(id2.isPlayer(), "Should not be player");
    }
}
