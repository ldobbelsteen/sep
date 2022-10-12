package org.lukos.model.voting;

import org.lukos.database.VoteDB;
import org.lukos.model.user.player.Player;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is a player vote, this vote will accept vote for everyone who is an {@link Player} and who is allowed to vote.
 * The list with allowed players to vote will be immutable.
 *
 * @author Rick van der Heijden (1461923)
 * @since 21-02-2022
 */
public class PlayerVote extends Vote {

    /**
     * Constructs a {@code PlayerVote} object for the {@code Vote} with the given ID.
     *
     * @param voteId the ID of the vote
     */
    public PlayerVote(int voteId) {
        super(voteId);
    }

    /**
     * Constructs a player vote from a list with players that are allowed to vote in this vote.
     *
     * @param iid      the ID of the {@code Instance} in which this {@code PlayerVote} is taking place
     * @param voteType the {@code VoteType} of the {@code Vote}
     * @param allowed  the list of {@code Player}s that is allowed to vote or receive votes
     * @throws SQLException when an exception occurs in a database operation
     */
    public PlayerVote(int iid, VoteType voteType, List<Player> allowed) throws SQLException {
        super(iid, voteType);

        Set<PlayerIdentifier> allowedIDs =
                allowed.stream().map(Player::getPlayerIdentifier).collect(Collectors.toSet());
        VoteDB.saveAllowedPlayers(getVid(), allowedIDs);
    }
}
