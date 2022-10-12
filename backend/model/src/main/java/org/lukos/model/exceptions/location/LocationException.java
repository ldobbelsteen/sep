package org.lukos.model.exceptions.location;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.location.Location;

/**
 * General location exception that gets thrown when an error occurs regarding a {@link Location}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public abstract class LocationException extends GameException {

    /**
     * Constructs a {@code LocationException}.
     */
    public LocationException() {
    }

    /**
     * Constructs a {@code LocationException} with a {@code message}.
     *
     * @param message the message
     */
    public LocationException(String message) {
        super(message);
    }
}
