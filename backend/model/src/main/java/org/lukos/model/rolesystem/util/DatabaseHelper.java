package org.lukos.model.rolesystem.util;

import org.lukos.database.*;
import org.lukos.model.actionsystem.ActionDT;
import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a facade used for the database classes.
 *
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
abstract class DatabaseHelper {

    /**
     * Returns a {@code List} of dead {@code Player}s from the {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a {@code List} of dead {@code Player}s from the {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getDeadPlayers(int instanceID) throws SQLException {
        return InstanceDB.getDeadPlayers(instanceID);
    }

    /**
     * Returns a {@code List} of alive {@code Player}s from the {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a {@code List} of alive {@code Player}s from the {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getAlivePlayers(int instanceID) throws SQLException {
        return InstanceDB.getAlivePlayers(instanceID);
    }

    /**
     * Returns a {@code List} of all {@code Player}s from the {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a {@code List} of all {@code Player}s from the {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getPlayers(int instanceID) throws SQLException {
        return InstanceDB.getPlayers(instanceID);
    }

    /**
     * Returns a {@code List} of to be executed {@code Player}s from the {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a {@code List} of to be executed {@code Player}s from the {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getToBeExecuted(int instanceID) throws SQLException {
        return InstanceDB.getToBeExecuted(instanceID);
    }

    /**
     * Returns whether the {@code Player} with ID {@code playerID} is alive.
     *
     * @param playerID the ID of the {@code Player}
     * @return whether the {@code Player} with ID {@code playerID} is alive
     * @throws SQLException when a database operation fails
     */
    public static boolean isAlivePlayer(PlayerIdentifier playerID) throws SQLException {
        return InstanceDB.isAlivePlayer(playerID);
    }

    /**
     * Returns a {@code List} of tied {@code Player}s from the {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a {@code List} of tied {@code Player}s from the {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static List<PlayerIdentifier> getTiedPlayers(int instanceID) throws SQLException {
        return VoteDB.getTiedPlayers(instanceID);
    }

    /**
     * Returns a {@code List} of the IDs of the {@code Bridge}s of the {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return a {@code List} of the IDs of the {@code Bridge}s of the {@code Instance} with ID {@code instanceID}
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static List<Integer> getBridgesByInstance(int instanceID) throws SQLException, GameException {
        return LocationDB.getBridgesByInstance(instanceID);
    }

    /**
     * Adds the {@code item} from the {@code Player}.
     *
     * @param playerID the ID of the {@code Player}
     * @param item     the item to be added
     * @throws SQLException when a database operation fails
     */
    public static void addPlayerItem(PlayerIdentifier playerID, String item) throws SQLException {
        ItemDB.addPlayerItem(playerID, item);
    }

    /**
     * Deletes the {@code item} from the {@code Player}.
     *
     * @param playerID the ID of the {@code Player}
     * @param item     the item to be deleted
     * @throws SQLException when a database operation fails
     */
    public static void deletePlayerItem(PlayerIdentifier playerID, String item) throws SQLException {
        ItemDB.deletePlayerItem(playerID, item);
    }

    /**
     * Returns the amount of items of {@code item} the {@code Player} has.
     *
     * @param playerID the ID of the {@code Player}
     * @param item     the item to be counted
     * @return the amount of items of {@code item} the {@code Player} has
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static int amountOfItems(PlayerIdentifier playerID, String item) throws SQLException, GameException {
        return ItemDB.amountOfItems(playerID, item);
    }

    /**
     * Adds a new {@code ActionDT} to the database.
     *
     * @param actionDT the {@code ActionDT} to be added
     * @return the ID of the {@code Action}
     * @throws SQLException when a database operation fails
     */
    public static int addNewAction(ActionDT actionDT) throws SQLException {
        return ActionsDB.addNewAction(actionDT);
    }

    /**
     * Sets the {@code Action} with ID {@code actionID} to 'EXECUTED' in the database.
     *
     * @param actionID the ID of the {@code Action}
     * @throws SQLException when a database operation fails
     */
    public static void executeAction(int actionID) throws SQLException {
        ActionsDB.executeAction(actionID);
    }

    /**
     * Returns whether there exist a successor with type {@code successorType} in {@code Instance} with ID
     * {@code instanceID}.
     *
     * @param instanceID    the ID of the {@code Instance}
     * @param successorType the type of the successor
     * @return whether there exist a successor with type {@code successorType} in {@code Instance} with ID
     *         {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static boolean existSuccessor(int instanceID, SuccessorType successorType) throws SQLException {
        return SuccessorDB.existSuccessor(instanceID, successorType);
    }

    /**
     * Returns the {@code PlayerIdentifier} of the successor with type {@code successorType} in {@code Instance} with ID
     * {@code instanceID}.
     *
     * @param instanceID    the ID of the {@code Instance}
     * @param successorType the type of the successor
     * @return the {@code PlayerIdentifier} of the successor with type {@code successorType} in {@code Instance} with ID
     *         {@code instanceID}.
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static PlayerIdentifier getSuccessor(int instanceID, SuccessorType successorType)
            throws SQLException, GameException {
        return SuccessorDB.getSuccessor(instanceID, successorType);
    }

    /**
     * Removes the successor with type {@code successorType} from {@code Instance} with ID {@code InstanceID}.
     *
     * @param instanceID    the ID of the {@code Instance}
     * @param successorType the type of the successor
     * @throws SQLException when a database operation fails
     */
    public static void removeSuccessor(int instanceID, SuccessorType successorType) throws SQLException {
        SuccessorDB.removeSuccessor(instanceID, successorType);
    }

    /**
     * Returns the amount of undecided lynches in {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return the amount of undecided lynches in {@code Instance} with ID {@code instanceID}
     * @throws SQLException when a database operation fails
     */
    public static int getUndecidedLynches(int instanceID) throws SQLException {
        return VoteDB.getUndecidedLynches(instanceID);
    }

    /**
     * Adds a {@code Job} to a {@code Player} with the given {@code PlayerIdentifier}.
     *
     * @param playerID the {@code PlayerIdentifier} of the {@code Player}
     * @param job      the {@code Job} to add to the {@code Player}
     * @throws SQLException when a database operation fails
     */
    public static void addJobPlayerByID(PlayerIdentifier playerID, Job job) throws SQLException {
        RoleDB.addJobPlayerByID(playerID, job);
    }
}
