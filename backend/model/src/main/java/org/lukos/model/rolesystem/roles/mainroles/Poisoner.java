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
 * Role: Poisoner
 * <p>
 * The Poisoner is part of the townspeople goal group. Every night, the Poisoner can decide to poison the breakfast at a
 * location of their choice. Every player who stayed at that location for that night will die in the morning. However,
 * performing this action consumes a jar of poison. The Poisoner is provided with one such jar of poison at the start of
 * the game.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Poisoner extends MainRole {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.POISON);
    }

    /**
     * Constructs a {@code Poisoner}
     *
     * @param poison how much poison is still available
     */
    public Poisoner(int poison) {
        super(CharacterConfig.POISONER.getCharacter(), GroupConfig.POISONER.getGroup());
    }

    /** Default constructor. */
    public Poisoner() {
        // TODO: Read from config file
        this(1);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        addPlayerItem(player, "poison");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        if (!isAlivePlayer(data.playerIdentifier())) {
            throwNotAllowedToPerformAction("You must be alive to perform this action.");
        }
        if (amountOfItems(data.playerIdentifier(), "poison") <= 0) {
            throwNotAllowedToPerformAction("You do not have any of these actions left");
        }
        if (action != Action.POISON) {
            throwNoSuchAction("That action does not exist!");
        }
        ActionEnc actionData = data.data();
        if (actionData.locations().size() != 1 || !actionData.players().isEmpty()) {
            throwWrongInputException("You can only select 1 player's location for this action!");
        }

        performPoisonerAction(data);
        deletePlayerItem(data.playerIdentifier(), "poison");
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws GameException, SQLException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();
        if (amountOfItems(player, "poison") > 0 && isAlivePlayer(player) && isEvening(instance)) {
            roleActionInformationList.add(
                    new RoleActionInformation(Action.POISON, getLocationsAsEligible(instance), 1));
        }
        return roleActionInformationList;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) throws SQLException, GameException {
    }
}
