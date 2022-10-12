package org.lukos.model.rolesystem.roles.doubleroles;

import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.config.CharacterConfig;
import org.lukos.model.config.GroupConfig;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * Double role: Jester
 * <p>
 * The Jester's goal is to be lynched by the other players. The Jester wins the game when they are lynched. In contrast
 * with other players, the Jester may decide to spend a night underneath a bridge instead of their home, even if their
 * home has not been destroyed.
 *
 * @author Lucas Gether-Rønning
 * @since 27-02-2022
 */
public class Jester extends DoubleRole {

    /**
     * Constructs a {@code Jester}
     */
    public Jester() {
        super(CharacterConfig.JESTER.getCharacter(), GroupConfig.JESTER.getGroup());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Jester cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Jester cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Jester does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Jester does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Jester cannot replenish its actions yet!");
    }
}
