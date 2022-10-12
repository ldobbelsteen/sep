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
 * Role: Cult leader
 * <p>
 * The Cult Leader and their followers want to convert the whole town to their cult. Every evening, the Cult Leader can
 * visit a location to convert everyone who is present at that location to their cult. This fails when players at that
 * location have cult immunity.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class CultLeader extends MainRole {
    private boolean visited; //variable keeping track of if the cult leader has used their ability

    /**
     * Constructs a {@code CultLeader}
     *
     * @param visited if the cult leader has used their ability
     */
    public CultLeader(boolean visited) {
        super(CharacterConfig.CULT_LEADER.getCharacter(), GroupConfig.CULT_LEADER.getGroup());
        this.visited = visited;
    }

    /** Default constructor. */
    public CultLeader() {
        // TODO: Read from config file
        this(false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The CultLeader cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The CultLeader cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The CultLeader does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The CultLeader does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The CultLeader cannot replenish its actions yet!");
    }
}
