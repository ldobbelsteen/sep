package org.lukos.controller.response;

/**
 * The response datatype that sends information about a user's profile.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class ProfileResponse {
    /** The username of the response. */
    private final String username;

    /**
     * Constructor for responses of {@code ProfileResponse}.
     *
     * @param username the username of the response
     */
    public ProfileResponse(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the response.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
