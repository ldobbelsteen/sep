package org.lukos.model.listeners;

import org.lukos.model.events.NextPhaseEvent;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.Group;

import java.sql.SQLException;

/**
 * Listener for when an {@link org.lukos.model.events.WinEvent} gets triggered.
 *
 * @author Martijn van Andel (1251104)
 * @since 04-04-2022
 */
public interface WinEventListener {
    /**
     * Listener function that will be triggered by a {@link org.lukos.model.events.WinEvent}.
     *
     * @param gid The game which changed phase
     * @throws SQLException when an SQL exception occurs
     * @throws GameException when an exception in the game logic occurs
     */
    void win(int gid, Group winGroup) throws SQLException, GameException, ReflectiveOperationException;
}
