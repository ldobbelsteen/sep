package org.lukos.model.exceptions.location;

/**
 * Exception for when a {@code Bridge} does not exist.
 *
 * @author Rick van der Heijden (1461923)
 * @since 28-03-2022
 */
public class BridgeDoesNotExistException extends LocationException {

    /**
     * Constructs a {@code BridgeDoesNotExistException}.
     */
    public BridgeDoesNotExistException() {
    }

    /**
     * Constructs a {@code BridgeDoesNotExistException} with a {@code message}.
     *
     * @param message the message
     */
    public BridgeDoesNotExistException(String message) {
        super(message);
    }
}
