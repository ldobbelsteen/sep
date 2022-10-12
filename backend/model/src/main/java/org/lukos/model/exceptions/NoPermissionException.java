package org.lukos.model.exceptions;

/**
 * This exception is thrown when a user calls a function they have no access to.
 *
 * @author Martijn van Andel (1251104)
 * @since 22-02-2022
 */
public class NoPermissionException extends GameException {

    /**
     * Constructs a {@code NoPermissionException}.
     */
    public NoPermissionException() {
    }

    /**
     * Constructs a {@code NoPermissionsException} with a {@code message}.
     *
     * @param message the message
     */
    public NoPermissionException(String message) {
        super(message);
    }
}
