package org.lukos.controller.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link AlphaWolfKillActionRequest}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class AlphaWolfKillActionRequestTest {

    /** @utp.description Tests whether the constructor behaves as intended. */
    @Test
    public void constructorTest() {
        try {
            new AlphaWolfKillActionRequest();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the function {@code getBridgeOrPlayerID()} sets the ID. */
    @Test
    public void getBridgeOrPlayerIDTest() {
        AlphaWolfKillActionRequest request = new AlphaWolfKillActionRequest();
        request.setBridgeOrPlayerID(1);
        assertEquals(1, request.getBridgeOrPlayerID());
        request.setBridgeOrPlayerID(66);
        assertEquals(66, request.getBridgeOrPlayerID());
    }

    /** @utp.description Tests whether the function {@code setBridgeOrPlayerID()} gets the ID. */
    @Test
    public void setBridgeOrPlayerIDTest() {
        AlphaWolfKillActionRequest request = new AlphaWolfKillActionRequest();
        request.setBridgeOrPlayerID(-10);
        assertEquals(-10, request.getBridgeOrPlayerID());
        request.setBridgeOrPlayerID(666);
        assertEquals(666, request.getBridgeOrPlayerID());
    }

    /** @utp.description Tests whether the function {@code isPlayer()} sets the ID. */
    @Test
    public void isPlayerTest() {
        AlphaWolfKillActionRequest request = new AlphaWolfKillActionRequest();
        request.setPlayer(true);
        assertTrue(request.isPlayer());
    }

    /** @utp.description Tests whether the function {@code setPlayer()} sets the ID. */
    @Test
    public void setPlayerTest() {
        AlphaWolfKillActionRequest request = new AlphaWolfKillActionRequest();
        request.setPlayer(false);
        assertFalse(request.isPlayer());
    }
}
