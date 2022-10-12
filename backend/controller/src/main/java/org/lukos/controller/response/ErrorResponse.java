package org.lukos.controller.response;

/**
 * The response datatype for sending back that an error has occurred.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class ErrorResponse {
    /** The error message of the response. */
    private final String error;

    /**
     * Constructor for responses of {@code ErrorResponse}.
     *
     * @param error the error message
     */
    public ErrorResponse(String error) {
        this.error = error;
    }

    /**
     * Returns the error message of the response.
     *
     * @return the error message
     */
    public String getError() {
        return error;
    }
}
