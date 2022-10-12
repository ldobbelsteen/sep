package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype that gives a list of {@code ObituaryPlayerEntry}s.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 15-03-2022
 */
public class ListObituaryResponse extends SuccessResponse {
    /** The list of players of the response. */
    private final List<ObituaryPlayerEntry> players;

    /**
     * Constructor for responses of {@code ListObituaryResponse}.
     *
     * @param players the list of players of the response
     */
    public ListObituaryResponse(List<ObituaryPlayerEntry> players) {
        super(null); // TODO: add message

        this.players = players;
    }

    /**
     * Returns the list of players of the response.
     *
     * @return the list of players
     */
    public List<ObituaryPlayerEntry> getPlayers() {
        return players;
    }
}
