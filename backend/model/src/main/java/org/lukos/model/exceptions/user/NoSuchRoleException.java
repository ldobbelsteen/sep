package org.lukos.model.exceptions.user;

/**
 * This exception is thrown when a player does not have the requested role.
 *
 * @author Rick van der Heijden (1461923)
 * @since 01-04-2022
 */
public class NoSuchRoleException extends UserException {

    /**
     * Constructs a {@code NoSuchRoleException}.
     */
    public NoSuchRoleException() {
    }

    /**
     * Constructs a {@code NoSuchRoleException} with a {@code message}.
     *
     * @param message the message
     */
    public NoSuchRoleException(String message) {
        super(message);
    }
}
