package org.lukos.model.rolesystem.util;

import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.actionsystem.NotAllowedToPerformActionException;
import org.lukos.model.exceptions.actionsystem.WrongInputException;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a facade being used by {@code Purpose}s to do checks.
 *
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
public class GeneralPurposeHelper extends RoleActionHelper {

    /**
     * Returns whether the {@code DayPhase} of the {@code Instance} is {@code Morning}.
     *
     * @param instance the {@code Instance} for which to check the {@code DayPhase}
     * @return whether the {@code DayPhase} of the {@code Instance} is {@code Morning}
     * @throws SQLException  when a database operation fails
     * @throws GameException when a game-logic operation fails
     */
    public static boolean isMorning(IInstance instance) throws SQLException, GameException {
        return instance.getInstanceState().getPhase() == DayPhase.MORNING;
    }

    /**
     * Returns whether the {@code DayPhase} of the {@code Instance} is {@code Execution}.
     *
     * @param instance the {@code Instance} for which to check the {@code DayPhase}
     * @return whether the {@code DayPhase} of the {@code Instance} is {@code Execution}
     * @throws SQLException  when a database operation fails
     * @throws GameException when a game-logic operation fails
     */
    public static boolean isExecution(IInstance instance) throws SQLException, GameException {
        return instance.getInstanceState().getPhase() == DayPhase.EXECUTION;
    }

    /**
     * Returns whether the {@code DayPhase} of the {@code Instance} is {@code Evening}.
     *
     * @param instance the {@code Instance} for which to check the {@code DayPhase}
     * @return whether the {@code DayPhase} of the {@code Instance} is {@code Evening}
     * @throws SQLException  when a database operation fails
     * @throws GameException when a game-logic operation fails
     */
    public static boolean isEvening(IInstance instance) throws SQLException, GameException {
        return instance.getInstanceState().getPhase() == DayPhase.EVENING;
    }

    /**
     * Returns the current day of the {@code Instance}.
     *
     * @param instanceID the ID of {@code Instance} for which to check the current day
     * @return the current day of the {@code Instance}
     * @throws SQLException  when a database operation fails
     * @throws GameException when a game-logic operation fails
     */
    public static int getDayByInstanceID(int instanceID) throws GameException, SQLException {
        return InstanceManager.getInstanceManager().getInstance(instanceID).getInstanceState().getDay();
    }

    /**
     * Checks whether the data for the {@code Action} of the {@code Graverobber} is sufficient.
     *
     * @param data the data for the {@code Action} of the {@code Graverobber}
     * @throws SQLException        when a database operation fails
     * @throws WrongInputException when the data is not sufficient
     */
    public static void graverobberCheck(PreActionDT data) throws SQLException, WrongInputException {
        if (!data.data().locations().isEmpty() || data.data().players().size() != 1) {
            throwWrongInputException("You can only select 1 player for this action!");
        }
        if (isAlivePlayer(data.data().players().get(0))) {
            throwWrongInputException("This player is still alive!");
        }
    }

    /**
     * Checks whether the {@code AlphaWolf} can perform its action.
     *
     * @param data  the data of the action
     * @param kills the amount of kills the {@code AlphaWolf} has left
     * @throws NotAllowedToPerformActionException when the {@code AlphaWolf} is not allowed to perform the action
     */
    public static void alphaWolfCheck(PreActionDT data, int kills) throws NotAllowedToPerformActionException {
        if (data.data().players().size() != 0 || data.data().locations().size() > kills ||
                data.data().locations().size() == 0) {
            throwNotAllowedToPerformAction("The action received the wrong amount of inputs");
        }
    }
}
