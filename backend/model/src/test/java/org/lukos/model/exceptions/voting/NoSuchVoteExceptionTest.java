package org.lukos.model.exceptions.voting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link NoSuchVoteException}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public class NoSuchVoteExceptionTest {

    /**
     * Basic test with empty constructor.
     *
     * @utp.description Tests whether the constructor with no parameters shows intended behaviour.
     */
    @Test
    public void testEmptyConstructor() {
        Class<?> expected = NoSuchVoteException.class;
        try {
            throw new NoSuchVoteException();
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
        Class<?> expected = NoSuchVoteException.class;
        try {
            throw new NoSuchVoteException("Message");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }
}
