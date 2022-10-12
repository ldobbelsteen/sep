package org.lukos.model.exceptions.location;

/**
 * This exception is thrown when a location cannot be found.
 *
 * @author Martijn van Andel (1251104)
 * @since 20-03-2022
 */
public class NoSuchLocationException extends LocationException {

    /**
     * Constructs a {@code NoSuchLocationException}.
     */
    public NoSuchLocationException() {
    }

    /**
     * Constructs a {@code GameException} with a {@code message}.
     *
     * @param message the message
     */
    public NoSuchLocationException(String message) {
        super(message);
    }
}
