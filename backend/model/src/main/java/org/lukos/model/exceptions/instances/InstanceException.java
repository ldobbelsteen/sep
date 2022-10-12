package org.lukos.model.exceptions.instances;

import org.lukos.model.exceptions.GameException;

/**
 * General instance exception that gets thrown when an error occurs regarding a {@code Instance}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public abstract class InstanceException extends GameException {

    /**
     * Constructs a {@code InstanceException}.
     */
    public InstanceException() {
    }

    /**
     * Constructs a {@code InstanceException} with a {@code message}.
     *
     * @param message the message
     */
    public InstanceException(String message) {
        super(message);
    }
}
