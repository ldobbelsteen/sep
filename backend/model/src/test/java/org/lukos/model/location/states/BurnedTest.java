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
 * Test cases for {@link Burned}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 24-02-2022
 */
public class BurnedTest extends GameTest {

    /** Test Instance */
    private House instance;

    /**
     * Sets up a {@link House} to test the state {@link Burned} on.
     */
    @BeforeEach
    public void setUp() {
        try {
            User user1 = UserManager.getInstance().createUser(new IssuerSub("BurnedTest", "User1"), "User1");
            user1.createGame("BurnedTestInstance", 1);
            PlayerIdentifier playerID = user1.getPlayer().getPlayerIdentifier();
            instance = new House(playerID, Burned.getInstance());
            assertEquals(0, this.instance.getStateDay(), "stateDay should be 0");
            assertEquals(Burned.getInstance(), this.instance.getState(), "State should be Burned");
        } catch (SQLException | GameException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether only 1 instance of {@link Burned} exist */
    @Test
    public void testInstance() {
        assertEquals(Burned.getInstance(), Burned.getInstance());
    }

    /**
     * Tests the function {@code soak()}.
     *
     * @utp.description Tests whether the function {@code soak()} has the correct functionality.
     */
    @Test
    public void testSoak() {
        try {
            this.instance.soak();
            fail("Should have thrown an error.");
        } catch (WrongStateMethodException e) {
            assertNotNull(e.getMessage(), "Message should not be null.");
        } catch (Exception e) {
            fail("type: " + e.getClass().getName() + " should have be instance of " +
                    WrongStateMethodException.class.getName());
        }
    }

    /**
     * Tests the function {@code burned()}.
     *
     * @utp.description Tests whether the function {@code burn()} has the correct functionality.
     */
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

    /**
     * Tests the function {@code build()}.
     *
     * @utp.description Tests whether the function {@code build()} has the correct functionality.
     */
    @Test
    public void testBuild() {
        try {
            try {
                this.instance.build();
            } catch (WrongStateMethodException e) {
                fail("build() shouldn't throw an error.");
            }

            assertEquals(1, this.instance.getStateDay(), "stateDay should be 1");
            assertEquals(Burned.getInstance(), this.instance.getState(), "State should be Burned");

            try {
                this.instance.build();
            } catch (WrongStateMethodException e) {
                fail("build() shouldn't throw an error.");
            }

            assertEquals(2, this.instance.getStateDay(), "stateDay should be 2");
            assertEquals(Burned.getInstance(), this.instance.getState(), "State should be Burned");

            try {
                this.instance.build();
            } catch (WrongStateMethodException e) {
                fail("build() shouldn't throw an error.");
            }

            assertEquals(0, this.instance.getStateDay(), "stateDay should be 0");
            assertEquals(Repaired.getInstance(), this.instance.getState(), "State should be Repaired");
        } catch (SQLException | GameException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests the function {@code repaired()}.
     *
     * @utp.description Tests whether the function {@code repaired()} has the correct functionality.
     */
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
