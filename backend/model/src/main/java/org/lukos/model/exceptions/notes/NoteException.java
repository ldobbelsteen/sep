package org.lukos.model.exceptions.notes;

import org.lukos.model.exceptions.GameException;

/**
 * General note exception that gets thrown when there occurs an error regarding {@code INote}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public abstract class NoteException extends GameException {

    /**
     * Constructs a {@code NoteException}.
     */
    public NoteException() {

    }

    /**
     * Constructs a {@code NoteException} with a {@code message}.
     *
     * @param message the message
     */
    public NoteException(String message) {
        super(message);
    }
}
