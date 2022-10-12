package org.lukos.model.instances.util;

import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.jobs.AlphaWolf;
import org.lukos.model.rolesystem.jobs.Mayor;
import org.lukos.model.rolesystem.util.RoleActions;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;
import org.lukos.model.voting.VoteType;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

/**
 * This is a helper class for general things that the {@code Instance} has to do.
 *
 * @author Martijn van Andel (1251104)
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
public class GeneralInstanceHelper extends GeneralUtilInstanceHelper {

    /**
     * This functions assigns the successor his new {@code Job} if the {@code Player} has a {@code Job} with a
     * successor.
     *
     * @param player     the {@code Player} who died
     * @param instanceID the ID of the {@code Instance}
     * @param gameMaster the ID of the game master
     * @throws GameException                when an exception occurs in the game-logic
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void assignSuccessor(Player player, int instanceID, int gameMaster)
            throws ReflectiveOperationException, SQLException, GameException {
        for (Job job : player.getJobs()) {
            assignAlphaWolfSuccessor(player, job, instanceID);
            assignMayorSuccessor(player, job, instanceID, gameMaster);
        }
    }

    /**
     * If the {@code Player} is the {@code AlphaWolf} its successor will be assigned.
     *
     * @param player     the {@code Player}
     * @param job        the {@code Job} of the {@code player}
     * @param instanceID the ID of the {@code Instance}
     * @throws GameException when an exception occurs in the game-logic
     * @throws SQLException  when a database operation fails
     */
    public static void assignAlphaWolfSuccessor(Player player, Job job, int instanceID)
            throws SQLException, GameException {
        // If the alpha wolf dies the successor will be the new alpha wolf
        if (job.getClass().equals(AlphaWolf.class)) {
            SuccessorType type = SuccessorType.ALPHA_WOLF;
            if (existSuccessor(instanceID, SuccessorType.ALPHA_WOLF)) {
                PlayerIdentifier successorID = getSuccessor(instanceID, type);
                if (successorID.instanceID() == instanceID) {
                    removeSuccessor(instanceID, type);
                    addJobPlayerByID(successorID, new AlphaWolf());
                }
            }
            player.removeJob(new AlphaWolf());
        }
    }

    /**
     * If the {@code Player} is the {@code Mayor} its successor will be assigned.
     *
     * @param player     the {@code Player}
     * @param job        the {@code Job} of the {@code player}
     * @param instanceID the ID of the {@code Instance}
     * @param gameMaster the ID of the game master
     * @throws GameException when an exception occurs in the game-logic
     * @throws SQLException  when a database operation fails
     */
    public static void assignMayorSuccessor(Player player, Job job, int instanceID, int gameMaster)
            throws SQLException, GameException {
        // If the mayor dies the successor will be the new mayor
        if (job.getClass().equals(Mayor.class)) {
            SuccessorType type = SuccessorType.MAYOR;
            if (existSuccessor(instanceID, SuccessorType.MAYOR)) {
                ArrayList<PlayerIdentifier> successorID = new ArrayList<>();
                successorID.add(getSuccessor(instanceID, type));
                if (successorID.get(0).instanceID() == instanceID) {
                    removeSuccessor(instanceID, type);
                    addMayor(successorID, instanceID, gameMaster);
                }
            }
            player.removeJob(new Mayor());
        }
    }

    /**
     * Adds Mayor to a given player.
     *
     * @param chosenPlayers a {@code ArrayList} of chosen {@code Player}s
     * @param instanceID    the ID of the {@code Instance}
     * @param gameMaster    the ID of the game master
     * @throws GameException when an exception occurs in the game-logic
     * @throws SQLException  when a database operation fails
     */
    public static void addMayor(ArrayList<PlayerIdentifier> chosenPlayers, int instanceID, int gameMaster)
            throws SQLException, GameException {
        addAction(Instant.now(), RoleActions.NEW_MAYOR.getAction(),
                createNewPreActionDT(new PlayerIdentifier(instanceID, gameMaster),
                        createNewActionEnc(new ArrayList<>(), chosenPlayers)));
    }

    /**
     * Returns whether there exist an {@code Mayor} in the given list of {@code Player}s.
     *
     * @param playerList the list of {@code Player}s
     * @return whether there exist an {@code Mayor} in the given list of {@code Player}s
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static boolean existMayor(ArrayList<Player> playerList) throws ReflectiveOperationException, SQLException {
        for (Player player : playerList) {
            for (Job job : player.getJobs()) {
                if (job.getClass().equals(Mayor.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether there exist an {@code AlphaWolf} in the given list of {@code Player}s.
     *
     * @param playerList the list of {@code Player}s
     * @return whether there exist an {@code AlphaWolf} in the given list of {@code Player}s
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static boolean existAlphaWolf(ArrayList<Player> playerList)
            throws ReflectiveOperationException, SQLException {
        for (Player player : playerList) {
            for (Job job : player.getJobs()) {
                if (job.getClass().equals(AlphaWolf.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Applies the voting results generated in processVote(). Can also be used by the Mayor action.
     *
     * @param instanceID    the ID of the {@code Instance}
     * @param gameMaster    the ID of the game master
     * @param chosenPlayers List of chosen players to which an action needs to be applied.
     * @param voteType      Type of vote, determining what action is applied.
     * @throws GameException                when an exception occurs in the game-logic
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void applyResults(int instanceID, int gameMaster, ArrayList<PlayerIdentifier> chosenPlayers,
                                    VoteType voteType)
            throws SQLException, GameException, ReflectiveOperationException {
        // Apply necessary actions
        switch (voteType) {
            case LYNCH -> addAction(Instant.now(), RoleActions.KILL_PLAYERS.getAction(),
                    createNewPreActionDT(new PlayerIdentifier(instanceID, gameMaster),
                            createNewActionEnc(new ArrayList<>(), chosenPlayers)));
            case MAYOR -> addMayor(chosenPlayers, instanceID, gameMaster);
            case ALPHA_WOLF -> {
                Player player = new Player(chosenPlayers.get(0));
                player.addJob(new AlphaWolf());
                for (Job job : player.getJobs().stream().filter(job -> job instanceof AlphaWolf).toList()) {
                    job.initializeActions(player.getPlayerIdentifier());
                }
            }
        }
    }

    /**
     * Resets the actions parameters of roles that can be performed every night.
     *
     * @param alivePlayers all the alive {@code Player}s
     * @param gameSpeed    the current game speed
     * @throws GameException                when an exception occurs in the game-logic
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void replenishActions(ArrayList<Player> alivePlayers, int gameSpeed)
            throws ReflectiveOperationException, SQLException, GameException {
        for (Player p : alivePlayers) {
            p.getMainRole().replenishAction(gameSpeed, p.getPlayerIdentifier());
            for (Job job : p.getJobs()) {
                job.replenishAction(gameSpeed, p.getPlayerIdentifier());
            }
        }
    }
}
