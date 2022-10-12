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
 * Initial class for townspeople
 * <p>
 * This exact role will not be in the final implementation, but will be used as a neutral role for testing 'not shady'
 * roles.
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
public class Townsperson extends MainRole {

    /**
     * Constructs a dud {@code Townsperson}
     */
    public Townsperson() {
        super(CharacterConfig.TOWNSPERSON.getCharacter(), GroupConfig.TOWNSPERSON.getGroup());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Townsperson cannot perform actions, as it has none!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException(
                "The Townsperson does not have action information, as it has no actions!");
    }

    @Override
    public List<Action> getActions() {
        return new ArrayList<>();
    }
}
