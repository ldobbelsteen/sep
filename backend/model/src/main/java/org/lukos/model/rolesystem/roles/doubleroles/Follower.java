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
 * Double role: Follower
 * <p>
 * Once a {@code Player} is converted to a cult by a {@code CultLeader} they are turned into a follower, and their goal
 * changes. The {@code Follower} wants to convert the whole town to the cult of that {@code CultLeader} to win.
 *
 * @author Lucas Gether-Rønning
 * @since 27-02-2022
 */
public class Follower extends DoubleRole {

    /**
     * Constructs a {@code Follower}
     */
    public Follower() {
        super(CharacterConfig.FOLLOWER.getCharacter(), GroupConfig.FOLLOWER.getGroup());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Follower cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Follower cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Follower does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Follower does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Follower cannot replenish its actions yet!");
    }
}
