package org.lukos.model.rolesystem.jobs;

import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.config.CharacterConfig;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.rolesystem.util.GeneralPurposeHelper.*;

/**
 * Job: Mayor
 * <p>
 * On the first in-game day, the Mayor is chosen by the players. The Mayor will have the final say if there is a tie
 * during a vote. The Mayor will always have the character type `not shady', regardless of their other roles, double
 * roles and jobs. The mayor will choose their successor for when they die.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel
 * @author Rick van der Heijden (1461923)
 * @author Marco Pleket (1295713)
 * @since 27-02-2022
 */
public class Mayor extends Job {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.SUCCESSOR_MAYOR);
        actions.add(Action.MAYOR_DECIDE);
    }

    /**
     * Constructs a {@code Mayor}
     */
    public Mayor() {
        super(CharacterConfig.MAYOR.getCharacter());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
    }

    @Override
    public void performAction(PreActionDT data, Action act)
            throws GameException, SQLException, ReflectiveOperationException {
        switch (act) {
            case SUCCESSOR_MAYOR -> performSuccessorAction(data, SuccessorType.MAYOR);
            case MAYOR_DECIDE -> {
                if (getUndecidedLynches(data.playerIdentifier().instanceID()) > 0) {
                    performMayorDecideAction(data);
                } else {
                    throwNotAllowedToPerformAction("There are no undecided lynches at the moment!");
                }
            }
            default -> throwNoSuchAction("That action does not exist!");
        }
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();

        /* Add successor action if none has been submitted yet. */
        if (!existSuccessor(instance.getIid(), SuccessorType.MAYOR)) {
            roleActionInformationList.add(new RoleActionInformation(Action.SUCCESSOR_MAYOR,
                    getAlivePlayersExceptSomeoneAsEligible(instance, player), 1));
        }

        /* Decide on the lynch vote if necessary. */
        if (isExecution(instance)) {
            int undecidedLynches = getUndecidedLynches(instance.getIid());
            if (undecidedLynches > 0) {
                roleActionInformationList.add(
                        new RoleActionInformation(Action.MAYOR_DECIDE, getVoteTiedPlayersAsEligible(instance),
                                undecidedLynches));
            }
        }

        return roleActionInformationList;
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
    }
}
