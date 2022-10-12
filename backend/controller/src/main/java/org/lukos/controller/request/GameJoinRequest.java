package org.lukos.controller.request;

/**
 * The request datatype for joining a game.
 *
 * @author Rick van der Heijden (1461923)
 * @since 28-03-2022
 */
public class GameJoinRequest {
    /** The join code of the game. */
    private int joinCode;

    /**
     * Returns the join code of the game.
     *
     * @return the join code
     */
    public int getJoinCode() {
        return this.joinCode;
    }

    /**
     * Sets the join code of the game.
     *
     * @param joinCode the join code
     */
    public void setJoinCode(int joinCode) {
        this.joinCode = joinCode;
    }
}
