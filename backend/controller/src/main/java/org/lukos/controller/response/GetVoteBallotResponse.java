package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype for retrieving all ballots of a vote.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class GetVoteBallotResponse extends SuccessResponse {
    /** The ballots sent with this response. */
    private final List<BallotEntry> ballotEntries;

    /**
     * Constructor for responses of {@code GetVoteBallotResponse}.
     *
     * @param ballotEntries The ballots of this response
     */
    public GetVoteBallotResponse(List<BallotEntry> ballotEntries) {
        super(null); // TODO: give message
        this.ballotEntries = ballotEntries;
    }

    /**
     * Returns a list of ballots of this response.
     *
     * @return a list of ballots
     */
    public List<BallotEntry> getBallotEntries() {
        return ballotEntries;
    }
}
