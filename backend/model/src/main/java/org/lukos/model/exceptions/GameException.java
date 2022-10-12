package org.lukos.model.exceptions;

/**
 * General game exception that gets thrown when there occurs a game error.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public abstract class GameException extends Exception {

    /**
     * Constructs a {@code GameException}.
     */
    public GameException() {
    }

    /**
     * Constructs a {@code GameException} with a {@code message}.
     *
     * @param message the message
     */
    public GameException(String message) {
        super(message);
    }
}
