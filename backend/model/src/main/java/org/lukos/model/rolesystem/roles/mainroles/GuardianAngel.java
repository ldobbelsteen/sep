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
 * Role: Guardian Angel
 * <p>
 * Part of the townspeople goal group. Once per in-game day, a player with this role can protect one player of their
 * choice. This protected player cannot be killed during the following night. The Guardian Angel can choose to protect
 * themselves
 *
 * @author Lucas Gether-Rønning
 * @author Marco Pleket (1295713)
 * @author Martijn van Andel (1251104)
 * @since 21-02-2022
 */
public class GuardianAngel extends MainRole {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.PROTECT);
    }

    /**
     * Constructs a {@code GuardianAngel}
     *
     * @param leftToSave number of saves remaining
     */
    public GuardianAngel(int leftToSave) {
        super(CharacterConfig.GUARDIAN_ANGEL.getCharacter(), GroupConfig.GUARDIAN_ANGEL.getGroup());
    }

    /** Default constructor. */
    public GuardianAngel() {
        // TODO: Read from config file
        this(1);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        if (amountOfItems(data.playerIdentifier(), "guard") <= 0) {
            throwNotAllowedToPerformAction("You do not have any of these actions left");
        }
        if (action != Action.PROTECT) {
            throwNoSuchAction("That action does not exist!");
        }
        if (!isAlivePlayer(data.playerIdentifier())) {
            throwNotAllowedToPerformAction("You must be alive to perform this action.");
        }
        if (data.data().players().size() != 1) {
            throwWrongInputException("You can only select 1 player for this action!");
        }

        performGuardianAngelAction(data);
        deletePlayerItem(data.playerIdentifier(), "guard");
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();
        if (amountOfItems(player, "guard") > 0 && isAlivePlayer(player) && isEvening(instance)) {
            roleActionInformationList.add(
                    new RoleActionInformation(Action.PROTECT, getAlivePlayersAsEligible(instance), 1));
        }
        return roleActionInformationList;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) throws SQLException, GameException {
        /* Clear items. */
        int tooManyItems = amountOfItems(player, "guard");
        while (tooManyItems > 0) {
            deletePlayerItem(player, "guard");
            tooManyItems--;
        }
        /* Add fresh "guard" item. */
        addPlayerItem(player, "guard");
    }
}
