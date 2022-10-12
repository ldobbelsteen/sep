package org.lukos.controller.response;

/**
 * An entry that holds information about an id.
 *
 * @param id       the id
 * @param isPlayer whether the id is of a player
 * @author Rick van der Heijden (1461923)
 * @since 03-04-2022
 */
public record IdEntry(int id, boolean isPlayer) {
}
