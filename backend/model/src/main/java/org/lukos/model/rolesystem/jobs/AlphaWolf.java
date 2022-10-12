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
 * Job: Alpha Wolf
 * <p>
 * One of the werewolves will be unanimously elected to be the Alpha Wolf by the werewolves. Every night, the Alpha Wolf
 * decides a location where everyone will be eliminated by the werewolves, and goes to kill everyone at this house or
 * bridge. The Alpha Wolf can choose their successor for when they die.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @author Rick van der Heijden (1461923)
 * @author Marco Pleket (1295713)
 * @since 27-02-2022
 */
public class AlphaWolf extends Job {
    private static final List<Action> actions;

    static {
        actions = new ArrayList<>();
        actions.add(Action.ALPHA_WOLF_KILL);
        actions.add(Action.SUCCESSOR_ALPHA_WOLF);
    }

    /**
     * Constructs an {@code AlphaWolf}
     *
     * @param kills number of kills available
     */
    public AlphaWolf(int kills) {
        super(CharacterConfig.ALPHA_WOLF.getCharacter());
    }

    /** Default constructor. */
    public AlphaWolf() {
        // TODO: Read from config file
        this(1);
    }

    @Override
    public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
        addPlayerItem(player, "kill");
    }

    @Override
    public void performAction(PreActionDT data, Action act) throws GameException, SQLException {
        switch (act) {
            case ALPHA_WOLF_KILL -> {
                int kills = amountOfItems(data.playerIdentifier(), "kill");
                if (kills <= 0) {
                    throwNotAllowedToPerformAction("You do not have any of these actions left");
                }
                alphaWolfCheck(data, kills);

                performAlphaWolfKillAction(data);
                deletePlayerItem(data.playerIdentifier(), "kill");
            }
            case SUCCESSOR_ALPHA_WOLF -> performSuccessorAction(data, SuccessorType.ALPHA_WOLF);
            default -> throwNoSuchAction("That action does not exist!");
        }
    }

    @Override
    public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier player)
            throws SQLException, GameException, ReflectiveOperationException {
        List<RoleActionInformation> roleActionInformationList = new ArrayList<>();

        int kills = amountOfItems(player, "kill");
        if (kills > 0 && isEvening(instance)) {
            roleActionInformationList.add(
                    new RoleActionInformation(Action.ALPHA_WOLF_KILL, getLocationOfNonWolvesAsEligible(instance),
                            kills));
        }

        if (!existSuccessor(instance.getIid(), SuccessorType.ALPHA_WOLF)) {
            roleActionInformationList.add(new RoleActionInformation(Action.SUCCESSOR_ALPHA_WOLF,
                    getAliveWolvesExceptSomeoneAsEligible(instance, player), 1));
        }

        return roleActionInformationList;
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public void replenishAction(int gameSpeed, PlayerIdentifier player) throws SQLException, GameException {
        int amount = gameSpeed - amountOfItems(player, "kill") + 1;
        /* Add kill items until there are 'gamespeed' number of kill items. */
        while (amount > 0) {
            addPlayerItem(player, "kill");
            amount--;
        }
    }
}
