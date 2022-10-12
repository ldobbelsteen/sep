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
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.rolesystem.util.GeneralPurposeHelper.*;

/**
 * Role: Medium
 * <p>
 * The Medium is part of the townspeople goal group. The Medium can communicate with the dead. Once every game, they can
 * resurrect one player. Upon resurrection, this player will get the same role and double role they had before dying.
 *
 * @author Lucas Gether-Rønning
 * @author Marco Pleket (1295713)
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Medium extends MainRole {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.REVIVE);
    }

    /**
     * Constructs a {@code Medium}
     *
     * @param revives number of revives available
     */
    public Medium(int revives) {
        super(CharacterConfig.MEDIUM.getCharacter(), GroupConfig.MEDIUM.getGroup());
    }

    /** Default constructor. */
    public Medium() {
        // TODO: Read from config file
        this(1);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        addPlayerItem(player, "revive");
    }

    @Override
    public void performAction(PreActionDT data, Action action)
            throws GameException, SQLException, ReflectiveOperationException {
        if (!isAlivePlayer(data.playerIdentifier())) {
            throwNotAllowedToPerformAction("You must be alive to perform this action.");
        }
        if (amountOfItems(data.playerIdentifier(), "revive") <= 0) {
            throwNotAllowedToPerformAction("You do not have any of these actions left");
        }
        if (action != Action.REVIVE) {
            throwNoSuchAction("That action does not exist!");
        }
        if (data.data().players().size() != 1) {
            throwWrongInputException("You can only select 1 player for this action!");
        }

        performMediumAction(data);
        deletePlayerItem(data.playerIdentifier(), "revive");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();
        if (amountOfItems(player, "revive") > 0 && isAlivePlayer(player)) {
            roleActionInformationList.add(
                    new RoleActionInformation(Action.REVIVE, getDeadPlayersAsEligible(instance), 1));
        }
        return roleActionInformationList;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }
}
