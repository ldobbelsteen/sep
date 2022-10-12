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
 * Role: Executioner
 * <p>
 * The Executioner is part of the townspeople goal group. Once per game, the Executioner can decide not to kill the
 * player with the most votes during the lynch vote. Also, once per game, the Executioner can decide to kill one extra
 * player of their choosing during the lynch.
 *
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class Executioner extends MainRole {
    private int leftToSave; // variable keeping track of how many saves are available to the executioner
    private int leftToKill; // variable keeping track of how many kills are available to the executioner

    /**
     * Constructs an {@code Executioner}
     *
     * @param leftToSave number of saves left
     * @param leftToKill number of kills left
     */
    public Executioner(int leftToSave, int leftToKill) {
        super(CharacterConfig.EXECUTIONER.getCharacter(), GroupConfig.EXECUTIONER.getGroup());
        this.leftToSave = leftToSave;
        this.leftToKill = leftToKill;
    }

    /** Default constructor. */
    public Executioner() {
        // TODO: Read from config file
        this(1, 1);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Executioner cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Executioner cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Executioner does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Executioner does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Executioner cannot replenish its actions yet!");
    }
}
