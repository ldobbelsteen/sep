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
 * Role: Judge
 * <p>
 * The Judge is part of the townspeople goal group. Once per game, before the lynch vote, the Judge can decide that one
 * more player will be killed during the lynch. If the n players with the most votes would originally be killed, the n+1
 * players with the most votes will be killed after the decision of the Judge.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Judge extends MainRole {
    private int judgements; // variable keeping track of how many judgements are available to the judge

    /**
     * Constructs a {@code Judge}
     *
     * @param judgements number of judgements left
     */
    public Judge(int judgements) {
        super(CharacterConfig.JUDGE.getCharacter(), GroupConfig.JUDGE.getGroup());
        this.judgements = judgements;
    }

    /** Default constructor. */
    public Judge() {
        // TODO: Read from config file
        this(1);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        throw new UnsupportedOperationException("The Judge cannot initialize its actions yet!");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        throw new UnsupportedOperationException("The Judge cannot perform actions yet!");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
            throws SQLException, GameException, ReflectiveOperationException {
        throw new UnsupportedOperationException("The Judge does not have action information yet!");
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException("The Judge does not have actions yet!");
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
        throw new UnsupportedOperationException("The Judge cannot replenish its actions yet!");
    }
}
