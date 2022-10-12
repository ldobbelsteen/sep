package org.lukos.model.rolesystem.jobs;

import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.config.CharacterConfig;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * Job: Blacksmith
 * <p>
 * The job of {@code Blacksmith} is assigned randomly to a non-werewolf player. If a {@code Werewolf} kills this player,
 * the {@code Werewolf} will die with them. If the {@code WerewolfElder} tries to convert this player to a {@code
 * WolfCub}, the {@code WerewolfElder} will die.
 *
 * @author Lucas Gether-Rønning
 * @since 27-02-2022
 */
public class Blacksmith extends Job {

    /**
     * Constructs a {@code Blacksmith}
     */
    public Blacksmith() {
        super(CharacterConfig.BLACKSMITH.getCharacter());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Blacksmith cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Blacksmith cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Blacksmith does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Blacksmith does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Blacksmith cannot replenish its actions yet!");
    }
}
