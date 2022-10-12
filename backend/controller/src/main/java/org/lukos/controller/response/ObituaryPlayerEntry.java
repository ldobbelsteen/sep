package org.lukos.controller.response;

/**
 * An entry to hold information about a deceased player.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 15-03-2022
 */
public record ObituaryPlayerEntry(int id, ObituaryEntry obituaryEntry) {

    /**
     * Returns the {@code PlayerStatus} of the deceased player.
     *
     * @return the {@code PlayerStatus}
     */
    public PlayerStatus getPlayerStatus() {
        return PlayerStatus.DECEASED;
    }
}
