package org.lukos.model.voting;

import org.lukos.database.VoteDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.voting.NotAllowedToJoinVoteException;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is an alpha wolf vote, this vote will accept vote for everyone who is an {@code Werewolf} and who is allowed to
 * vote. The list with allowed players to vote will be immutable.
 *
 * @author Rick van der Heijden (1461923)
 * @since 16-03-2022
 */
public class AlphaWolfVote extends Vote {

    /**
     * Constructs a {@code AlphaWolfVote} with a pre-specified ID.
     *
     * @param voteId the ID of the vote
     */
    public AlphaWolfVote(int voteId) {
        super(voteId);
    }

    /**
     * Constructs a {@code AlphaWolfVote}, but only {@code Player}s that belong to the {@code Group} {@code WEREWOLVES}
     * are allowed to vote.
     *
     * @param instanceID the ID of the {@code Instance} to which this vote belongs
     * @param allowed    List of {@code Player}s that are allowed to vote in this vote
     * @throws GameException                when an exception in the game logic occurs
     * @throws SQLException                 when an exception occurs in a database operation
     * @throws ReflectiveOperationException when an exception occurs in a reflective operation
     */
    public AlphaWolfVote(int instanceID, List<Player> allowed)
            throws GameException, SQLException, ReflectiveOperationException {
        super(instanceID, VoteType.ALPHA_WOLF);

        // Checks if 1 player is not a werewolf
        Set<Group> groups = new HashSet<>();
        for (Player player : allowed) {
            groups.add(player.getMainRole().getGroup());
        }
        if (!groups.stream().allMatch(group -> group == Group.WEREWOLVES)) {
            throw new NotAllowedToJoinVoteException(
                    "One of the players is not a werewolf and is not allowed to join this vote.");
        }
        Set<PlayerIdentifier> allowedIDs = new HashSet<>();
        for (Player player : allowed) {
            allowedIDs.add(player.getPlayerIdentifier());
        }
        VoteDB.saveAllowedPlayers(getVid(), allowedIDs);
    }

    @Override
    public void submitVote(PlayerIdentifier playerIdentifier, Ballot ballot)
            throws SQLException, GameException, ReflectiveOperationException {
        super.submitVote(playerIdentifier, ballot);

        BallotCheckerHelper.ballotChecker(getVid());
    }
}
