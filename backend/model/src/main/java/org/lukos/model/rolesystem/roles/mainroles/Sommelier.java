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
 * Role: Sommelier
 * <p>
 * The Sommelier is part of the townspeople goal group. Once per day, the Sommelier can visit a player with a nice
 * bottle of wine. This player will be forced to perform their special ability. If applicable, the target of that
 * action, which can be a person or a location, is selected randomly.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Sommelier extends MainRole {
    private boolean visited; //variable keeping track of if the sommelier has used their ability

    /**
     * Constructs a {@code Sommelier}
     *
     * @param visited if the sommelier has used their ability
     */
    public Sommelier(boolean visited) {
        super(CharacterConfig.SOMMELIER.getCharacter(), GroupConfig.SOMMELIER.getGroup());
        this.visited = visited;
    }

    /** Default constructor. */
    public Sommelier() {
        // TODO: Read from config file
        this(false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Sommelier cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Sommelier cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Sommelier does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Sommelier does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Sommelier cannot replenish its actions yet!");
    }
}
