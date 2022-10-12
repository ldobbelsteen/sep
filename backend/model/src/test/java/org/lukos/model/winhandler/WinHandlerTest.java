package org.lukos.model.winhandler;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.winhandler.CutOffChainException;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.roles.doubleroles.Lover;
import org.lukos.model.rolesystem.roles.mainroles.Townsperson;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code WinHandler}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-03-2022
 */
public abstract class WinHandlerTest extends GameTest {
    /** Test instance. */
    protected WinHandler instance;

    /**
     * Creates a new instance of {@code WinHandler}.
     *
     * @return The new instance.
     */
    protected abstract WinHandler createNewInstance();

    /**
     * Creates a new instance of {@code WinHandler} with a next {@code WinHandler}.
     *
     * @param next The next {@code WinHandler}.
     * @return The new instance.
     */
    protected abstract WinHandler createNewInstance(WinHandler next);

    @BeforeEach
    public void setup() {
        // Assumes Group.values() is not empty
        instance = createNewInstance();
    }

    /**
     * Tests the single parameter constructor
     *
     * @utp.description Tests whether the constructor with 1 parameter behaves as intended.
     */
    @Test
    public void testConstructorSingleParameter() {
        // Assumes Group.values() is not empty
        Group group1 = Group.values()[0];
        Group group2 = Group.values()[Group.values().length - 1];

        WinHandler handler1 = new WinHandlerImpl(group1);
        assertEquals(group1, handler1.getGroup(), "Handler 1 should have group 1.");
        assertNull(handler1.getNext(), "Handler 1 should have next null.");

        WinHandler handler2 = new WinHandlerImpl(group2);
        assertEquals(group2, handler2.getGroup(), "Handler 2 should have group 2.");
        assertNull(handler2.getNext(), "Handler 2 should have next null.");
        assertEquals(group1, handler1.getGroup(), "Handler 1 should still have group 1.");
        assertNull(handler1.getNext(), "Handler 1 should still have next null.");
        assertNotEquals(handler1, handler2, ("Handler 1 and handler 2 should not be equal."));
    }

    /**
     * Tests the multi parameter constructor
     *
     * @utp.description Tests whether the constructor with multiple parameters behaves as intended.
     */
    @Test
    public void testConstructorMultiParameter() {
        // Assumes Group.values() is not empty
        Group group1 = Group.values()[0];
        Group group2 = Group.values()[Group.values().length - 1];

        WinHandler dummy = new WinHandlerImpl(group1);
        assertNull(dummy.getNext(), "Dummy should have next null. (check 1)");

        WinHandler handler1 = new WinHandlerImpl(group1, dummy);
        assertEquals(group1, handler1.getGroup(), "Handler 1 should have group 1.");
        assertEquals(dummy, handler1.getNext(), "Handler 1 should have dummy as next.");
        assertNull(dummy.getNext(), "Dummy should have next null. (check 2)");

        WinHandler handler2 = new WinHandlerImpl(group2, handler1);
        assertEquals(group1, handler1.getGroup(), "Handler 1 should still have group 1.");
        assertEquals(dummy, handler1.getNext(), "Handler 1 should still have dummy as next.");
        assertEquals(group2, handler2.getGroup(), "Handler 2 should have group 2.");
        assertEquals(handler1, handler2.getNext(), "Handler 2 should have handler 1 as next.");
        assertNull(dummy.getNext(), "Dummy should have next null. (check 3)");

        assertNotEquals(dummy, handler1, ("Dummy and handler 1 should not be equal."));
        assertNotEquals(dummy, handler2, ("Dummy and handler 2 should not be equal."));
        assertNotEquals(handler1, handler2, ("Handler 1 and handler 2 should not be equal."));
    }

    /**
     * Tests the single parameter constructor
     *
     * @utp.description Tests whether the constructor with 1 parameter behaves as intended.
     */
    @Test
    public void testInstanceConstructorNoParameter() {
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertNull(instance.getNext(), "Next should be null.");
    }

    /**
     * Tests the multi parameter constructor
     *
     * @utp.description Tests whether the constructor with multiple parameters behaves as intended.
     */
    @Test
    public void testInstanceConstructorSingleParameter() {
        WinHandler instance = createNewInstance(this.instance);
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertNotNull(instance.getNext(), "Next should not be null.");
        assertEquals(this.instance, instance.getNext(), "Instance should have this.instance as next.");
    }

    /**
     * Tests the {@code setNext()} method
     *
     * @utp.description Tests whether the {@code setNext()} method behaves as intended.
     */
    @Test
    public void testSetNext() {
        // Assumes Group.values() is not empty
        Group dummyGroup = Group.values()[0];
        WinHandler dummyHandler = new WinHandlerImpl(dummyGroup);

        assertNull(instance.getNext(), "Instance next should be null.");
        try {
            instance.setNext(null);
            instance.setNext(dummyHandler);
        } catch (CutOffChainException e) {
            fail("Should not have thrown an exception.");
        }
        assertEquals(dummyHandler, instance.getNext(), "Instance next should be dummyHandler.");

        WinHandler handler = createNewInstance(instance);
        assertEquals(dummyHandler, instance.getNext(), "Instance next should still be dummyHandler.");
        assertEquals(instance, handler.getNext(), "Handler next should be instance");
        assertEquals(dummyHandler, handler.getNext().getNext(), "Handler next next should be dummyHandler.");
        assertNull(handler.getNext().getNext().getNext(), "Handler next next next should be null.");
    }

