package org.lukos.model.rolesystem.util;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.rolesystem.EligibleType;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.isWolf;

/**
 * This class is a facade for retrieving maps of eligible players or locations.
 *
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
abstract class InformationHelper extends DatabaseHelper {

    /**
     * Returns a {@code Map} with all dead {@code Player}s as eligible.
     *
     * @param instance the instance for which to take the dead {@code Player}s
     * @return all dead {@code Player}s as eligible
     * @throws SQLException when a database operation fails
     */
    public static Map<EligibleType, List<Integer>> getDeadPlayersAsEligible(IInstance instance) throws SQLException {
        Map<EligibleType, List<Integer>> eligible = new HashMap<>();
        List<Integer> playerIDs = getDeadPlayers(instance.getIid()).stream().map(PlayerIdentifier::userID).toList();
        eligible.put(EligibleType.PLAYER, playerIDs);
        return eligible;
    }

    /**
     * Returns a {@code Map} with all alive {@code Player}s as eligible.
     *
     * @param instance the instance for which to take the alive {@code Player}s
     * @return all alive {@code Player}s as eligible
     * @throws SQLException when a database operation fails
     */
    public static Map<EligibleType, List<Integer>> getAlivePlayersAsEligible(IInstance instance) throws SQLException {
        Map<EligibleType, List<Integer>> eligible = new HashMap<>();
        List<Integer> playerIDs = getAlivePlayers(instance.getIid()).stream().map(PlayerIdentifier::userID).toList();
        eligible.put(EligibleType.PLAYER, playerIDs);
        return eligible;
    }

    /**
     * Returns a {@code Map} with all alive wolves as eligible except the {@code Player} with ID {@code playerID}.
     *
     * @param instance the instance for which to take the wolves
     * @param playerID the {@code Player} to exclude from eligible {@code Map}
     * @return a {@code Map} with all alive wolves as eligible except the {@code Player} with ID {@code playerID}
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static Map<EligibleType, List<Integer>> getAliveWolvesExceptSomeoneAsEligible(IInstance instance,
                                                                                         PlayerIdentifier playerID)
            throws SQLException, ReflectiveOperationException, GameException {
        Map<EligibleType, List<Integer>> eligible = new HashMap<>();
        List<Integer> playerIDs = new ArrayList<>();
        for (PlayerIdentifier wolfID : getAlivePlayers(instance.getIid())) {
            if (isWolf(wolfID) && !wolfID.equals(playerID)) {
                playerIDs.add(wolfID.userID());
            }
        }
        eligible.put(EligibleType.PLAYER, playerIDs);
        return eligible;
    }

    /**
     * Returns a {@code Map} with all alive {@code Player}s as eligible except the {@code Player} with ID
     * {@code playerID}.
     *
     * @param instance the instance for which to take the {@code Player}s
     * @param playerID the {@code Player} to exclude from eligible {@code Map}
     * @return a {@code Map} with all alive {@code Player}s as eligible except the {@code Player} with ID
     *         {@code playerID}
     * @throws SQLException when a database operation fails
     */
    public static Map<EligibleType, List<Integer>> getAlivePlayersExceptSomeoneAsEligible(IInstance instance,
                                                                                          PlayerIdentifier playerID)
            throws SQLException {
        Map<EligibleType, List<Integer>> eligible = new HashMap<>();
        List<Integer> playerIDs = new ArrayList<>();
        for (PlayerIdentifier id : getAlivePlayers(instance.getIid())) {
            if (!id.equals(playerID)) {
                playerIDs.add(id.userID());
            }
        }
        eligible.put(EligibleType.PLAYER, playerIDs);
        return eligible;
    }

    /**
     * Returns a {@code Map} with all {@code Player}s as eligible.
     *
     * @param instance the instance for which to take the {@code Player}s
     * @return all {@code Player}s as eligible
     * @throws SQLException when a database operation fails
     */
    public static Map<EligibleType, List<Integer>> getAllPlayersAsEligible(IInstance instance) throws SQLException {
        Map<EligibleType, List<Integer>> eligible = new HashMap<>();
        List<Integer> playerIDs = getPlayers(instance.getIid()).stream().map(PlayerIdentifier::userID).toList();
        eligible.put(EligibleType.PLAYER, playerIDs);
        return eligible;
    }

    /**
     * Returns a {@code Map} with all to be executed {@code Player}s as eligible.
     *
     * @param instance the instance for which to take the to be executed {@code Player}s
     * @return all to be executed {@code Player}s as eligible
     * @throws SQLException when a database operation fails
     */
    public static Map<EligibleType, List<Integer>> getToBeExecutedPlayersAsEligible(IInstance instance)
            throws SQLException {
        Map<EligibleType, List<Integer>> eligible = new HashMap<>();
        List<Integer> playerIDs = getToBeExecuted(instance.getIid()).stream().map(PlayerIdentifier::userID).toList();
        eligible.put(EligibleType.PLAYER, playerIDs);
        return eligible;
    }

    /**
     * Returns a {@code Map} with all vote tied {@code Player}s as eligible.
     *
     * @param instance the instance for which to take the vote tied {@code Player}s
     * @return all vote tied {@code Player}s as eligible
     * @throws SQLException when a database operation fails
     */
    public static Map<EligibleType, List<Integer>> getVoteTiedPlayersAsEligible(IInstance instance)
            throws SQLException {
        /* Retreive userIDs from tied players. */
        List<Integer> chosenPlayerIDs = new ArrayList<>();
        getTiedPlayers(instance.getIid()).forEach(p -> chosenPlayerIDs.add(p.userID()));

        /* Send tied players as Action Information. */
        HashMap<EligibleType, List<Integer>> eligiblePlayers = new HashMap<>();
        eligiblePlayers.put(EligibleType.PLAYER, chosenPlayerIDs);
        return eligiblePlayers;
    }

    /**
     * Returns a {@code Map} with all {@code Location}s as eligible.
     *
     * @param instance the instance for which to take the {@code Location}s
     * @return all {@code Location}s as eligible
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static Map<EligibleType, List<Integer>> getLocationsAsEligible(IInstance instance)
            throws SQLException, GameException {
        Map<EligibleType, List<Integer>> eligible = new HashMap<>();
        List<Integer> playerIDs = getAlivePlayers(instance.getIid()).stream().map(PlayerIdentifier::userID).toList();

        eligible.put(EligibleType.HOUSE, playerIDs);

        List<Integer> bridgeIDs = getBridgesByInstance(instance.getIid());
        eligible.put(EligibleType.BRIDGE, bridgeIDs);
        return eligible;
    }

    /**
     * Returns a {@code Map} with all {@code Location}s, except those of wolves, as eligible.
     *
     * @param instance the instance for which to take the {@code Location}s, except those of wolves
     * @return all {@code Location}s, except those of wolves, as eligible
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static Map<EligibleType, List<Integer>> getLocationOfNonWolvesAsEligible(IInstance instance)
            throws SQLException, GameException, ReflectiveOperationException {
        HashMap<EligibleType, List<Integer>> eligible = new HashMap<>();
        List<Integer> playerIDs = new ArrayList<>();
        for (PlayerIdentifier alive : getAlivePlayers(instance.getIid())) {
            if (!isWolf(alive)) {
                playerIDs.add(alive.userID());
            }
        }
        eligible.put(EligibleType.HOUSE, playerIDs);
        eligible.put(EligibleType.BRIDGE, instance.getBridges());
        return eligible;
    }
}
