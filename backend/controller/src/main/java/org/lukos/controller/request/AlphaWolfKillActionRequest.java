package org.lukos.controller.request;

/**
 * The request datatype for the alpha wolf kill action.
 *
 * @author Rick van der Heijden (1461923)
 * @since 01-04-2022
 */
public class AlphaWolfKillActionRequest {
    /** The ID of either the bridge of the player */
    private int bridgeOrPlayerID;
    /** Boolean whether the given ID is of a player or not */
    private boolean isPlayer;

    /**
     * Returns the ID of either the bridge or the player.
     *
     * @return the ID
     */
    public int getBridgeOrPlayerID() {
        return bridgeOrPlayerID;
    }

    /**
     * Sets the ID of either the bridge or the player.
     *
     * @param bridgeOrPlayerID the ID
     */
    public void setBridgeOrPlayerID(int bridgeOrPlayerID) {
        this.bridgeOrPlayerID = bridgeOrPlayerID;
    }

    /**
     * Returns whether the given ID is from a player.
     *
     * @return whether the given ID is from a player
     */
    public boolean isPlayer() {
        return isPlayer;
    }

    /**
     * Sets whether the given ID is from a player.
     *
     * @param player whether the given ID is from a player
     */
    public void setPlayer(boolean player) {
        isPlayer = player;
    }
}
