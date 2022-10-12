package org.lukos.model.exceptions.actionsystem;

/**
 * Exception thrown when someone tried to perform an action that does not exist.
 *
 * @author Rick van der Heijden (1461923)
 * @since 01-04-2022
 */
public class NoSuchActionException extends ActionException {

    /**
     * Constructs a {@code NoSuchActionException}.
     */
    public NoSuchActionException() {
    }

    /**
     * Constructs a {@code NoSuchActionException} with a {@code message}.
     *
     * @param message the message
     */
    public NoSuchActionException(String message) {
        super(message);
    }
}
