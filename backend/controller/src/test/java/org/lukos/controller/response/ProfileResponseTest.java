package org.lukos.controller.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link ProfileResponse}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class ProfileResponseTest {

    /** @utp.description Tests whether the constructor initializes the variables correctly. */
    @Test
    public void constructorTest() {
        ProfileResponse response = new ProfileResponse("");
        assertEquals("", response.getUsername(), "Username should be empty");
    }

    /** @utp.description Tests whether the function {@code getUsername()} returns the right value. */
    @Test
    public void getUsernameTest() {
        String username1 = "Henk";
        ProfileResponse response1 = new ProfileResponse(username1);
        assertEquals(username1, response1.getUsername(), "Username should be the same");

        String username2 = "Jan";
        ProfileResponse response2 = new ProfileResponse(username2);
        assertEquals(username2, response2.getUsername(), "Username should be the same");
    }
}
