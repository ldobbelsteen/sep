package org.lukos.model.rolesystem.roles.mainroles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.Role;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link WerewolfCleaner}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 12-04-2022
 */
public class WerewolfCleanerTest {
    /** An instance of {@link WerewolfCleaner} that can be used throughout the test cases. */
    private Role werewolfCleaner;

    @BeforeEach
    public void setUp() {
        this.werewolfCleaner = new WerewolfCleaner();
    }

    /** @utp.description Tests whether the constructor is demonstrating the correct functionality */
    @Test
    public void constructorTest1() {
        assertEquals(CharacterType.SHADY, werewolfCleaner.getCharacter());
        assertEquals(Group.WEREWOLVES, werewolfCleaner.getGroup());
    }

    /** @utp.description Tests whether the constructor is demonstrating the correct functionality */
    @Test
    public void constructorTest2() {
        WerewolfCleaner werewolfCleaner = new WerewolfCleaner(true, true);
        assertEquals(CharacterType.SHADY, werewolfCleaner.getCharacter());
        assertEquals(Group.WEREWOLVES, werewolfCleaner.getGroup());
    }

    /**
     * @utp.description Tests whether the {@code initializeActions()} function is demonstrating the correct
     * functionality.
     */
    @Test
    public void initializeActionsTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            werewolfCleaner.initializeActions(null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code performAction()} function is demonstrating the correct
     * functionality.
     */
    @Test
    public void performActionTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            werewolfCleaner.performAction(null, null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation()} function is demonstrating the correct
     * functionality.
     */
    @Test
    public void getInformationTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            werewolfCleaner.getInformation(null, null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code getActions()} function is demonstrating the correct
     * functionality.
     */
    @Test
    public void getActionsTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            werewolfCleaner.getActions();
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code replenishAction()} function is demonstrating the correct
     * functionality.
     */
    @Test
    public void replenishActionTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            werewolfCleaner.replenishAction(-1, null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }
}
