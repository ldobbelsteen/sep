package org.lukos.model.exceptions.instances;

/**
 * This exception is thrown when assignRoles is executed but the number of roles that are distributed are less than the
 * number of players to receive a role.
 *
 * @author Martijn van Andel (1251104)
 * @since 23-02-2022
 */
public class NotEnoughRolesException extends InstanceException {

    /**
     * Constructs a {@code NotEnoughRolesException}.
     */
    public NotEnoughRolesException() {
    }

    /**
     * Constructs a {@code NotEnoughRolesException} with a {@code message}.
     *
     * @param message the message
     */
    public NotEnoughRolesException(String message) {
        super(message);
    }
}
