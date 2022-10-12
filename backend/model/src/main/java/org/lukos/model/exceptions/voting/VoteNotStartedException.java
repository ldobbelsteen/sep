package org.lukos.model.exceptions.voting;

/**
 * This exception is to report that a {@code Ballot} has been submitted before the {@code IVote} has started.
 *
 * @author Rick van der Heijden (1461923)
 * @since 21-02-2022
 */
public class VoteNotStartedException extends VotingException {

    /**
     * Constructs a {@code VoteNotStartedException}.
     */
    public VoteNotStartedException() {
    }

    /**
     * Constructs a {@code VoteNotStartedException} with a {@code message}.
     *
     * @param message the message
     */
    public VoteNotStartedException(String message) {
        super(message);
    }
}
