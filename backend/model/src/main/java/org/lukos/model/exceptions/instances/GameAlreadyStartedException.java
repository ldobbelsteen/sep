package org.lukos.model.exceptions.instances;

/**
 * Exception thrown when performing an action that needs the game to not be started.
 *
 * @author Rick van der Heijden (1461923)
 * @since 29-03-2022
 */
public class GameAlreadyStartedException extends InstanceException {

    /**
     * Constructs a {@code GameAlreadyStartedException}.
     */
    public GameAlreadyStartedException() {
    }

    /**
     * Constructs a {@code GameAlreadyStartedException} with a {@code message}.
     *
     * @param message the message
     */
    public GameAlreadyStartedException(String message) {
        super(message);
    }
}
