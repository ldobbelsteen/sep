package org.lukos.model.actionsystem.actions.util;

import org.lukos.model.config.CharacterConfig;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.*;
import org.lukos.model.rolesystem.jobs.AlphaWolf;
import org.lukos.model.rolesystem.jobs.Blacksmith;
import org.lukos.model.rolesystem.jobs.Mayor;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a facade for the role system.
 *
 * @author Rick van der Heijden (1461923)
 * @since 08-04-2022
 */
class RoleHelper {

    /**
     * Private constructor to avoid instance methods.
     */
    private RoleHelper() {
    }

    /**
     * Returns whether a {@code Player} is an {@code AlphaWolf}.
     *
     * @param player the {@code Player}
     * @return whether a {@code Player} is an {@code AlphaWolf}
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static boolean isAlphaWolf(Player player) throws ReflectiveOperationException, SQLException {
        return player.getJobs().stream().anyMatch(job -> job instanceof AlphaWolf);
    }

    /**
     * Returns whether a {@code Player} is a wolf.
     *
     * @param player the {@code Player}
     * @return whether the {@code Player} is a wolf
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static boolean isWolf(Player player) throws ReflectiveOperationException, SQLException, GameException {
        return player.getMainRole().getGroup() == Group.WEREWOLVES;
    }

    /**
     * Returns the {@code CharacterType} of a {@code Player}.
     *
     * @param player the {@code Player}
     * @return the {@code CharacterType} of a {@code Player}
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static CharacterType getCharacterTypeByPlayer(Player player)
            throws ReflectiveOperationException, SQLException, GameException {
        List<Job> jobs = player.getJobs();
        if (!jobs.isEmpty()) {
            if (jobs.stream().anyMatch(job -> job instanceof Mayor)) {
                return CharacterConfig.MAYOR.getCharacter();
            } else if (jobs.stream().anyMatch(job -> job instanceof AlphaWolf)) {
                return CharacterConfig.ALPHA_WOLF.getCharacter();
            } else if (jobs.stream().anyMatch(job -> job instanceof Blacksmith)) {
                return CharacterConfig.BLACKSMITH.getCharacter();
            } else {
                return CharacterConfig.GATEKEEPER.getCharacter();
            }
        }
        List<DoubleRole> doubleRoles = player.getDoubleRoles();
        if (!doubleRoles.isEmpty()) {
            if (doubleRoles.size() == 1) {
                return doubleRoles.get(0).getCharacter();
            }
            return CharacterType.SHADY;
        }
        return player.getMainRole().getCharacter();
    }

    /**
     * Adds a {@code Job} to a {@code Player}.
     *
     * @param player the {@code Player}
     * @param newJob the {@code Job}
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void addJob(Player player, Job newJob)
            throws ReflectiveOperationException, SQLException, GameException {
        player.addJob(newJob);
        for (Job job : player.getJobs().stream().filter(job -> job.getClass().equals(newJob.getClass())).toList()) {
            job.initializeActions(player.getPlayerIdentifier());
        }
    }
}
