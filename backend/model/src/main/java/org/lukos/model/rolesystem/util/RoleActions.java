package org.lukos.model.rolesystem.util;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.actions.*;

/**
 * Enumerations for all the different used {@link Action}s.
 *
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
public enum RoleActions {
    KILL_PLAYERS(new KillPlayers()),
    SEE_ROLE(new SeeRole()),
    CHANGE_ROLE(new ChangeRole()),
    MAYOR_DECIDE(new MayorDecide()),
    PROTECT_PLAYERS(new ProtectPlayers()),
    HEAL_PLAYER(new HealPlayer()),
    REVIVE_PLAYERS(new RevivePlayers()),
    SEE_CHARACTER(new SeeCharacter()),
    MOVE_TO_LOCATION(new MoveToLocation()),
    NEW_MAYOR(new NewMayor()),
    KILL_MARKED_PLAYERS_NIGHT(new KillMarkedPlayersNight()),
    KILL_MARKED_PLAYERS_LYNCH(new KillMarkedPlayersLynch());

    /** The {@link Action} associated with the {@link RoleActions}. */
    private final Action action;

    /**
     * Constructs a {@link RoleActions}.
     *
     * @param action the {@link Action} associated with the {@link RoleActions}.
     */
    RoleActions(Action action) {
        this.action = action;
    }

    /**
     * Returns the {@link Action} associated with the {@link RoleActions}.
     *
     * @return the {@link Action} associated with the {@link RoleActions}.
     */
    public Action getAction() {
        return action;
    }
}
