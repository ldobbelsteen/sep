package org.lukos.model.exceptions.voting;

/**
 * This exception is to report that a {@code Vote} does not exist.
 *
 * @author Martijn van Andel (1251104)
 * @since 07-03-2022
 */
public class NoSuchVoteException extends VotingException {

    /**
     * Constructs a {@code NoSuchVoteException}.
     */
    public NoSuchVoteException() {
    }

    /**
     * Constructs a {@code NoSuchVoteException} with a {@code message}.
     *
     * @param message the message
     */
    public NoSuchVoteException(String message) {
        super(message);
    }
}
