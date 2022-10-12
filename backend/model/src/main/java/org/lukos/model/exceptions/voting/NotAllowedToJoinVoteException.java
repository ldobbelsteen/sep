package org.lukos.model.exceptions.voting;

/**
 * This exception is to report that the {@code Player} is not allowed to join a certain vote.
 *
 * @author Rick van der Heijden (1461923)
 * @since 16-03-2022
 */
public class NotAllowedToJoinVoteException extends VotingException {

    /**
     * Constructs a {@code NotAllowedToJoinVoteException}.
     */
    public NotAllowedToJoinVoteException() {
    }

    /**
     * Constructs a {@code NotAllowedToJoinVoteException} with a {@code message}.
     *
     * @param message the message
     */
    public NotAllowedToJoinVoteException(String message) {
        super(message);
    }
}
