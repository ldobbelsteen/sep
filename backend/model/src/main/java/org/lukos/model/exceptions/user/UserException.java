package org.lukos.model.exceptions.user;

import org.lukos.model.exceptions.GameException;

/**
 * General user exception that gets thrown when an error occurs regarding the user system.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public abstract class UserException extends GameException {

    /**
     * Constructs a {@code UserException}.
     */
    public UserException() {
    }

    /**
     * Constructs a {@code UserException} with a {@code message}.
     *
     * @param message the message
     */
    public UserException(String message) {
        super(message);
    }
}
