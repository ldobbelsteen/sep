package org.lukos.database;

import org.lukos.model.exceptions.user.NoSuchRoleException;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.Purpose;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling database-operations related to roles
 *
 * @author Rick van der Heijden (1461923)
 * @since 05-04-2022
 */
public class RoleDB {

    /**
     * Setting the main role of a player.
     *
     * @param playerIdentifier Identifier of the player
     * @param mainRole         Main role of the player
     * @throws SQLException                 Exception thrown when writing to the database fails
     * @throws ReflectiveOperationException Catch-all exception
     */
    public static void setMainRolePlayerByID(PlayerIdentifier playerIdentifier, MainRole mainRole)
            throws SQLException, ReflectiveOperationException {
        boolean exist = true;
        try {
            getMainRolePlayerByID(playerIdentifier);
        } catch (NoSuchRoleException e) {
            exist = false;
        }

        PreparedStatement statement;
        if (exist) {
            statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "UPDATE Roles SET purpose=? WHERE instanceID=? AND userID=? AND purposeType='mainRole';"); //
            // FIXME: in which table should this be inserted?
            statement.setString(1, mainRole.getClass().getSimpleName());
            statement.setInt(2, playerIdentifier.instanceID());
            statement.setInt(3, playerIdentifier.userID());
        } else {
            statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "INSERT INTO Roles(userid, instanceid, purposeType, purpose) VALUES(?, ?, ?, ?);");
            statement.setInt(1, playerIdentifier.userID());
            statement.setInt(2, playerIdentifier.instanceID());
            statement.setString(3, "mainRole");
            statement.setString(4, mainRole.getClass().getSimpleName());
        }

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Getting the main role of a player.
     *
     * @param playerIdentifier Identifier of the player to get the main role for
     * @return The main role of the player
     * @throws SQLException                 Exception thrown when reading from the database fails
     * @throws ReflectiveOperationException Catch-all exception
     * @throws NoSuchRoleException          When the {@code Player} does not have a {@code MainRole}
     */
    public static MainRole getMainRolePlayerByID(PlayerIdentifier playerIdentifier)
            throws SQLException, ReflectiveOperationException, NoSuchRoleException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT purpose FROM Roles WHERE userID=? AND instanceID=? AND purposeType=?;");
        statement.setInt(1, playerIdentifier.userID());
        statement.setInt(2, playerIdentifier.instanceID());
        statement.setString(3, "mainRole");

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (resultSet.next()) {
            String purpose = resultSet.getString("purpose");
            // Based on https://makeinjava.com/create-object-instance-class-name-using-class-forname-java-examples/
            Class<?> mainRoleClass = Class.forName("org.lukos.model.rolesystem.roles.mainroles." + purpose);
            return (MainRole) mainRoleClass.getDeclaredConstructor().newInstance();
        }
        throw new NoSuchRoleException("The player does not have a mainrole.");
    }

    /**
     * Getting the double roles of a player by their identifier.
     *
     * @param playerIdentifier Identifier of the player to get the double roles of.
     * @return A list of all double roles of the player.
     * @throws SQLException                 Exception thrown if reading from the database fails
     * @throws ReflectiveOperationException Catch-all exception
     */
    public static ArrayList<DoubleRole> getDoubleRolesPlayerByID(PlayerIdentifier playerIdentifier)
            throws SQLException, ReflectiveOperationException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT purpose FROM Roles WHERE userID=? AND instanceID=? AND purposeType='doubleRole';");
        statement.setInt(1, playerIdentifier.userID());
        statement.setInt(2, playerIdentifier.instanceID());

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        ArrayList<DoubleRole> doubleRoles = new ArrayList<>();
        while (resultSet.next()) {
            String purpose = resultSet.getString("purpose");
            Class<?> doubleRoleClass = Class.forName("org.lukos.model.rolesystem.roles.doubleroles." + purpose);
            doubleRoles.add((DoubleRole) doubleRoleClass.getDeclaredConstructor().newInstance());
        }

        return doubleRoles;
    }

    /**
     * Getting all jobs of a player.
     *
     * @param playerIdentifier Identifier of the player
     * @return A list of all jobs of the player
     * @throws SQLException                 Exception thrown when reading from the database fails
     * @throws ReflectiveOperationException Catch-all exception
     */
    public static ArrayList<Job> getJobsPlayerByID(PlayerIdentifier playerIdentifier)
            throws SQLException, ReflectiveOperationException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT purpose FROM Roles WHERE userID=? AND instanceID=? AND purposeType='job';");
        statement.setInt(1, playerIdentifier.userID());
        statement.setInt(2, playerIdentifier.instanceID());

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        ArrayList<Job> jobs = new ArrayList<>();
        while (resultSet.next()) {
            String purpose = resultSet.getString("purpose");
            Class<?> jobsClass = Class.forName("org.lukos.model.rolesystem.jobs." + purpose);
            jobs.add((Job) jobsClass.getDeclaredConstructor().newInstance());
        }

        return jobs;
    }

    /**
     * Adding a double role to a specified player.
     *
     * @param playerIdentifier Identifier of the player to add a double role to
     * @param doubleRole       The double role to add to the player
     * @throws SQLException Exception thrown if writing to the database fails
     */
    public static void addDoubleRolePlayerByID(PlayerIdentifier playerIdentifier, DoubleRole doubleRole)
            throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "INSERT INTO Roles(userID, instanceID, purposeType, purpose) VALUES (?,?,'doubleRole',?);");

        statement.setInt(1, playerIdentifier.userID());
        statement.setInt(2, playerIdentifier.instanceID());
        statement.setString(3, doubleRole.getClass().getSimpleName());

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Removing a double role from a player.
     *
     * @param playerIdentifier Identifier of the player
     * @param doubleRole       Double role to remove
     * @return true if the role was removed
     * @throws SQLException Exception thrown when writing to the database fails
     */
    public static boolean removeDoubleRolePlayerByID(PlayerIdentifier playerIdentifier, DoubleRole doubleRole)
            throws SQLException {
        return removePurposePlayerByID(playerIdentifier, doubleRole);
    }

    /**
     * Adding a job to a player by their ID.
     *
     * @param playerIdentifier Identifier of the player to add the job to
     * @param job              Job to add to the player
     * @throws SQLException Exception thrown if reading from the database fails
     */
    public static void addJobPlayerByID(PlayerIdentifier playerIdentifier, Job job) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO Roles(userID, instanceID, purposeType, purpose) VALUES (?,?,'job',?);");
        statement.setInt(1, playerIdentifier.userID());
        statement.setInt(2, playerIdentifier.instanceID());
        statement.setString(3, job.getClass().getSimpleName());

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Removing a job from a player.
     *
     * @param playerIdentifier Identifier of the player
     * @param job              Job to remove
     * @return Whether the job was removed successfully
     * @throws SQLException Exception thrown when writing to the database fails
     */
    public static boolean removeJobPlayerByID(PlayerIdentifier playerIdentifier, Job job) throws SQLException {
        return removePurposePlayerByID(playerIdentifier, job);
    }

    /**
     * Removes the given {@code Purpose} from the {@code Player} with ID {@code playerIdentifier}.
     *
     * @param playerIdentifier the ID of the {@code Player}
     * @param purpose          the {@code Purpose} to be removed
     * @return {@code true}
     * @throws SQLException when a database operation fails
     */
    public static boolean removePurposePlayerByID(PlayerIdentifier playerIdentifier, Purpose purpose)
            throws SQLException {
        if (!(purpose instanceof DoubleRole || purpose instanceof Job)) {
            throw new SQLException("Only double roles and jobs can be removed.");
        }

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM Roles WHERE userID=? AND instanceID=? AND purpose=?;");
        statement.setInt(1, playerIdentifier.userID());
        statement.setInt(2, playerIdentifier.instanceID());
        statement.setString(3, purpose.getClass().getSimpleName());

        DatabaseConnection.getInstance().writeStatement(statement);
        return true;
    }
}
