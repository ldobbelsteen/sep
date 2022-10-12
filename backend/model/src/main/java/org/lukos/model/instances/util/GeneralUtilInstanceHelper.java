package org.lukos.model.instances.util;

import org.lukos.database.UserDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.actionsystem.InvalidActionException;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.util.RoleActions;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * This is a helper class for general things that the {@code Instance} has to do.
 *
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
abstract class GeneralUtilInstanceHelper extends ChatHelper {

    /**
     * Kills all the marked {@code Player}s.
     *
     * @param killer            the killer of the marked {@code Player}s
     * @param toBeExecuted      the list of IDs of {@code Player}s that are to be executed
     * @param killMarkedPlayers the enum value of {@code KillMarkedPlayers}
     * @throws InvalidActionException when the given enum {@code KillMarkedPlayers} is invalid
     * @throws SQLException           when a database operation fails
     */
    public static void killMarkedPlayers(PlayerIdentifier killer, ArrayList<PlayerIdentifier> toBeExecuted,
                                         KillMarkedPlayers killMarkedPlayers)
            throws InvalidActionException, SQLException {
        RoleActions action;
        switch (killMarkedPlayers) {
            case NIGHT -> action = RoleActions.KILL_MARKED_PLAYERS_NIGHT;
            case LYNCH -> action = RoleActions.KILL_MARKED_PLAYERS_LYNCH;
            default -> throw new InvalidActionException("That action does not exist.");
        }

        addAction(Instant.now().plus(10, ChronoUnit.MINUTES), action.getAction(),
                createNewPreActionDT(killer, createNewActionEnc(new ArrayList<>(), toBeExecuted)));
    }

    /**
     * Adds a win or loss to all {@code Player}s in the given list based on the {@code Group} their in and the winning
     * {@code Group}.
     *
     * @param playerList the list of {@code Player}s
     * @param winner     the {@code Group of the winner}
     * @throws GameException                when an exception occurs in the game-logic
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void addWinOrLossPerRole(ArrayList<Player> playerList, Group winner)
            throws ReflectiveOperationException, SQLException, GameException {
        /* Add win/loss to UserStats. */
        for (Player player : playerList) {
            MainRole mainRole = player.getMainRole();
            UserDB.incrementGamesPlayedByUserID(player.getPlayerIdentifier().userID(),
                    mainRole.getClass().getSimpleName(), mainRole.getGroup() == winner);
        }
    }

    /**
     * Enumeration to determine on which time of day the kill marked players is being called.
     *
     * @author Rick van der Heijden (1461923)
     * @since 09-04-2022
     */
    public enum KillMarkedPlayers {
        NIGHT,
        LYNCH
    }
}
