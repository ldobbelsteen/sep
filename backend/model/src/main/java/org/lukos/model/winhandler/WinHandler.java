package org.lukos.model.winhandler;

import lombok.Getter;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.winhandler.CutOffChainException;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the abstract base class for any {@code WinHandler}. It is a {@code WinHandler}s responsibility to check
 * whether a certain win-condition has been met, if so it will return the group that has won.
 *
 * @author Rick van der Heijden (1461923)
 * @since 03-03-2022
 */
@Getter
public abstract class WinHandler {

    private final Group group;
    private WinHandler next;

    /**
     * Constructs a {@code WinHandler} with a {@link Group}.
     *
     * @param group The {@code Group} of the {@code WinHandler}.
     */
    public WinHandler(Group group) {
        this.group = group;
    }

    /**
     * Constructs a {@code WinHandler} with a {@link Group} and the next {@code WinHandler} in the chain.
     *
     * @param group The {@code Group} of the {@code WinHandler}.
     * @param next  The next {@code WinHandler} in the chain.
     */
    public WinHandler(Group group, WinHandler next) {
        this.group = group;
        this.next = next;
    }

    /**
     * Sets the next {@code WinHandler} in the chain.
     *
     * @param handler The next {@code WinHandler} in the chain.
     * @throws CutOffChainException if the {@code handler} is {@code null} and the current next still has a next.
     */
    public void setNext(WinHandler handler) throws CutOffChainException {
        if (this.next != null && this.next.next != null && handler == null) {
            throw new CutOffChainException("You tried to cut off the chain of the win handlers, that is not possible!");
        }
        this.next = handler;
    }

    /**
     * Returns a {@link Group} if there is a group that has won, or {@code null} if no group won in the {@code Instance}
     * that has been sent with. If the request from the current {@code WinHandler} would result in {@code null} but
     * there is a next {@code WinHandler} then the request will be passed along the chain.
     *
     * @param alivePlayers The players that are still alive and need to be checked on a win condition.
     * @return The {@code Group} that won, or else {@code null}.
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public Group checkWin(List<Player> alivePlayers) throws ReflectiveOperationException, SQLException, GameException {
        return this.next == null ? null : next.checkWin(alivePlayers);
    }

    /**
     * Returns a {@code Set} of {@code Group}s that the {@code alivePlayers} have.
     *
     * @param alivePlayers the {@code Player}s of which the {@code Group}s will be returned
     * @return a {@code Set} of {@code Group}s that the {@code alivePlayers} have.
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    static Set<Group> listGroups(List<Player> alivePlayers)
            throws ReflectiveOperationException, SQLException, GameException {
        Set<Group> groups = new HashSet<>();
        for (Player player : alivePlayers) {
            for (DoubleRole doubleRole : player.getDoubleRoles()) {
                Group doubleRoleGroup = doubleRole.getGroup();
                if (doubleRoleGroup != Group.NONWINNING) {
                    groups.add(doubleRoleGroup);
                }
            }
            Group mainRoleGroup = player.getMainRole().getGroup();
            if (mainRoleGroup != Group.NONWINNING) {
                groups.add(mainRoleGroup);
            }
        }
        return groups;
    }
}
