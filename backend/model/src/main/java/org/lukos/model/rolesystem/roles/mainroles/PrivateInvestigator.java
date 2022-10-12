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
 * Role: Private Investigator
 * <p>
 * Part of the townspeople goal group. Once per in-game day, the Private Investigator can investigate a few players,
 * where the exact number depends on the number of players. The next day the investigator will learn the selected
 * players’ character types, which can be shady, not shady or vague.
 *
 * @author Lucas Gether-Rønning
 * @author Marco Pleket (1295713)
 * @author Martijn van Andel (1251104)
 * @since 21-02-2022
 */
public class PrivateInvestigator extends MainRole {

    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.PRIVATE_INVESTIGATE);
    }

    /**
     * Constructs a {@code PrivateInvestigator}
     */
    public PrivateInvestigator() {
        super(CharacterConfig.PRIVATE_INVESTIGATOR.getCharacter(), GroupConfig.PRIVATE_INVESTIGATOR.getGroup());
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        if (!isAlivePlayer(data.playerIdentifier())) {
            throwNotAllowedToPerformAction("You must be alive to perform this action.");
        }
        if (amountOfItems(data.playerIdentifier(), "investigation") <= 0) {
            throwNotAllowedToPerformAction("You do not have any of these actions left");
        }
        if (action != Action.PRIVATE_INVESTIGATE) {
            throwNoSuchAction("That action does not exist!");
        }

        performPrivateInvestigatorAction(data);
        deletePlayerItem(data.playerIdentifier(), "investigation");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();
        if (amountOfItems(player, "investigation") > 0 && isAlivePlayer(player) && isEvening(instance)) {
            roleActionInformationList.add(
                    new RoleActionInformation(Action.PRIVATE_INVESTIGATE, getAllPlayersAsEligible(instance), 1));
        }
        return roleActionInformationList;
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) throws GameException, SQLException {
        /* Clear items. */
        int tooManyItems = amountOfItems(player, "investigation");
        while (tooManyItems > 0) {
            deletePlayerItem(player, "investigation");
            tooManyItems--;
        }
        /* Add fresh "investigation" items.
         * if players < 20          -> 1 investigation
         * if 20 <= players < 30    -> 2 investigations
         * if 30 <= players < 40    -> 3 investigations
         * etc.
         */
        int players = getPlayers(player.instanceID()).size();
        int investigations = players / 10;
        if (investigations == 0) {
            investigations++;
        }

        while (investigations > 0) {
            addPlayerItem(player, "investigation");
            investigations--;
        }
    }
}
