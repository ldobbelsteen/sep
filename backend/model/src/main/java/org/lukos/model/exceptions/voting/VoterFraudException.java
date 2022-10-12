package org.lukos.model.exceptions.voting;

/**
 * This exception is to report that a {@code Player} tried to submit a {@code Ballot} for another {@code Player}.
 *
 * @author Sander van Heesch (1436708)
 * @since 08-03-2022
 */
public class VoterFraudException extends VotingException {

    /**
     * Constructs a {@code VoterFraudException}.
     */
    public VoterFraudException() {
    }

    /**
     * Constructs a {@code VoterFraudException} with a {@code message}.
     *
     * @param message the message
     */
    public VoterFraudException(String message) {
        super(message);
    }
}
