package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype that gives a list of groups.
 *
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
public class GroupResponse extends SuccessResponse {
    /** The list of groups of the response. */
    private final List<GroupEntry> playersGroups;

    /**
     * Constructor for responses of {@code GroupResponse}.
     *
     * @param playersGroups the list of groups of the response
     */
    public GroupResponse(List<GroupEntry> playersGroups) {
        super(null); // TODO: give message
        this.playersGroups = playersGroups;
    }

    /**
     * Returns the list of groups of the response.
     *
     * @return the list of groups
     */
    public List<GroupEntry> getPlayersGroups() {
        return playersGroups;
    }
}
