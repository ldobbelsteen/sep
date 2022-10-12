package org.lukos.model.exceptions.actionsystem;

/**
 * Exception thrown when an action is invalid or the caller does not have permission to execute the action.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 23-03-2022
 */
public class InvalidActionException extends ActionException {

    /**
     * Constructs a {@code InvalidActionException}.
     */
    public InvalidActionException() {
    }

    /**
     * Constructs a {@code InvalidActionException} with a {@code message}.
     *
     * @param message the message
     */
    public InvalidActionException(String message) {
        super(message);
    }
}

