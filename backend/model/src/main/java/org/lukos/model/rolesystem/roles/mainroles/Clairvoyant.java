package org.lukos.model.rolesystem.roles.mainroles;

import org.lukos.model.actionsystem.ActionEnc;
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
 * Role: Clairvoyant
 * <p>
 * The Clairvoyant is part of the townspeople group. Every in-game day, the Clairvoyant can select one player. The role
 * of that player is then revealed to the Clairvoyant.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel
 * @author Marco Pleket (1295713)
 * @since 26-02-22
 */
public class Clairvoyant extends MainRole {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.CLAIRVOYANT_SEE_ROLE);
    }

    /**
     * Constructs a {@code Clairvoyant}
     *
     * @param seen if player has used their ability
     */
    public Clairvoyant(boolean seen) {
        super(CharacterConfig.CLAIRVOYANT.getCharacter(), GroupConfig.CLAIRVOYANT.getGroup());
    }

    /** Default constructor. */
    public Clairvoyant() {
        // TODO: Read from config file
        this(false);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException {
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        if (!isAlivePlayer(data.playerIdentifier())) {
            throwNotAllowedToPerformAction("You must be alive to perform this action.");
        }
        if (amountOfItems(data.playerIdentifier(), "seen") <= 0) {
            throwNotAllowedToPerformAction("You do not have any of these actions left");
        }
        if (action != Action.CLAIRVOYANT_SEE_ROLE) {
            throwNoSuchAction("That action does not exist!");
        }
        ActionEnc actionData = data.data();
        if (!actionData.locations().isEmpty() || actionData.players().size() != 1) {
            throwWrongInputException("You can only select 1 player for this action!");
        }

        performClairvoyantAction(data);
        deletePlayerItem(data.playerIdentifier(), "seen");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();
        if (amountOfItems(player, "seen") > 0 && isAlivePlayer(player) && isEvening(instance)) {
            roleActionInformationList.add(
                    new RoleActionInformation(Action.CLAIRVOYANT_SEE_ROLE, getAllPlayersAsEligible(instance), 1));
        }
        return roleActionInformationList;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) throws SQLException, GameException {
        /* Clear items. */
        int tooManyItems = amountOfItems(player, "seen");
        while (tooManyItems > 0) {
            deletePlayerItem(player, "seen");
            tooManyItems--;
        }
        /* Add fresh "seen" item. */
        addPlayerItem(player, "seen");
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }
}
