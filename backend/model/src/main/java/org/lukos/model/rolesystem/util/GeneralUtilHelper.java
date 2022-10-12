package org.lukos.model.rolesystem.util;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.actionsystem.NoSuchActionException;
import org.lukos.model.exceptions.actionsystem.NotAllowedToPerformActionException;
import org.lukos.model.exceptions.actionsystem.WrongInputException;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.UserManager;

import java.sql.SQLException;

/**
 * This class is a facade for general things, such as throwing exceptions, retrieving house IDs, or retrieving
 * instances.
 *
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
abstract class GeneralUtilHelper extends InformationHelper {

    /**
     * Throws the {@code NoSuchActionException} with message {@code message}.
     *
     * @param message the message of the exception
     * @throws NoSuchActionException always
     */
    public static void throwNoSuchAction(String message) throws NoSuchActionException {
        throw new NoSuchActionException(message);
    }

    /**
     * Throws the {@code NotAllowedToPerformActionException} with message {@code message}.
     *
     * @param message the message of the exception
     * @throws NotAllowedToPerformActionException always
     */
    public static void throwNotAllowedToPerformAction(String message) throws NotAllowedToPerformActionException {
        throw new NotAllowedToPerformActionException(message);
    }

    /**
     * Throws the {@code WrongInputException} with message {@code message}.
     *
     * @param message the message of the exception
     * @throws WrongInputException always
     */
    public static void throwWrongInputException(String message) throws WrongInputException {
        throw new WrongInputException(message);
    }

    /**
     * Returns the {@code Instance} with ID {@code instanceID}.
     *
     * @param instanceID the ID of the {@code Instance}
     * @return the {@code Instance} with ID {@code instanceID}
     * @throws NoSuchInstanceException when there exist no {@code Instance} with the ID {@code instanceID}
     * @throws SQLException            when a database operation fails
     */
    public static IInstance getInstanceByID(int instanceID) throws NoSuchInstanceException, SQLException {
        return InstanceManager.getInstanceManager().getInstance(instanceID);
    }

    /**
     * Returns the ID of the {@code House} of the {@code Player} with {@code User}ID {@code userID}.
     *
     * @param userID the {@code User}ID of the {@code Player}
     * @return the ID of the {@code House} of the {@code Player} with {@code User}ID {@code userID}
     * @throws GameException when a game-logic operation fails
     * @throws SQLException  when a database operation fails
     */
    public static int getHouseIDByUserID(int userID) throws SQLException, GameException {
        return UserManager.getInstance().getUser(userID).getPlayer().getHouse();
    }
}
