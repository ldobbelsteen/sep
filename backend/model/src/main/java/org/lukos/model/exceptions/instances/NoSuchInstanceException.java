package org.lukos.model.exceptions.instances;

/**
 * This exception is thrown when an instance cannot be found.
 *
 * @author Martijn van Andel (1251104)
 * @since 23-02-2022
 */
public class NoSuchInstanceException extends InstanceException {

    /**
     * Constructs a {@code NoSuchInstanceException}.
     */
    public NoSuchInstanceException() {
    }

    /**
     * Constructs a {@code NoSuchInstanceException} with a {@code message}.
     *
     * @param message the message
     */
    public NoSuchInstanceException(String message) {
        super(message);
    }
}
