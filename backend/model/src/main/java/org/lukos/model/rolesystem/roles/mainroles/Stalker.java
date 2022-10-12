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
 * Role: Stalker
 * <p>
 * The Stalker is part of the townspeople goal group. Every evening, the Stalker decides where they want to stalk. The
 * Stalker will see everyone who comes to and/or leaves the chosen location during that night.
 *
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class Stalker extends MainRole {
    private boolean visited; //variable keeping track of if the stalker has used their ability

    /**
     * Constructs a {@code Stalker}
     *
     * @param visited if the stalker has used their ability
     */
    public Stalker(boolean visited) {
        super(CharacterConfig.STALKER.getCharacter(), GroupConfig.STALKER.getGroup());
        this.visited = visited;
    }

    /** Default constructor. */
    public Stalker() {
        // TODO: Read from config file
        this(false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Stalker cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Stalker cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Stalker does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Stalker does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Stalker cannot replenish its actions yet!");
    }
}
