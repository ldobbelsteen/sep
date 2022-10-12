package org.lukos.controller.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link BooleanResponse}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class BooleanResponseTest {

    /** @utp.description Tests whether the constructor initializes the variables correctly. */
    @Test
    public void constructorTest() {
        BooleanResponse response = new BooleanResponse(false);
        assertFalse(response.getOpen(), "Open should be false");
        assertNotNull(response.getMessage(), "Message should be not null");
    }

    /** @utp.description Tests whether the function {@code getOpen()} returns the right value. */
    @Test
    public void getOpenTest() {
        BooleanResponse booleanResponse1 = new BooleanResponse(false);
        assertFalse(booleanResponse1.getOpen(), "Open should be false");
        BooleanResponse booleanResponse2 = new BooleanResponse(true);
        assertTrue(booleanResponse2.getOpen(), "Open should be true");
    }
}
