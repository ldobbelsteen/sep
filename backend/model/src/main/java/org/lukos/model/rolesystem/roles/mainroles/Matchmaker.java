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
 * Role: Matchmaker
 * <p>
 * The Matchmaker is part of the townspeople goal group. During the first evening of the game, the Matchmaker will chose
 * two players or two roles. In the case that the Matchmaker selects two roles, the system will, for each of these
 * roles, select one player with that role. The two selected players will become lovers.
 *
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class Matchmaker extends MainRole {

    /**
     * Constructs a {@code Matchmaker}
     */
    public Matchmaker() {
        super(CharacterConfig.MATCHMAKER.getCharacter(), GroupConfig.MATCHMAKER.getGroup());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Matchmaker cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Matchmaker cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Matchmaker does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Matchmaker does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Matchmaker cannot replenish its actions yet!");
    }
}
