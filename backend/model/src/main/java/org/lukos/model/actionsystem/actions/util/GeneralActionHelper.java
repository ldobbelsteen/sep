package org.lukos.model.actionsystem.actions.util;

import org.lukos.database.VoteDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.user.UserException;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a facade for the actions with communication with other parts of the model.
 *
 * @author Rick van der Heijden (1461923)
 * @since 08-04-2022
 */
public class GeneralActionHelper extends ActionMessageHelper {

    /**
     * Private constructor to avoid instance methods.
     */
    private GeneralActionHelper() {
    }

    /**
     * Returns a list of {@code Player}s of an {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a list of {@code Player}s of an {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getPlayersByInstanceID(int instanceID) throws SQLException {
        return InstanceAndUserHelper.getPlayersByInstanceID(instanceID);
    }

    /**
     * Returns a list of {@code Player}s that are protected of an {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a list of {@code Player}s that are protected of an {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getProtectedPlayersByInstanceID(int instanceID) throws SQLException {
        return InstanceAndUserHelper.getProtectedPlayersByInstanceID(instanceID);
    }

    /**
     * Modify the executed status of a {@code Player}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @param playerID   the ID of the {@code Player}
     * @param executed   the new executed status
     * @throws SQLException when a database operation fails
     */
    public static void modifyExecutedByInstanceID(int instanceID, PlayerIdentifier playerID, boolean executed)
            throws SQLException {
        InstanceAndUserHelper.modifyExecutedByInstanceID(instanceID, playerID, executed);
    }

    /**
     * Revives a {@code Player}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @param player     the {@code Player}
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void revivePlayerByInstanceID(int instanceID, Player player)
            throws GameException, SQLException, ReflectiveOperationException {
        InstanceAndUserHelper.revivePlayerByInstanceID(instanceID, player);
    }

    /**
     * Makes a {@code List} of {@code Player}s to be executed/
     *
     * @param instanceID the ID of the {@code Instance}
     * @param playerIDs  the {@code List} of {@code Player} IDs
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void addToBeExecutedByInstanceID(int instanceID, List<PlayerIdentifier> playerIDs)
            throws GameException, SQLException {
        InstanceAndUserHelper.addToBeExecutedByInstanceID(instanceID, playerIDs);
    }

    /**
     * Returns the {@code Player} associated with a {@code User} with ID {@code userID}.
     *
     * @param userID the ID of the {@code User}
     * @return the {@code Player} associated with a {@code User} with ID {@code userID}
     * @throws SQLException  when a database operation fails
     * @throws UserException when a user related operation fails
     */
    public static Player getPlayerByUserID(int userID) throws SQLException, UserException {
        return InstanceAndUserHelper.getPlayerByUserID(userID);
    }

    /**
     * Returns the username of a {@code User} with ID {@code userID}.
     *
     * @param userID the ID of the {@code User}
     * @return the username of a {@code User} with ID {@code userID}
     * @throws SQLException when a database operation fails
     */
    public static String getUsernameByUserID(int userID) throws SQLException {
        return InstanceAndUserHelper.getUsernameByUserID(userID);
    }

    /**
     * Returns whether a {@code Player} is an {@code AlphaWolf}.
     *
     * @param playerID the ID of the {@code Player}
     * @return whether a {@code Player} is an {@code AlphaWolf}
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static boolean isAlphaWolf(PlayerIdentifier playerID) throws ReflectiveOperationException, SQLException {
        return RoleHelper.isAlphaWolf(new Player(playerID));
    }

    /**
     * Returns whether a {@code Player} is a wolf.
     *
     * @param playerID the ID of the {@code Player}
     * @return whether the {@code Player} is a wolf
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static boolean isWolf(PlayerIdentifier playerID)
            throws ReflectiveOperationException, SQLException, GameException {
        return RoleHelper.isWolf(new Player(playerID));
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
        return RoleHelper.getCharacterTypeByPlayer(player);
    }

    /**
     * Adds a {@code Job} to a {@code Player}.
     *
     * @param player the {@code Player}
     * @param job    the {@code Job}
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void addJob(Player player, Job job) throws ReflectiveOperationException, SQLException, GameException {
        RoleHelper.addJob(player, job);
    }

    /**
     * Sets the amount of undecided lynches in an {@code Instance} with ID {@code iid}.
     *
     * @param iid              the ID of the {@code Instance}
     * @param undecidedLynches the amount of undecided lynches
     * @throws SQLException when a database operation fails
     */
    public static void setUndecidedLynchesByInstanceID(int iid, int undecidedLynches) throws SQLException {
        VoteDB.setUndecidedLynchesByInstanceID(iid, undecidedLynches);
    }
}
