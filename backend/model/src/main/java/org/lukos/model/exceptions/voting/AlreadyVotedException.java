package org.lukos.model.exceptions.voting;

/**
 * This exception is to report that a {@code Player} tried to vote on an {@code IVote} that they already voted on.
 *
 * @author Sander van Heesch (1436708)
 * @since 08-03-2022
 */
public class AlreadyVotedException extends VotingException {

    /**
     * Constructs a {@code AlreadyVotedException}.
     */
    public AlreadyVotedException() {
    }

    /**
     * Constructs a {@code AlreadyVotedException} with a {@code message}.
     *
     * @param message the message
     */
    public AlreadyVotedException(String message) {
        super(message);
    }
}
