package org.lukos.model.exceptions.user;

/**
 * This exception is thrown when a user already exists.
 *
 * @author Rick van der Heijden (1461923)
 * @since 08-03-2022
 */
public class UserAlreadyExistException extends UserException {

    /**
     * Constructs a {@code UserAlreadyExistException}.
     */
    public UserAlreadyExistException() {
    }

    /**
     * Constructs a {@code UserAlreadyExistException} with a {@code message}.
     *
     * @param message the message
     */
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
