package org.lukos.model.exceptions.instances;

/**
 * This exception is thrown when startGame is issued but there are not enough players in the instance.
 *
 * @author Martijn van Andel (1251104)
 * @since 23-02-2022
 */
public class NotEnoughPlayersException extends InstanceException {

    /**
     * Constructs a {@code NotEnoughPlayersException}.
     */
    public NotEnoughPlayersException() {
    }

    /**
     * Constructs a {@code NotEnoughPlayersException} with a {@code message}.
     *
     * @param message the message
     */
    public NotEnoughPlayersException(String message) {
        super(message);
    }
}
