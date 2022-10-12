package org.lukos.controller.request;

/**
 * The request datatype for creating a game.
 *
 * @author Rick van der Heijden (1461923)
 * @since 28-03-2022
 */
public class GameCreateRequest {
    /** The name of the created game. */
    private String gameName;
    /** The maximum amount of players of the created game. */
    private int maxAmountOfPlayers;
    /** The seed to be used in RNG. */
    private int SEED;

    /**
     * Returns the game name of the created game.
     *
     * @return the game name
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Sets the game name of the created game.
     *
     * @param gameName the game name
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Returns the maximum amount of players of the created game.
     *
     * @return the maximum amount of players
     */
    public int getMaxAmountOfPlayers() {
        return maxAmountOfPlayers;
    }

    /**
     * Sets the maximum amount of players of the created game.
     *
     * @param maxAmountOfPlayers the maximum amount of players
     */
    public void setMaxAmountOfPlayers(int maxAmountOfPlayers) {
        this.maxAmountOfPlayers = maxAmountOfPlayers;
    }

    /**
     * Returns the seed of the created game.
     *
     * @return the seed
     */
    public int getSEED() {
        return SEED;
    }

    /**
     * Sets the seed of the created game.
     *
     * @param SEED the seed
     */
    public void setSEED(int SEED) {
        this.SEED = SEED;
    }
}
