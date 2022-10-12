package org.lukos.controller.response;

/**
 * The response datatype to indicate that the response was successful.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class SuccessResponse {
    /** The message of the response. */
    private final String message;

    /**
     * Constructor for responses of {@code SuccessResponse}.
     *
     * @param message the message of the response
     */
    public SuccessResponse(String message) {
        this.message = message;
    }

    /**
     * Returns the message of the response.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
