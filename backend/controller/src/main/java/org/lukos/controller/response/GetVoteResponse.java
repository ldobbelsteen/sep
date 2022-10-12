package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype for retrieving votes.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class GetVoteResponse extends SuccessResponse {
    /** The currently ongoing votes. */
    private final List<VoteEntry> voteEntries;

    /**
     * Constructor for responses of {@code GetVoteResponse}.
     *
     * @param voteEntries The votes of the response
     */
    public GetVoteResponse(List<VoteEntry> voteEntries) {
        super(null); // TODO: give message
        this.voteEntries = voteEntries;
    }

    /**
     * Returns a list of votes as {@code VoteEntry}s.
     *
     * @return a list of votes
     */
    public List<VoteEntry> getVoteEntries() {
        return voteEntries;
    }
}
