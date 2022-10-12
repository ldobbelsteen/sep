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
 * Role: Healer
 * <p>
 * The Healer is part of the townspeople goal group. At the beginning of the game, the healer gets access to one
 * med-kit. Every morning, the Healer receives a list of all players that are dying at that moment. They can decide to
 * use their medkit to save one player. After the Healer has saved a player, they learn why and where that player would
 * have died.
 *
 * @author Lucas Gether-Rønning
 * @author Marco Pleket (1295713)
 * @author Martijn van Andel (1251104)
 * @since 26-02-22
 */
public class Healer extends MainRole {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.HEAL);
    }

    /**
     * Constructs a {@code Healer}
     *
     * @param medkits number of medkits available
     */
    public Healer(int medkits) {
        super(CharacterConfig.HEALER.getCharacter(), GroupConfig.HEALER.getGroup());
    }

    /** Default constructor. */
    public Healer() {
        // TODO: Read from config file
        this(1);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        addPlayerItem(player, "medkit");
    }

    @Override
    public void performAction(PreActionDT data, Action action) throws GameException, SQLException {
        if (!isAlivePlayer(data.playerIdentifier())) {
            throwNotAllowedToPerformAction("You must be alive to perform this action.");
        }
        if (amountOfItems(data.playerIdentifier(), "medkit") <= 0) {
            throwNotAllowedToPerformAction("You do not have any of these actions left");
        }
        if (action != Action.HEAL) {
            throwNoSuchAction("That action does not exist!");
        }
        if (data.data().players().size() != 1) {
            throwWrongInputException("You can only select 1 player for this action!");
        }

        performHealerAction(data);
        deletePlayerItem(data.playerIdentifier(), "medkit");
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();
        if (amountOfItems(player, "medkit") > 0 && isAlivePlayer(player) && isMorning(instance)) {
            roleActionInformationList.add(
                    new RoleActionInformation(Action.HEAL, getToBeExecutedPlayersAsEligible(instance), 1));
        }
        return roleActionInformationList;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) throws SQLException, GameException {
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }
}
