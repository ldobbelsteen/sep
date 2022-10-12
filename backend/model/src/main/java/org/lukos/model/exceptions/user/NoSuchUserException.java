package org.lukos.model.exceptions.user;

/**
 * This exception is thrown when a user cannot be found.
 *
 * @author Rick van der Heijden (1461923)
 * @since 08-03-2022
 */
public class NoSuchUserException extends UserException {

    /**
     * Constructs a {@code NoSuchUserException}.
     */
    public NoSuchUserException() {
    }

    /**
     * Constructs a {@code NoSuchUserException} with a {@code message}.
     *
     * @param message the message
     */
    public NoSuchUserException(String message) {
        super(message);
    }
}
