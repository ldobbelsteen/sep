package org.lukos.model.exceptions.instances;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link TooManyPlayersException}.
 *
 * @author Sander van Heesch (1436708)
 * @since 09-03-2022
 */
public class TooManyPlayersExceptionTest {

    /**
     * Basic test with empty constructor.
     *
     * @utp.description Tests whether the constructor with no parameters shows intended behaviour.
     */
    @Test
    public void testEmptyConstructor() {
        Class<?> expected = TooManyPlayersException.class;
        try {
            throw new TooManyPlayersException();
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
        Class<?> expected = TooManyPlayersException.class;
        try {
            throw new TooManyPlayersException("Message");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }
}
