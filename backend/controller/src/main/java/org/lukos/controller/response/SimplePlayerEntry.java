package org.lukos.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * An entry to hold information about a player.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
// Annotation @JsonInclude->NON_NULL Based on https://stackoverflow.com/a/16089705/2378368
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SimplePlayerEntry(int id, PlayerStatus playerStatus, String name) {
}
