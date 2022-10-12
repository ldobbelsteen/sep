package org.lukos.model.exceptions.instances;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link NotEnoughRolesException}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public class NotEnoughRolesExceptionTest {

    /**
     * Basic test with empty constructor.
     *
     * @utp.description Tests whether the constructor with no parameters shows intended behaviour.
     */
    @Test
    public void testEmptyConstructor() {
        Class<?> expected = NotEnoughRolesException.class;
        try {
            throw new NotEnoughRolesException();
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNull(e.getMessage(), "Message should be null.");
        }
    }

    /**
     * Basic test with constructor.
     *
     * @utp.description Tests whether the constructor with parameters shows intended behaviour.
     */
    @Test
    public void testMessageConstructor() {
        Class<?> expected = NotEnoughRolesException.class;
        try {
            throw new NotEnoughRolesException("Message");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }
}
