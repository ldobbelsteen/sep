package org.lukos.model.winhandler;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link WinHandler} which checks whether the {@link Group} {@code WEREWOLVES} won.
 *
 * @author Rick van der Heijden (1461923)
 * @since 03-03-2022
 */
public class WinWolves extends WinHandler {

    /** Constructs a {@code WinWolves}. */
    public WinWolves() {
        super(Group.WEREWOLVES);
    }

    /**
     * Constructs a {@code WinWolves} with the next {@code WinHandler} in the chain.
     *
     * @param next The next {@code WinHandler} in the chain.
     */
    public WinWolves(WinHandler next) {
        super(Group.WEREWOLVES, next);
    }

    @Override
    public Group checkWin(List<Player> alivePlayers) throws ReflectiveOperationException, SQLException, GameException {
        Set<Group> groups = listGroups(alivePlayers);
        return groups.stream().allMatch(group -> group == Group.WEREWOLVES) ? Group.WEREWOLVES :
               super.checkWin(alivePlayers);
    }
}
