package org.lukos.model.listeners;

import org.lukos.model.exceptions.GameException;

import java.sql.SQLException;

/**
 * Listener for when an {@code NextPhaseEvent} gets triggered.
 *
 * @author Rick van der Heijden (1461923)
 * @since 31-03-2022
 */
public interface NextPhaseListener {
    /**
     * Listener function that will be triggered by a {@code NextPhaseEvent}.
     *
     * @param gid The game which changed phase
     * @throws SQLException when an SQL exception occurs
     * @throws GameException when an exception in the game logic occurs
     */
    void nextPhaseUpdate(int gid) throws SQLException, GameException;
}
