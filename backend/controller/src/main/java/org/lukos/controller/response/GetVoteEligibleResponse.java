package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype for retrieving the players that are eligible to vote.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class GetVoteEligibleResponse extends SuccessResponse {
    /** The players eligible to vote. */
    private final List<SimplePlayerEntry> eligible;

    /**
     * Constructor for responses of {@code GetVoteEligibleResponse}.
     *
     * @param eligible The player eligible to vote
     */
    public GetVoteEligibleResponse(List<SimplePlayerEntry> eligible) {
        super(null); // TODO: give message
        this.eligible = eligible;
    }

    /**
     * Returns a list of eligible players to vote.
     *
     * @return a list of eligible players
     */
    public List<SimplePlayerEntry> getEligible() {
        return eligible;
    }
}
