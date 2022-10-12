package org.lukos.model.location.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.location.House;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link Repaired}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 24-02-2022
 */
public class RepairedTest extends GameTest {

    /** Test Instance */
    private House instance;

    /**
     * Sets up a {@link House} to test the state {@link Repaired} on.
     */
    @BeforeEach
    public void setUp() {
        try {
            User user1 = UserManager.getInstance().createUser(new IssuerSub("RepairedTest", "User1"), "User1");
            user1.createGame("RepairedTestInstance", 1);
            PlayerIdentifier playerID = user1.getPlayer().getPlayerIdentifier();
            instance = new House(playerID, Repaired.getInstance());
            assertEquals(0, this.instance.getStateDay(), "stateDay should be 0");
            assertEquals(Repaired.getInstance(), this.instance.getState(), "State should be Repaired");
        } catch (SQLException | GameException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether only 1 instance of {@link Repaired} exist */
    @Test
    public void testInstance() {
        assertEquals(Repaired.getInstance(), Repaired.getInstance());
    }

    /** @utp.description Tests whether the function {@code soak()} functions as expected. */
    @Test
    public void testSoak() {
        try {
            try {
                this.instance.soak();
            } catch (WrongStateMethodException e) {
                fail("soak shouldn't throw an error.");
            }

            assertEquals(0, this.instance.getStateDay(), "stateDay should be 0");
            assertEquals(Soaked.getInstance(), this.instance.getState(), "State should be Soaked");
        } catch (SQLException | GameException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the function {@code burn()} functions as expected. */
    @Test
    public void testBurned() {
        try {
            this.instance.burn();
            fail("Should have thrown an error.");
        } catch (WrongStateMethodException e) {
            assertNotNull(e.getMessage(), "Message should not be null.");
        } catch (Exception e) {
            fail("type: " + e.getClass().getName() + " should have be instance of " +
                    WrongStateMethodException.class.getName());
        }
    }

    /** @utp.description Tests whether the function {@code build()} functions as expected. */
    @Test
    public void testBuild() {
        try {
            this.instance.build();
            fail("Should have thrown an error.");
        } catch (WrongStateMethodException e) {
            assertNotNull(e.getMessage(), "Message should not be null.");
        } catch (Exception e) {
            fail("type: " + e.getClass().getName() + " should have be instance of " +
                    WrongStateMethodException.class.getName());
        }
    }

    /** @utp.description Tests whether the function {@code repaired()} functions as expected. */
    @Test
    public void testRepaired() {
        try {
            try {
                this.instance.repaired();
            } catch (WrongStateMethodException e) {
                fail("repaired() shouldn't throw an error.");
            }

            assertEquals(0, this.instance.getStateDay(), "stateDay should be 0");
            assertEquals(Repaired.getInstance(), this.instance.getState(), "State should be Repaired");
        } catch (SQLException | GameException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
