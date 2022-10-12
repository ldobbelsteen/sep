package org.lukos.model.user.player;

import org.lukos.database.HouseDB;
import org.lukos.database.InstanceDB;
import org.lukos.database.PlayerDB;
import org.lukos.database.RoleDB;
import org.lukos.model.exceptions.location.HouseDoesNotExistException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.exceptions.user.NoSuchRoleException;
import org.lukos.model.notes.Deathnote;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class is used by the {@code Player} as a facade for the database classes. This means that the {@code Player}
 * class does not have to worry about the underlining connection to the database.
 *
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
class PlayerDBHelper {

    /**
     * Private constructor to avoid instance methods.
     */
    private PlayerDBHelper() {
    }

    /**
     * Retrieves a {@code Player}s {@code MainRole} based on their ID.
     *
     * @param playerID the ID of the {@code Player}
     * @return the {@code MainRole} of the {@code Player}
     * @throws NoSuchRoleException          when the {@code MainRole} does not exist
     * @throws ReflectiveOperationException when a reflective operation fails
     * @throws SQLException                 when a database operation fails
     */
    static MainRole getMainRolePlayerByID(PlayerIdentifier playerID)
            throws NoSuchRoleException, ReflectiveOperationException, SQLException {
        return RoleDB.getMainRolePlayerByID(playerID);
    }

    /**
     * Sets the {@code Player}s {@code MainRole} based on their ID.
     *
     * @param playerID the ID of the {@code Player}
     * @param mainRole the new {@code MainRole} of the {@code Player}
     * @throws ReflectiveOperationException when a reflective operation fails
     * @throws SQLException                 when a database operation fails
     */
    static void setMainRolePlayerByID(PlayerIdentifier playerID, MainRole mainRole)
            throws ReflectiveOperationException, SQLException {
        RoleDB.setMainRolePlayerByID(playerID, mainRole);
    }

    /**
     * Retrieves the {@code DoubleRole}s of the {@code Player} with ID {@code playerID}.
     *
     * @param playerID the ID of the {@code Player}
     * @return the {@code DoubleRole}s of the {@code Player}
     * @throws ReflectiveOperationException when a reflective operation fails
     * @throws SQLException                 when a database operation fails
     */
    static ArrayList<DoubleRole> getDoubleRolesPlayerByID(PlayerIdentifier playerID)
            throws ReflectiveOperationException, SQLException {
        return RoleDB.getDoubleRolesPlayerByID(playerID);
    }

    /**
     * Adds a {@code DoubleRole} to a {@code Player} by its ID.
     *
     * @param playerID   the ID of the {code Player}
     * @param doubleRole the new {@code DoubleRole}
     * @throws SQLException when a database operation fails
     */
    static void addDoubleRolePlayerByID(PlayerIdentifier playerID, DoubleRole doubleRole) throws SQLException {
        RoleDB.addDoubleRolePlayerByID(playerID, doubleRole);
    }

    /**
     * Adds a {@code Job} to a {@code Player} by its ID.
     *
     * @param playerID the ID of the {code Player}
     * @param job      the new {@code Job}
     * @throws SQLException when a database operation fails
     */
    static void addJobPlayerByID(PlayerIdentifier playerID, Job job) throws SQLException {
        RoleDB.addJobPlayerByID(playerID, job);
    }

    /**
     * Removes a {@code Job} to a {@code Player} by its ID.
     *
     * @param playerID the ID of the {code Player}
     * @param job      the {@code Job} to be removed
     * @return whether the {@code Player} had the {@code Job}
     * @throws SQLException when a database operation fails
     */
    static boolean removeJobPlayerByID(PlayerIdentifier playerID, Job job) throws SQLException {
        return RoleDB.removeJobPlayerByID(playerID, job);
    }

    /**
     * Retrieves the {@code Job}s of the {@code Player} with ID {@code playerID}.
     *
     * @param playerID the ID of the {@code Player}
     * @return the {@code Job}s of the {@code Player}
     * @throws ReflectiveOperationException when a reflective operation fails
     * @throws SQLException                 when a database operation fails
     */
    static ArrayList<Job> getJobsPlayerByID(PlayerIdentifier playerID)
            throws ReflectiveOperationException, SQLException {
        return RoleDB.getJobsPlayerByID(playerID);
    }

    /**
     * Retrieves the {@code House} of the {@code Player} with ID {@code playerID}.
     *
     * @param playerID the ID of the {@code Player}
     * @return the {@code House} of the {@code Player}
     * @throws HouseDoesNotExistException when the {@code Player} does not have a house
     * @throws SQLException               when a database operation fails
     */
    static int getHousePlayerByID(PlayerIdentifier playerID) throws HouseDoesNotExistException, SQLException {
        return HouseDB.getHousePlayerByID(playerID);
    }

    /**
     * Sets the {@code House} of a {@code Player} by its ID.
     *
     * @param playerID the ID of the {code Player}
     * @param houseID  the ID of the new {@code House}
     * @throws SQLException when a database operation fails
     */
    static void setHousePlayerByID(PlayerIdentifier playerID, int houseID) throws SQLException {
        HouseDB.setHousePlayerByID(playerID, houseID);
    }

    /**
     * Retrieves the {@code Deathnote} of the {@code Player} with ID {@code playerID}.
     *
     * @param playerID the ID of the {@code Player}
     * @return the {@code Deathnote} of the {@code Player}
     * @throws SQLException          when a database operation fails
     * @throws NoSuchPlayerException when there exist no player with ID {@code playerID}
     */
    static Deathnote getDeathNotePlayerByID(PlayerIdentifier playerID) throws SQLException, NoSuchPlayerException {
        return PlayerDB.getDeathNotePlayerByID(playerID);
    }

    /**
     * Sets the {@code DeathNote} of a {@code Player} by its ID.
     *
     * @param playerID  the ID of the {code Player}
     * @param deathnote the new {@code DeathNote}
     * @throws SQLException when a database operation fails
     */
    static void setDeathNotePlayerByID(PlayerIdentifier playerID, Deathnote deathnote) throws SQLException {
        PlayerDB.setDeathNotePlayerByID(playerID, deathnote);
    }

    /**
     * Returns whether the {@code Player} with ID {@code playerID} is alive.
     *
     * @param playerID the ID of the {@code Player}
     * @return the {@code Player} is alive
     * @throws SQLException when a database operation fails
     */
    static boolean isAlivePlayer(PlayerIdentifier playerID) throws SQLException {
        return InstanceDB.isAlivePlayer(playerID);
    }

    /**
     * Updates whether a {@code Player} is protected to the {@code newValue}.
     *
     * @param playerID the ID of the {@code Player} for which protected is updated
     * @param newValue the new value of protected
     * @throws SQLException when a database operation fails
     */
    static void updateProtected(PlayerIdentifier playerID, boolean newValue) throws SQLException {
        PlayerDB.updateProtected(playerID, newValue);
    }
}
