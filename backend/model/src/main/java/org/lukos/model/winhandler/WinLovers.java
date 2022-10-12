package org.lukos.model.winhandler;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@link WinHandler} which checks whether the {@link Group} {@code LOVERS} won.
 *
 * @author Rick van der Heijden (1461923)
 * @since 03-03-2022
 */
public class WinLovers extends WinHandler {

    /** Constructs a {@code WinLovers}. */
    public WinLovers() {
        super(Group.LOVERS);
    }

    /**
     * Constructs a {@code WinLovers} with the next {@code WinHandler} in the chain.
     *
     * @param next The next {@code WinHandler} in the chain.
     */
    public WinLovers(WinHandler next) {
        super(Group.LOVERS, next);
    }

    @Override
    public Group checkWin(List<Player> alivePlayers) throws ReflectiveOperationException, SQLException, GameException {
        return super.checkWin(alivePlayers); // added to avoid errors!
    }
}
