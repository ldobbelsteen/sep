package org.lukos.model.instances;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.instances.GameAlreadyStartedException;
import org.lukos.model.exceptions.location.BridgeDoesNotExistException;
import org.lukos.model.exceptions.voting.NoSuchVoteException;
import org.lukos.model.location.Location;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;
import org.lukos.model.voting.Vote;
import org.lukos.model.voting.VoteType;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IInstance {

    int getIid();

    int getGameMaster() throws SQLException;

    void removeOngoingVote(int voteID) throws SQLException;

    void initializeInstanceState(InstanceState instanceState) throws SQLException;

    String getGameName() throws SQLException;

    List<Integer> getBridges() throws SQLException, BridgeDoesNotExistException;

    List<PlayerIdentifier> getToBeExecuted() throws SQLException;

    void addToBeExecuted(PlayerIdentifier playerID) throws SQLException;

    void addToBeExecuted(List<PlayerIdentifier> players) throws SQLException;

    List<Player> playerIdentifierListToPlayerList(List<PlayerIdentifier> playerIdentifierList);

    List<Player> alivePlayers() throws SQLException;

    List<Player> homelessPlayers() throws SQLException;

    List<Player> homeOwners() throws SQLException;

    void killPlayer(Player player) throws GameException, SQLException, ReflectiveOperationException;

    void revivePlayer(Player player) throws GameException, SQLException, ReflectiveOperationException;

    boolean removePlayer(Player player) throws SQLException;

    List<Player> getPlayerList() throws SQLException;

    InstanceState getInstanceState() throws SQLException;

    void startGame(int caller) throws GameException, SQLException, ReflectiveOperationException;

    void stopGame() throws SQLException, GameAlreadyStartedException;

    void endGame() throws SQLException;

    void throwGame(int caller) throws GameException, SQLException, ReflectiveOperationException;

    void nextPhase() throws GameException, SQLException, ReflectiveOperationException;

    boolean isStarted() throws SQLException;

    List<Vote> getOngoingVotes() throws SQLException, NoSuchVoteException;

    Vote startVote(VoteType voteType) throws SQLException, GameException, ReflectiveOperationException;

    void processVote(Map<PlayerIdentifier, Integer> results, VoteType voteType)
            throws SQLException, GameException, ReflectiveOperationException;

    void endVote(Vote vote, VoteType voteType) throws GameException, SQLException, ReflectiveOperationException;

    void endVote(VoteType voteType) throws GameException, SQLException, ReflectiveOperationException;

    List<Integer> getLocations() throws SQLException;

    void movePlayer(Player player, Location newLocation) throws GameException, SQLException;
}
