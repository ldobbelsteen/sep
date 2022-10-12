package org.lukos.model.actionsystem.actions.util;

import org.lukos.database.InstanceDB;
import org.lukos.database.PlayerDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.user.UserException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a facade for the instance and user classes.
 *
 * @author Rick van der Heijden (1461923)
 * @since 08-04-2022
 */
class InstanceAndUserHelper {

    /**
     * Private constructor to avoid instance methods.
     */
    private InstanceAndUserHelper() {
    }

    /**
     * Returns a list of {@code Player}s of an {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a list of {@code Player}s of an {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getPlayersByInstanceID(int instanceID) throws SQLException {
        return InstanceDB.getPlayers(instanceID);
    }

    /**
     * Returns a list of {@code Player}s that are protected of an {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a list of {@code Player}s that are protected of an {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getProtectedPlayersByInstanceID(int instanceID) throws SQLException {
        return PlayerDB.getProtectedPlayers(instanceID);
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
        InstanceDB.modifyExecuted(instanceID, playerID, executed);
    }

    /**
     * Retrieves a {@code Instance} by its ID.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a {@code Instance} by its ID.
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static IInstance getInstanceByID(int instanceID) throws GameException, SQLException {
        return InstanceManager.getInstanceManager().getInstance(instanceID);
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
        getInstanceByID(instanceID).revivePlayer(player);
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
        getInstanceByID(instanceID).addToBeExecuted(playerIDs);
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
        return UserManager.getInstance().getUser(userID).getPlayer();
    }

    /**
     * Returns the username of a {@code User} with ID {@code userID}.
     *
     * @param userID the ID of the {@code User}
     * @return the username of a {@code User} with ID {@code userID}
     * @throws SQLException when a database operation fails
     */
    public static String getUsernameByUserID(int userID) throws SQLException {
        return new User(userID).getUsername();
    }
}
