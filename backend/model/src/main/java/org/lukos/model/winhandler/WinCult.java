package org.lukos.model.winhandler;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@link WinHandler} which checks whether the {@link Group} {@code CULT} won.
 *
 * @author Rick van der Heijden (1461923)
 * @since 03-03-2022
 */
public class WinCult extends WinHandler {

    /** Constructs a {@code WinCult}. */
    public WinCult() {
        super(Group.CULT);
    }

    /**
     * Constructs a {@code WinCult} with the next {@code WinHandler} in the chain.
     *
     * @param next The next {@code WinHandler} in the chain.
     */
    public WinCult(WinHandler next) {
        super(Group.CULT, next);
    }

    @Override
    public Group checkWin(List<Player> alivePlayers) throws ReflectiveOperationException, SQLException, GameException {
        return super.checkWin(alivePlayers); // added to avoid errors!
    }
}
