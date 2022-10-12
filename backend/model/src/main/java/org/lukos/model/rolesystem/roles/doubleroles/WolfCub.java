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
 * Double role: Wolf cub
 * <p>
 * Once a player is bitten by the Werewolf Elder, they are converted into a Werewolf Cub. While having this role, the
 * player will appear with charactertype Vague, and a nonwinning goal group. One in-game day later, this player shall
 * become a Werewolf and part of the werewolf goal group.
 *
 * @author Lucas Gether-Rønning
 * @since 27-02-2022
 */
public class WolfCub extends DoubleRole {

    /**
     * Constructs a {@code WolfCub}
     */
    public WolfCub() {
        super(CharacterConfig.WOLF_CUB.getCharacter(), GroupConfig.WOLF_CUB.getGroup());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Wolf Cub cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Wolf Cub cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Wolf Cub does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Wolf Cub does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Wolf Cub cannot replenish its actions yet!");
    }
}
