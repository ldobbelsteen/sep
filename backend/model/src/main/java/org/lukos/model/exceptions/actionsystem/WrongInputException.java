package org.lukos.model.exceptions.actionsystem;

/**
 * Exception thrown when an action does not have the correct inputs.
 *
 * @author Marco Pleket (1295713)
 * @since 04-04-2022
 */
public class WrongInputException extends ActionException {

    /**
     * Constructs a {@code InvalidActionException}.
     */
    public WrongInputException() {
    }

    /**
     * Constructs a {@code InvalidActionException} with a {@code message}.
     *
     * @param message the message
     */
    public WrongInputException(String message) {
        super(message);
    }
}
