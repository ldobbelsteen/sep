package org.lukos.controller.response;

import org.lukos.model.voting.Ballot;

/**
 * An entry to hold information about a {@link Ballot}.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public record BallotEntry(int player, int target) {
}
