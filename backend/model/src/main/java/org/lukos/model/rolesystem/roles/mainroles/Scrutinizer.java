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
 * Role: Scrutinizer
 * <p>
 * The Scrutinizer is part of the townspeople goal group. Once per in-game day, the Scrutinizer can compare 3 players.
 * They will learn whether these three players have the same goal.
 *
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class Scrutinizer extends MainRole {
    private boolean compared; //variable keeping track of if the scrutinizer has used their ability

    /**
     * Constructs a {@code Scrutinizer}
     *
     * @param compared if the scrutinizer has used their ability
     */
    public Scrutinizer(boolean compared) {
        super(CharacterConfig.SCRUTINIZER.getCharacter(), GroupConfig.SCRUTINIZER.getGroup());
        this.compared = compared;
    }

    /** Default constructor. */
    public Scrutinizer() {
        // TODO: Read from config file
        this(false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Scrutinizer cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Scrutinizer cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Scrutinizer does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Scrutinizer does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Scrutinizer cannot replenish its actions yet!");
    }
}
