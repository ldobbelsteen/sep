package org.lukos.model.exceptions.user;

/**
 * This exception is thrown when a player cannot be found.
 *
 * @author Martijn van Andel (1251104)
 * @since 23-02-2022
 */
public class NoSuchPlayerException extends UserException {

    /**
     * Constructs a {@code NoSuchPlayerException}.
     */
    public NoSuchPlayerException() {
    }

    /**
     * Constructs a {@code NoSuchPlayerException} with a {@code message}.
     *
     * @param message the message
     */
    public NoSuchPlayerException(String message) {
        super(message);
    }
}
