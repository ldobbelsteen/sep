package org.lukos.model.exceptions.notes;

/**
 * Exception for when the user wants to change a note that cannot be modified.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 03-03-2022
 */
public class ModificationNotAllowedException extends NoteException {

    /**
     * Constructs a {@code ModificationNotAllowedException}.
     */
    public ModificationNotAllowedException() {
    }

    /**
     * Constructs a {@code ModificationNotAllowedException} with a {@code message}.
     *
     * @param message the message
     */
    public ModificationNotAllowedException(String message) {
        super(message);
    }
}
