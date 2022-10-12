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
 * Job: Gatekeeper
 * <p>
 * The Gatekeeper will get a list of roles to which the latecomers have been assigned. This list is extended by l/2
 * randomly selected roles, where l is the number of latecomers.
 *
 * @author Lucas Gether-Rønning
 * @since 27-02-2022
 */
public class Gatekeeper extends Job {

    /**
     * Constructs a {@code Gatekeeper}
     */
    public Gatekeeper() {
        super(CharacterConfig.GATEKEEPER.getCharacter());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Gatekeeper cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Gatekeeper cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Gatekeeper does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Gatekeeper does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Gatekeeper cannot replenish its actions yet!");
    }
}
