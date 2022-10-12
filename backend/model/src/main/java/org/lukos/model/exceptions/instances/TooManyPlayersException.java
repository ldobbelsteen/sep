package org.lukos.model.exceptions.instances;

/**
 * This exception is thrown when startGame is issued but there are too many players in the instance.
 *
 * @author Martijn van Andel (1251104)
 * @since 23-02-2022
 */
public class TooManyPlayersException extends InstanceException {

    /**
     * Constructs a {@code TooManyPlayersException}.
     */
    public TooManyPlayersException() {
    }

    /**
     * Constructs a {@code TooManyPlayersException} with a {@code message}.
     *
     * @param message the message
     */
    public TooManyPlayersException(String message) {
        super(message);
    }
}
