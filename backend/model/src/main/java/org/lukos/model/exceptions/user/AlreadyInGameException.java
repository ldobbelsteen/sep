package org.lukos.model.exceptions.user;

/**
 * Exception for when a player tries to join or create a game, when they are already in a game.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 22-02-2022
 */
public class AlreadyInGameException extends UserException {

    /**
     * Constructs a {@code AlreadyInGameException}.
     */
    public AlreadyInGameException() {
    }

    /**
     * Constructs a {@code AlreadyInGameException} with a {@code message}.
     *
     * @param message the message
     */
    public AlreadyInGameException(String message) {
        super(message);
    }
}
