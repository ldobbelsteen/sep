package org.lukos.model.voting;

import org.lukos.model.exceptions.voting.*;
import org.lukos.model.user.PlayerIdentifier;

import java.util.List;

/**
 * Helper class for checking permissions for {@link Vote}s.
 *
 * @author Rick van der Heijden (1461923)
 * @since 05-04-2022
 */
public class VotePermissionChecker {

    /**
     * Private constructor for {@code VotePermissionChecker} to make sure only static methods will be called and made.
     */
    private VotePermissionChecker() {
    }

    /**
     * Returns whether the submitting of the vote is done with the right permission.
     *
     * @param allowed          the {@link List} with players that are allowed to vote.
     * @param ballots          the {@link List} with the ballots that are already submitted
     * @param playerIdentifier the {@link PlayerIdentifier} of the player submitting the ballot
     * @param ballot           the {@link Ballot} that is submitted to the vote
     * @param started          boolean whether the vote has started
     * @param ended            boolean whether the vote has ended
     * @throws VotingException when the ballot is not allowed to be submitted
     */
    public static void submitVotePermission(List<PlayerIdentifier> allowed, List<Ballot> ballots,
                                            PlayerIdentifier playerIdentifier, Ballot ballot, boolean started,
                                            boolean ended) throws VotingException {
        if (!allowed.contains(playerIdentifier)) {
            throw new NotAllowedToVoteException("Player is not allowed to vote.");
        }
        if (!allowed.contains(ballot.target())) {
            throw new NotAllowedTargetException("Target can not be voted on.");
        }
        if (!started) {
            throw new VoteNotStartedException("The vote has not yet started.");
        }
        if (ended) {
            throw new VoteClosedException("The vote has already ended.");
        }
        if (!ballot.player().equals(playerIdentifier)) {
            throw new VoterFraudException("Ballot is not from player submitting the vote");
        }
        if (alreadyVoted(ballots, playerIdentifier)) {
            throw new AlreadyVotedException("Player has already voted.");
        }
    }

    /**
     * Helper function to determine whether a player has already voted.
     *
     * @param ballots the {@code Ballot}s of the {@code Vote} the player is trying to vote on
     * @param voter   the player
     * @return whether the player has already voted
     */
    private static boolean alreadyVoted(List<Ballot> ballots, PlayerIdentifier voter) {
        return ballots.stream().anyMatch(ballot -> ballot.player().equals(voter));
    }
}
