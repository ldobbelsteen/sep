package org.lukos.model.winhandler;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@code WinHandler} which checks whether the {@link Group} {@code Arsonist} won.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-03-2022
 */
public class WinArsonist extends WinHandler {

    /** Constructs a {@code WinArsonist}. */
    public WinArsonist() {
        super(Group.ARSONIST);
    }

    /**
     * Constructs a {@code WinArsonist} with the next {@code WinHandler} in the chain.
     *
     * @param next The next {@code WinHandler} in the chain.
     */
    public WinArsonist(WinHandler next) {
        super(Group.ARSONIST, next);
    }

    @Override
    public Group checkWin(List<Player> alivePlayers) throws ReflectiveOperationException, SQLException, GameException {
        return super.checkWin(alivePlayers); // added to avoid errors!
    }
}
