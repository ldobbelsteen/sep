package org.lukos.controller.request;

/**
 * The request datatype for submitting a ballot to a vote.
 *
 * @author Rick van der Heijden (1461923)
 * @since 28-03-2022
 */
public class VoteSubmitRequest {
    /** The ID of the target of the ballot. */
    private int targetID;

    /**
     * Returns the ID of the target of the ballot.
     *
     * @return the ID of the target
     */
    public int getTargetID() {
        return targetID;
    }

    /**
     * Sets the ID of the target of the ballot.
     *
     * @param targetID the ID of the target
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
}
