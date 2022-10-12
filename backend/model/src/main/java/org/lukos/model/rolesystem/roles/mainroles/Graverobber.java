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
 * Role: Graverobber
 * <p>
 * The Graverobber will always lose, unless they use their ability. The Graverobber can steal a role from a dead player.
 * From that moment, the Graverobber will assume the former role of the dead player, which includes the (dis)advantages
 * and the goal of that role.
 *
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class Graverobber extends MainRole {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.ROB_GRAVE);
    }

    /**
     * Constructs a {@code Graverobber}
     */
    public Graverobber() {
        super(CharacterConfig.GRAVEROBBER.getCharacter(), GroupConfig.GRAVEROBBER.getGroup());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        addPlayerItem(player, "grave");
    }

    @Override
    public void performAction(PreActionDT data, Action action)
            throws GameException, SQLException, ReflectiveOperationException {
        if (!isAlivePlayer(data.playerIdentifier())) {
            throwNotAllowedToPerformAction("You must be alive to perform this action.");
        }
        if (amountOfItems(data.playerIdentifier(), "grave") <= 0) {
            throwNotAllowedToPerformAction("You do not have any of these actions left");
        }
        if (action != Action.ROB_GRAVE) {
            throwNoSuchAction("That action does not exist!");
        }
        graverobberCheck(data);

        performGraverobberAction(data);
        deletePlayerItem(data.playerIdentifier(), "grave");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException {
        if (amountOfItems(player, "grave") > 0 && isAlivePlayer(player)) {
            List<RoleActionInformation> roleActionInformationList = new ArrayList<>();
            roleActionInformationList.add(
                    new RoleActionInformation(Action.ROB_GRAVE, getDeadPlayersAsEligible(instance), 1));
            return roleActionInformationList;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) {
    }
}
