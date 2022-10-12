package org.lukos.controller.response;

/**
 * The response datatype that gives information about a single game.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class SingleGameResponse extends SuccessResponse {
    /** The game ID of the game of the response. */
    private final int gameId;

    /**
     * Constructor for responses of {@code SingleGameResponse}.
     *
     * @param gameId the game ID of the game of the response
     */
    public SingleGameResponse(int gameId) {
        // initialize StatusResponse
        super(null); // TODO: add message

        // add gameId to Response
        this.gameId = gameId;
    }

    /**
     * Returns the game ID of the game of the response.
     *
     * @return the game ID of the game
     */
    public int getGameId() {
        return gameId;
    }
}
