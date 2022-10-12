package org.lukos.model.rolesystem.util;

import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.actionsystem.actions.SuccessorAction;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * This class is a facade for performing actions.
 *
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
abstract class RoleActionHelper extends ActionHelper {

    /**
     * This method creates the {@code Archer} action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void performArcherAction(PreActionDT data) throws GameException, SQLException {
        addAction(Instant.now(), RoleActions.KILL_PLAYERS.getAction(), data);
    }

    /**
     * This method creates the {@code Clairvoyant} action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void performClairvoyantAction(PreActionDT data) throws GameException, SQLException {
        addAction(Instant.now(), RoleActions.SEE_ROLE.getAction(), data);
    }

    /**
     * This method creates the {@code Graverobber} action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void performGraverobberAction(PreActionDT data)
            throws GameException, SQLException, ReflectiveOperationException {
        performActionNow(data, RoleActions.CHANGE_ROLE.getAction());
    }

    /**
     * This method creates the {@code Mayor} tie-break decision action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void performMayorDecideAction(PreActionDT data)
            throws SQLException, GameException, ReflectiveOperationException {
        performActionNow(data, RoleActions.MAYOR_DECIDE.getAction());
    }

    /**
     * This method creates the {@code GuardianAngel} action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void performGuardianAngelAction(PreActionDT data) throws GameException, SQLException {
        addAction(Instant.now(), RoleActions.PROTECT_PLAYERS.getAction(), data);
    }

    /**
     * This method creates the {@code Healer} action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void performHealerAction(PreActionDT data) throws GameException, SQLException {
        addAction(Instant.now(), RoleActions.HEAL_PLAYER.getAction(), data);
    }

    /**
     * This method creates the {@code Medium} action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void performMediumAction(PreActionDT data)
            throws GameException, SQLException, ReflectiveOperationException {
        performActionNow(data, RoleActions.REVIVE_PLAYERS.getAction());
    }

    /**
     * This method creates the {@code Poisoner} action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void performPoisonerAction(PreActionDT data) throws GameException, SQLException {
        addAction(Instant.now().plus(12, ChronoUnit.HOURS), RoleActions.KILL_PLAYERS.getAction(), data);
    }

    /**
     * This method creates the {@code PrivateInvestigator} action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void performPrivateInvestigatorAction(PreActionDT data) throws GameException, SQLException {
        addAction(Instant.now(), RoleActions.SEE_CHARACTER.getAction(), data);
    }

    /**
     * This method creates the successor action and passes it on to the {@code ActionManager}.
     *
     * @param data          the data used by the action
     * @param successorType the type of successor this action is used for
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void performSuccessorAction(PreActionDT data, SuccessorType successorType)
            throws SQLException, GameException {
        IInstance instance = getInstanceByID(data.playerIdentifier().instanceID());
        if (existSuccessor(instance.getIid(), successorType)) {
            throwNotAllowedToPerformAction("You have already chosen a successor!");
        }
        SuccessorAction.execute(data, successorType);
    }

    /**
     * This method creates the {@code AlphaWolf} kill action and passes it on to the {@code ActionManager}.
     *
     * @param data the data used by the action
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static void performAlphaWolfKillAction(PreActionDT data) throws GameException, SQLException {
        /* Move to each given location and kill the present players. This takes 5 minutes per killing. */
        Instant time = Instant.now();
        for (int location : data.data().locations()) {
            ArrayList<Integer> oneLocation = new ArrayList<>();
            oneLocation.add(location);
            PreActionDT preActionDT =
                    new PreActionDT(data.playerIdentifier(), createNewActionEnc(oneLocation, data.data().players()));
            addAction(time, RoleActions.MOVE_TO_LOCATION.getAction(), preActionDT);
            addAction(time, RoleActions.KILL_PLAYERS.getAction(), preActionDT);
            time.plus(5, ChronoUnit.MINUTES);
        }

        /* Move back home after done killing. */
        ArrayList<Integer> houseIDs = new ArrayList<>();
        houseIDs.add(getHouseIDByUserID(data.playerIdentifier().userID()));
        PreActionDT secondData =
                new PreActionDT(data.playerIdentifier(), createNewActionEnc(houseIDs, new ArrayList<>()));

        addAction(time, RoleActions.MOVE_TO_LOCATION.getAction(), secondData);
    }
}
