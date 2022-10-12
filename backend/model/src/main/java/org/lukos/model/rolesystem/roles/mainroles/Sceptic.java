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
 * Role: Sceptic
 * <p>
 * The Sceptic is part of the townspeople goal group. Once per day, the Sceptic can visit a player. This player will be
 * protected from conversion to any cult for three in-game days.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Sceptic extends MainRole {
    private boolean visited; //variable keeping track of if the sceptic has used their ability

    /**
     * Constructs a {@code Sceptic}
     *
     * @param visited if the sceptic has used their ability
     */
    public Sceptic(boolean visited) {
        super(CharacterConfig.SCEPTIC.getCharacter(), GroupConfig.SCEPTIC.getGroup());
        this.visited = visited;
    }

    /** Default constructor. */
    public Sceptic() {
        // TODO: Read from config file
        this(false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Sceptic cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Sceptic cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Sceptic does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Sceptic does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Sceptic cannot replenish its actions yet!");
    }
}
