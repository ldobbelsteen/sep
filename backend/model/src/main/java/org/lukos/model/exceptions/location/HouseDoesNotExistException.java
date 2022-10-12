package org.lukos.model.exceptions.location;

import org.lukos.model.location.House;

/**
 * Exception for when a {@link House} does not exist.
 *
 * @author Rick van der Heijden (1461923)
 * @since 28-03-2022
 */
public class HouseDoesNotExistException extends LocationException {

    /**
     * Constructs a {@code HouseDoesNotExistException}.
     */
    public HouseDoesNotExistException() {
    }

    /**
     * Constructs a {@code HouseDoesNotExistException} with a {@code message}.
     *
     * @param message the message
     */
    public HouseDoesNotExistException(String message) {
        super(message);
    }
}
