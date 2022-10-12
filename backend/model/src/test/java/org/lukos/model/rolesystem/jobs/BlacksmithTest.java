package org.lukos.model.rolesystem.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.rolesystem.Job;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link Blacksmith}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 12-04-2022
 */
public class BlacksmithTest {
    /** An instance of {@link Blacksmith} that can be used throughout the test cases. */
    private Job blacksmith;

    @BeforeEach
    public void setUp() {
        this.blacksmith = new Blacksmith();
    }

    /** @utp.description Tests whether the constructor is demonstrating the correct functionality */
    @Test
    public void constructorTest1() {
        assertEquals(CharacterType.SHADY, blacksmith.getCharacter());
    }

    /**
     * @utp.description Tests whether the {@code initializeActions()} function is demonstrating the correct
     * functionality.
     */
    @Test
    public void initializeActionsTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            blacksmith.initializeActions(null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code performAction()} function is demonstrating the correct functionality.
     */
    @Test
    public void performActionTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            blacksmith.performAction(null, null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation()} function is demonstrating the correct functionality.
     */
    @Test
    public void getInformationTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            blacksmith.getInformation(null, null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code getActions()} function is demonstrating the correct functionality.
     */
    @Test
    public void getActionsTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            blacksmith.getActions();
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
            blacksmith.replenishAction(-1, null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }
}
