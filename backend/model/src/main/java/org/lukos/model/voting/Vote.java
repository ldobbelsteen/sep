package org.lukos.model.voting;

import org.lukos.database.VoteDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The abstract base class for votes. All ways of voting should adhere to this interface.
 *
 * @author Rick van der Heijden (1461923)
 * @since 21-02-2022
 */
public abstract class Vote {

    /** The vote ID */
    private final int vid;

    /**
     * Constructs a {@link Vote} object for the {@link Vote} with the given ID.
     *
     * @param voteId the ID of the {@link Vote}
     */
    public Vote(int voteId) {
        this.vid = voteId;
    }

    /**
     * Constructs a {@code Vote} of type {@code voteType} for the {@code Instance} with ID {@code instanceId}.
     *
     * @param instanceId The ID of the {@code Instance}
     * @param voteType   The {@code VoteType} of the {@code Vote}
     * @throws SQLException when an exception occurs in a database operation
     */
    public Vote(int instanceId, VoteType voteType) throws SQLException {
        this.vid = VoteDB.addNewVote(instanceId, voteType);
    }

    /**
     * Returns the ID of this {@code Vote}.
     *
     * @return the ID of this {@code Vote}
     */
    public int getVid() {
        return this.vid;
    }

    /**
     * Getter for the {@code allowed} list, which contains all {@code Player}s that are allowed to vote.
     *
     * @return The list with {@code Player}s that are allowed to vote
     * @throws SQLException when an exception occurs in a database operation
     */
    public List<PlayerIdentifier> getAllowed() throws SQLException {
        return VoteDB.getAllowedPlayers(this.getVid());
    }

    /**
     * Getter for the {@code ballots} list, which contains all the {@code Ballot}s that have been cast.
     *
     * @return The list with all the {@code Ballot}s that have been cast
     * @throws SQLException when an exception occurs in a database operation
     */
    public List<Ballot> getBallots() throws SQLException {
        ResultSet result = VoteDB.getAllBallotsOfVote(this.vid);
        List<Ballot> ballots = new ArrayList<>();
        while (result.next()) {
            int instanceID = result.getInt("instanceID");
            int userID = result.getInt("userID");
            int targetID = result.getInt("targetID");

            PlayerIdentifier player = new PlayerIdentifier(instanceID, userID);
            PlayerIdentifier target = new PlayerIdentifier(instanceID, targetID);
            ballots.add(new Ballot(player, target));
        }
        return ballots;
    }

    /**
     * This method will start the vote.
     *
     * @throws SQLException when an exception occurs in a database operation
     */
    public void start() throws SQLException {
        VoteDB.modifyStarted(this.getVid(), true);
    }

    /**
     * This method will end the vote and release its result in an array that will be given.
     *
     * @return A {@code Map} with the result of the vote
     * @throws SQLException when an exception occurs in a database operation
     */
    public synchronized Map<PlayerIdentifier, Integer> end() throws SQLException {
        VoteDB.modifyEnded(this.vid, true);
        Map<PlayerIdentifier, Integer> votes = new HashMap<>();
        this.getBallots().forEach(ballot -> votes.put(ballot.target(), votes.getOrDefault(ballot.target(), 0) + 1));
        return votes.size() > 0 ? votes : null;
    }

    /**
     * This function will be used for a {@code Player} to submit its vote. The player should be allowed to vote.
     *
     * @param playerIdentifier The player who has submitted the vote.
     * @param ballot           The voting ballot of the player.
     * @throws GameException                when an exception in the game logic occurs
     * @throws SQLException                 when an exception occurs in a database operation
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public synchronized void submitVote(PlayerIdentifier playerIdentifier, Ballot ballot)
            throws GameException, SQLException, ReflectiveOperationException {
        ResultSet result = VoteDB.findVoteByID(this.vid);
        boolean started;
        boolean ended;

        if (result.next()) {
            started = result.getBoolean("started");
            ended = result.getBoolean("ended");
        } else {
            throw new SQLException("That ID is invalid!");
        }

        VotePermissionChecker.submitVotePermission(getAllowed(), getBallots(), playerIdentifier, ballot, started,
                ended);

        VoteDB.addBallot(playerIdentifier.userID(), playerIdentifier.instanceID(), this.vid, ballot.target().userID());
    }

    /**
     * Returns the type of the vote.
     *
     * @return the type of the vote
     * @throws SQLException when an exception occurs in a database operation
     */
    public VoteType getVoteType() throws SQLException {
        return VoteDB.getVoteTypeByID(this.vid);
    }

    /**
     * Returns whether the vote is still busy.
     *
     * @return whether the vote is still busy
     * @throws SQLException when an exception occurs in a database operation
     */
    public boolean isBusy() throws SQLException {
        return VoteDB.getBusyByID(this.vid);
    }

    /**
     * Returns whether the vote has started.
     *
     * @return whether the vote has started
     * @throws SQLException when an exception occurs in a database operation
     */
    protected boolean started() throws SQLException {
        return VoteDB.getStartedByID(this.vid);
    }

    /**
     * Returns whether the vote has ended.
     *
     * @return whether the vote has ended
     * @throws SQLException when an exception occurs in a database operation
     */
    protected boolean ended() throws SQLException {
        return VoteDB.getEndedByID(this.vid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vote vote) {
            return vote.vid == this.vid;
        }
        return false;
    }
}
