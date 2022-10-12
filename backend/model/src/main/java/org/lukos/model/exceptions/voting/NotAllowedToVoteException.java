package org.lukos.model.exceptions.voting;

/**
 * This exception is to report that the {@code Player} is not allowed to vote.
 *
 * @author Sander van Heesch (1436708)
 * @since 08-03-2022
 */
public class NotAllowedToVoteException extends VotingException {

    /**
     * Constructs a {@code NotAllowedToVoteException}.
     */
    public NotAllowedToVoteException() {
    }

    /**
     * Constructs a {@code NotAllowedToVoteException} with a {@code message}.
     *
     * @param message the message
     */
    public NotAllowedToVoteException(String message) {
        super(message);
    }
}
