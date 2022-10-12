package org.lukos.model.rolesystem.roles.mainroles;

import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.config.CharacterConfig;
import org.lukos.model.config.GroupConfig;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * Role: Scout
 * <p>
 * The Scout is part of the townspeople goal group. A player with the role of Scout has their own communication channel,
 * to which they can add at most 4 other players of their choosing. The Scout can also remove these players from the
 * channel whenever they would like to do so.
 *
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class Scout extends MainRole {

    /**
     * Constructs a {@code Scout}
     */
    public Scout(/*, IChat chat*/) {
        super(CharacterConfig.SCOUT.getCharacter(), GroupConfig.SCOUT.getGroup());
        //this.chat = chat;
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Scout cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Scout cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Scout does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Scout does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Scout cannot replenish its actions yet!");
    }
}
