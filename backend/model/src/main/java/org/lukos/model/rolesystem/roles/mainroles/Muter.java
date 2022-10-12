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
 * Role: Muter
 * <p>
 * The Muter is part of the townspeople goal group. Once a day, the Muter can take away someones right to speak for the
 * remainder of that day.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Muter extends MainRole {
    private boolean muted; // variable keeping track of if the muting-ability is used
    private boolean punished; // variable keeping track of if the muter is currently punished

    /**
     * Constructs a {@code Muter}
     *
     * @param muted    if the muting-ability is used
     * @param punished if the muter is punished
     */
    public Muter(boolean muted, boolean punished) {
        super(CharacterConfig.MUTER.getCharacter(), GroupConfig.MUTER.getGroup());
        this.muted = muted;
        this.punished = punished;
    }

    /** Default constructor. */
    public Muter() {
        // TODO: Read from config file
        this(false, false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Muter cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Muter cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Muter does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Muter does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Muter cannot replenish its actions yet!");
    }
}
