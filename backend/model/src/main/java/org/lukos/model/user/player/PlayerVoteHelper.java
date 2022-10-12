package org.lukos.model.user.player;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.voting.NoSuchVoteException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.voting.Ballot;
import org.lukos.model.voting.Vote;

import java.sql.SQLException;
import java.util.List;

/**
 * This is a helper class that helps the {@code Player} to perform its {@code Action}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
public class PlayerVoteHelper {

    /** Private constructor, as it is a helper class, and it enforces static methods */
    private PlayerVoteHelper() {
    }

    /**
     * Method used by the {@code Player} to vote in a {@code Vote}.
     *
     * @param playerID the {@code PlayerIdentifier} of the {@code Player}
     * @param vid      the ID of the {@code Vote}
     * @param targetID the {@code PlayerIdentifier} of the {@code Player} that is being voted on.
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    static void vote(PlayerIdentifier playerID, int vid, PlayerIdentifier targetID)
            throws GameException, SQLException, ReflectiveOperationException {
        // Get the game instance
        IInstance gameInstance = InstanceManager.getInstanceManager().getInstance(playerID.instanceID());

        // Get the list of ongoing votes
        List<Vote> ongoingVotes = gameInstance.getOngoingVotes();

        Vote vote = null;

        // Look for the vote
        for (Vote ongoingVote : ongoingVotes) {
            // Check if this is the vote we are looking for
            if (vid == ongoingVote.getVid()) {
                vote = ongoingVote;
                break;
            }
        }
        // If we did not find the vote, throw an exception
        if (vote == null) {
            throw new NoSuchVoteException("The vote with vid " + vid + " does not exist!");
        }

        // Create ballot
        Ballot ballot = new Ballot(playerID, targetID);

        // Submit the vote
        vote.submitVote(playerID, ballot);
    }
}
