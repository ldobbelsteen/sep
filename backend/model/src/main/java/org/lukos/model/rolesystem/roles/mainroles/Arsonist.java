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
 * Role: Arsonist
 * <p>
 * The Arsonist wants to burn every house in the village (except for their own house). Every in-game day, the Arsonist
 * can choose from three actions: drench a house in benzene, light all drenched houses on fire or clean one house. The
 * number of actions the arsonist can choose to perform is dependent on the game-speed, and is stored in the {@code
 * actions} variable.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Arsonist extends MainRole {
    private int actions; //number of actions an arsonist has left to perform

    /**
     * Constructs a {@code Arsonist}
     *
     * @param actions number of actions available
     */
    public Arsonist(int actions) {
        super(CharacterConfig.ARSONIST.getCharacter(), GroupConfig.ARSONIST.getGroup());
        this.actions = actions;
    }

    /** Default constructor. */
    public Arsonist() {
        // TODO: Read from config file
        this(2);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Arsonist cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Arsonist cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Arsonist does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Arsonist does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Arsonist cannot replenish its actions yet!");
    }
}
