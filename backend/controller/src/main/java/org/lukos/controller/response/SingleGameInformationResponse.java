package org.lukos.controller.response;

/**
 * The response datatype that gives general information about a single game.
 *
 * @author Rick van der Heijden (1461923)
 * @since 15-03-2022
 */
public class SingleGameInformationResponse extends SingleGameResponse {
    /** The name of the game of the response. */
    private final String gameName;
    /** The {@code UUID} of the game master of the game of the response. */
    private final int gameMaster;
    /** A boolean whether the game of the response is started. */
    private final boolean started;
    /** An {@link GameEntry} which holds information about the game of the response. */
    private final GameEntry gameEntry;

    /**
     * Constructor for responses of {@code SingleGameResponse}.
     *
     * @param gameId     the game ID of the game of the response
     * @param gameName   the name of the game of the response
     * @param gameMaster the user ID of the game master for this game
     * @param started    the boolean whether the game has already started
     * @param gameEntry  the {@code GameEntry} of the game
     */
    public SingleGameInformationResponse(int gameId, String gameName, int gameMaster, boolean started,
                                         GameEntry gameEntry) {
        super(gameId);
        this.gameName = gameName;
        this.gameMaster = gameMaster;
        this.started = started;
        this.gameEntry = gameEntry;
    }

    /**
     * Returns the name of the game of the response.
     *
     * @return the name of the game
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Returns the {@code UUID} of the game master of the game of the response.
     *
     * @return the {@code UUID} of the game master
     */
    public int getGameMaster() {
        return this.gameMaster;
    }

    /**
     * Returns the {@code GameEntry} of the game of the response.
     *
     * @return the {@code GameEntry} of the game
     */
    public GameEntry getGameEntry() {
        return this.gameEntry;
    }

    /**
     * Returns whether the game of the response is started.
     *
     * @return whether the game is started
     */
    public boolean isStarted() {
        return this.started;
    }
}