    /**
     * Tests the {@code setNext()} method for exceptions that should be thrown
     *
     * @utp.description Tests whether the {@code setNext()} method throws an error when someone tries to cut off the
     * handler chain.
     */
    @Test
    public void testSetNextException() {
        try {
            Group dummyGroup = Group.values()[0];
            WinHandler dummy1 = new WinHandlerImpl(dummyGroup);
            WinHandler dummy2 = new WinHandlerImpl(dummyGroup);
            dummy1.setNext(dummy2);
            this.instance.setNext(dummy1);
            this.instance.setNext(dummy2);
            this.instance.setNext(dummy1);
            this.instance.setNext(null);
            fail("Should have thrown an error.");
        } catch (CutOffChainException e) {
            assertNotNull(e.getMessage(), "Message should not be null.");
        } catch (Exception e) {
            fail("type: " + e.getClass().getName() + " should have be instance of " +
                    NullPointerException.class.getName());
        }
    }

    /**
     * Tests the {@code checkWin()} method.
     *
     * @utp.description Tests whether the function {@code checkWin()} behaves as intended.
     */
    @Test
    public void testCheckWin() {
        try {
            // Assumes Group.values() is not empty
            Group dummyGroup = Group.values()[0];
            WinHandlerImpl handler1 = new WinHandlerImpl(dummyGroup);
            WinHandlerImpl handler2 = new WinHandlerImpl(dummyGroup, handler1);
            WinHandlerImpl handler3 = new WinHandlerImpl(dummyGroup, handler2);

            assertNotEquals(handler1, handler2, "Handler 1 and handler 2 should be different");
            assertNotEquals(handler1, handler3, "Handler 1 and handler 3 should be different");
            assertNotEquals(handler2, handler3, "Handler 2 and handler 3 should be different");

            assertEquals(handler1, handler2.getNext(), "Handler 2 should have handler 1 as next");
            assertEquals(handler2, handler3.getNext(), "Handler 3 should have handler 2 as next");

            assertNull(handler3.checkWin(new ArrayList<>()), "Checkwin should be null.");
            assertTrue(handler1.isPassed(), "Handler 1 should be passed.");
            assertTrue(handler2.isPassed(), "Handler 2 should be passed.");
            assertTrue(handler3.isPassed(), "Handler 3 should be passed.");

            handler1.setPassed(false);
            handler2.setPassed(false);
            handler3.setPassed(false);

            assertFalse(handler1.isPassed(), "Handler 1 shouldn't be passed.");
            assertFalse(handler2.isPassed(), "Handler 2 shouldn't be passed.");
            assertFalse(handler3.isPassed(), "Handler 3 shouldn't be passed.");

            assertNull(handler2.checkWin(new ArrayList<>()), "Checkwin should be null. (second time)");
            assertFalse(handler3.isPassed(), "Handler 3 should not be passed.");
            assertTrue(handler2.isPassed(), "Handler 2 should still be passed.");
            assertTrue(handler1.isPassed(), "Handler 1 should still be passed.");

            handler1.setPassed(false);
            handler2.setPassed(false);
            handler3.setPassed(false);

            assertFalse(handler1.isPassed(), "Handler 1 shouldn't be passed again.");
            assertFalse(handler2.isPassed(), "Handler 2 shouldn't be passed again.");
            assertFalse(handler3.isPassed(), "Handler 3 shouldn't be passed again.");

            assertNull(handler1.checkWin(new ArrayList<>()), "Checkwin should be null. (third time)");
            assertFalse(handler3.isPassed(), "Handler 3 should not be passed.");
            assertFalse(handler2.isPassed(), "Handler 2 should not be passed.");
            assertTrue(handler1.isPassed(), "Handler 1 should still be passed again.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not throw an error.");
        }
    }

    /** @utp.description Tests whether the function {@code listGroups()} returns the right groups. */
    @Test
    public void listGroupsTest() {
        try {
            Set<Group> groups = new HashSet<>();
            List<Player> players = new ArrayList<>();

            Player player = new PlayerImpl(1, 1);
            player.setMainRole(new Townsperson());
            player.addDoubleRole(new Lover(new ArrayList<>()));
            players.add(player);
            groups.add(Group.LOVERS);
            groups.add(Group.TOWNSPEOPLE);

            assertEquals(groups, WinHandler.listGroups(players));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    // Implementation of Player for testing purposes
    private static class PlayerImpl extends Player {
        private MainRole mainRole;
        private final ArrayList<DoubleRole> doubleRoles;

        public PlayerImpl(PlayerIdentifier playerIdentifier) {
            super(playerIdentifier);
            mainRole = null;
            doubleRoles = new ArrayList<>();
        }

        public PlayerImpl(int gameId, int userId) {
            this(new PlayerIdentifier(gameId, userId));
        }

        @Override
        public MainRole getMainRole() {
            return mainRole;
        }

        @Override
        public void setMainRole(MainRole mainRole) {
            this.mainRole = mainRole;
        }

        @Override
        public ArrayList<DoubleRole> getDoubleRoles() {
            return this.doubleRoles;
        }

        public void addDoubleRole(DoubleRole doubleRole) {
            this.doubleRoles.add(doubleRole);
        }
    }

    // Implementation of WinHandler for testing purposes
    private static class WinHandlerImpl extends WinHandler {
        @Getter
        @Setter
        private boolean passed = false; // Added to see whether the handler has been passed by the chain

        public WinHandlerImpl(Group group) {
            super(group);
        }

        public WinHandlerImpl(Group group, WinHandler next) {
            super(group, next);
        }

        @Override
        public Group checkWin(List<Player> alivePlayers)
                throws ReflectiveOperationException, SQLException, GameException {
            passed = true;
            return super.checkWin(alivePlayers);
        }
    }
}
