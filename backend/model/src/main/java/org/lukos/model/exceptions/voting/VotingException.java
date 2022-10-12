package org.lukos.model.exceptions.voting;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.voting.Vote;

/**
 * This exception is to report any bad behaviour relating to and coming from {@link Vote}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 21-02-2022
 */
public abstract class VotingException extends GameException {

    /**
     * Constructs a {@code VotingException}.
     */
    public VotingException() {

    }

    /**
     * Constructs a {@code VotingException} with a {@code message}.
     *
     * @param message the message
     */
    public VotingException(String message) {
        super(message);
    }
}
