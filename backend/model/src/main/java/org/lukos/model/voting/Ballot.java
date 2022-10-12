package org.lukos.model.voting;

import org.lukos.model.user.PlayerIdentifier;

/**
 * Ballot used in votes (immutable).
 *
 * @param player The player who submitted this vote.
 * @param target The player who got voted on.
 * @author Rick van der Heijden (1461923)
 * @since 21-02-2022
 */
public record Ballot(PlayerIdentifier player, PlayerIdentifier target) {
}
