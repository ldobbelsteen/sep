package org.lukos.model.exceptions.location;

/**
 * This exception will be thrown when a method has been called in {@code HouseState} that is not executable in the
 * current state.
 *
 * @author Rick van der Heijden (1461923)
 * @since 22-02-2022
 */
public class WrongStateMethodException extends LocationException {

    /**
     * Constructs a {@code WrongStateMethodException}.
     */
    public WrongStateMethodException() {
    }

    /**
     * Constructs a {@code WrongStateMethodException} with a {@code message}.
     *
     * @param message the message
     */
    public WrongStateMethodException(String message) {
        super(message);
    }
}
