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
 * Role: Archer
 * <p>
 * The Archer is part of the townspeople group. Every fourth night (fourth, eighth, twelfth, etc.), the Archer can
 * choose to kill a player of their choice.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Archer extends MainRole {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.SHOOT);
    }

    /**
     * Constructs an {@code Archer}
     *
     * @param leftToKill number of kills left
     */
    public Archer(int leftToKill) {
        super(CharacterConfig.ARCHER.getCharacter(), GroupConfig.ARCHER.getGroup());
    }

    /** Default constructor. */
    public Archer() {
        // TODO: Read from config file
        this(0);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        if (amountOfItems(data.playerIdentifier(), "arrow") <= 0) {
            throwNotAllowedToPerformAction("You do not have any of these actions left");
        }
        if (!isAlivePlayer(data.playerIdentifier())) {
            throwNotAllowedToPerformAction("You must be alive to perform this action.");
        }
        if (action != Action.SHOOT) {
            throwNoSuchAction("That action does not exist!");
        }
        if (data.data().players().size() != 1) {
            throwWrongInputException("You can only select 1 player for this action!");
        }

        performArcherAction(data);
        deletePlayerItem(data.playerIdentifier(), "arrow");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();
        if (amountOfItems(player, "arrow") > 0 && isAlivePlayer(player)) {
            roleActionInformationList.add(
                    new RoleActionInformation(Action.SHOOT, getAlivePlayersAsEligible(instance), 1));
        }
        return roleActionInformationList;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) throws SQLException, GameException {
        int currDay = getDayByInstanceID(player.instanceID());
        /* Clear items. */
        int tooManyItems = amountOfItems(player, "arrow");
        while (tooManyItems > 0) {
            deletePlayerItem(player, "arrow");
            tooManyItems--;
        }
        /* Add item only when gameSpeed updates. */
        if (currDay % 4 == 0 && currDay > 0 && amountOfItems(player, "arrow") <= 0) {
            addPlayerItem(player, "arrow");
        }
    }
}
