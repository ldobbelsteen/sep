package org.lukos.model.exceptions.voting;

/**
 * This exception is to report that a {@code Ballot} has been submitted after the {@code IVote} is closed.
 *
 * @author Rick van der Heijden (1461923)
 * @since 21-02-2022
 */
public class VoteClosedException extends VotingException {

    /**
     * Constructs a {@code VoteClosedException}.
     */
    public VoteClosedException() {
    }

    /**
     * Constructs a {@code VoteClosedException} with a {@code message}.
     *
     * @param message the message
     */
    public VoteClosedException(String message) {
        super(message);
    }
}
