package org.lukos.model.exceptions.actionsystem;

/**
 * Exception thrown when someone tries to perform an action, who is not allowed to perform this action.
 *
 * @author Rick van der Heijden (1461923)
 * @since 30-03-2022
 */
public class NotAllowedToPerformActionException extends ActionException{

    /**
     * Constructs a {@code NotAllowedToPerformActionException}.
     */
    public NotAllowedToPerformActionException() {
    }

    /**
     * Constructs a {@code NotAllowedToPerformActionException} with a {@code message}.
     *
     * @param message the message
     */
    public NotAllowedToPerformActionException(String message) {
        super(message);
    }
}
