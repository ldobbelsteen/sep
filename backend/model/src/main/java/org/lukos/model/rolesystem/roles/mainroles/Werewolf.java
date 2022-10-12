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
import java.util.ArrayList;
import java.util.List;

/**
 * Initial class for the werewolf-role
 * <p>
 * The werewolves are part of the Werewolf goal group and have the goal to eliminate all players not in this goal group.
 * A Werewolf does not have any special abilities.
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
public class Werewolf extends MainRole {

    /**
     * Constructs a {@code Werewolf}
     */
    public Werewolf() {
        super(CharacterConfig.WEREWOLF.getCharacter(), GroupConfig.WEREWOLF.getGroup());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Werewolf cannot perform actions, as it has none!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        return new ArrayList<>();
    }

    @Override
    public List<Action> getActions() {
        return new ArrayList<>();
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
    }
}
