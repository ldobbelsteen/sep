package org.lukos.model.exceptions.actionsystem;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.exceptions.GameException;

/**
 * General action exception that gets thrown when an error occurs regarding a {@link Action}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public abstract class ActionException extends GameException {

    /**
     * Constructs a {@code ActionException}.
     */
    public ActionException() {
    }

    /**
     * Constructs a {@code ActionException} with a {@code message}.
     *
     * @param message the message
     */
    public ActionException(String message) {
        super(message);
    }
}
