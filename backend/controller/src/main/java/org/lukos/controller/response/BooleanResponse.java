package org.lukos.controller.response;

/**
 * The response datatype that sends whether something is open.
 *
 * @author Marco Pleket (1295713)
 * @since 01-04-2022
 */
public class BooleanResponse extends SuccessResponse {
    /** A boolean whether the something of the request is open or not. */
    private final boolean isOpen;

    /**
     * constructor for responses of {@code BooleanResponse}.
     *
     * @param isOpen whether the something is open
     */
    public BooleanResponse(boolean isOpen) {
        super("State obtained successfully");
        this.isOpen = isOpen;
    }

    /**
     * Returns whether the something of the response is open.
     *
     * @return whether the something of the response is open.
     */
    public boolean getOpen() {
        return isOpen;
    }
}
