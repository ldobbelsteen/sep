package org.lukos.model.exceptions.voting;

/**
 * This exception is to report that the {@code Player} someone tried to vote on is not allowed to get voted on.
 *
 * @author Sander van Heesch (1436708)
 * @since 08-03-2022
 */
public class NotAllowedTargetException extends VotingException {

    /**
     * Constructs a {@code NotAllowedTargetException}.
     */
    public NotAllowedTargetException() {
    }

    /**
     * Constructs a {@code NotAllowedTargetException} with a {@code message}.
     *
     * @param message the message
     */
    public NotAllowedTargetException(String message) {
        super(message);
    }
}
