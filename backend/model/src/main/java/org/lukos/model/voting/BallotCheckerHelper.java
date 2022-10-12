package org.lukos.model.voting;

import org.lukos.database.VoteDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class helps the {@code AlphaWolfVote} to check for a unanimous vote.
 *
 * @author Rick van der Heijden (1461923)
 * @since 08-04-2022
 */
public class BallotCheckerHelper {

    /**
     * Private constructor to avoid instance methods.
     */
    private BallotCheckerHelper() {
    }

    /**
     * This method checks whether all ballots has been submitted and then signals {@code Instance} to close the {@code
     * AlphaWolfVote}.
     *
     * @param vid The ID of the {@code AlphaWolfVote}
     * @throws SQLException                 when a database operation fails
     * @throws GameException                when a game-logic operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void ballotChecker(int vid) throws SQLException, GameException, ReflectiveOperationException {
        ResultSet submittedVotes = VoteDB.getAllBallotsOfVote(vid);
        int votes = 0;
        while (submittedVotes.next()) {
            votes++;
        }
        List<PlayerIdentifier> allowedPlayers = VoteDB.getAllowedPlayers(vid);
        if (votes == allowedPlayers.size()) {
            InstanceManager.getInstanceManager().getInstance(allowedPlayers.get(0).instanceID())
                    .endVote(VoteType.ALPHA_WOLF);
        }
    }
}
