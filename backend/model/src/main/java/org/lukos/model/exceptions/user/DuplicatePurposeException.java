package org.lukos.model.exceptions.user;

/**
 * Exception for when you try to add a purpose to a player that already has that purpose.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 09-03-2022
 */
public class DuplicatePurposeException extends UserException {

    /**
     * Constructs a {@code DuplicatePurposeException}.
     */
    public DuplicatePurposeException() {
    }

    /**
     * Constructs a {@code DuplicatePurposeException} with a {@code message}.
     *
     * @param message the message
     */
    public DuplicatePurposeException(String message) {
        super(message);
    }

}
