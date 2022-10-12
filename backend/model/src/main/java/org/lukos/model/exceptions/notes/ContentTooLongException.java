package org.lukos.model.exceptions.notes;

/**
 * Exception for when the new content of a note is over the character limit.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 03-03-2022
 */
public class ContentTooLongException extends NoteException {

    /**
     * Constructs a {@code ContentTooLongException}.
     */
    public ContentTooLongException() {
    }

    /**
     * Constructs a {@code ContentTooLongException} with a {@code message}.
     *
     * @param message the message
     */
    public ContentTooLongException(String message) {
        super(message);
    }
}
