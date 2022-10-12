package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype that sends a list of players.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class ListPlayersResponse extends SuccessResponse {
    /** The list of players of the response. */
    private final List<PlayerEntry> players;

    /**
     * Constructor for responses of {@code ListPlayerResponse}.
     *
     * @param players the players of the response
     */
    public ListPlayersResponse(List<PlayerEntry> players) {
        super(null); // TODO: add message

        this.players = players;
    }

    /**
     * Returns a list of players of the response.
     *
     * @return the list of players
     */
    public List<PlayerEntry> getPlayers() {
        return players;
    }
}
