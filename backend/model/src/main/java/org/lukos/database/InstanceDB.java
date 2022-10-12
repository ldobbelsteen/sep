package org.lukos.database;

import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.database.util.ReadingHelper.readPlayerIdentifiers;

/**
 * Class for handling database-operations related to users
 *
 * @author Lucas Gether-Rønning
 * @since 12-03-22
 */
public class InstanceDB {

    /**
     * Adds a new instance to the `instance`-table of the database with the values taken from the {@code Instance} given
     * as parameter.
     *
     * @param gameMasterID ID of {@code User} to add to database as gamemaster
     * @param SEED         randomness seed that random actions will use.
     * @return the ID of the newly created {@code Instance}
     * @throws SQLException Exception thrown when inserting into database fails
     */
    public static int addNewInstance(int gameMasterID, String name, int SEED) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO Instance(day, gameMasterID, name, seed) VALUES (?, ?, ?, ?);",
                        // Make sure this uses RETURN_GENERATED_KEYS
                        Statement.RETURN_GENERATED_KEYS);
        // Initialize day to -1
        statement.setInt(1, -1);
        statement.setInt(2, gameMasterID);
        statement.setString(3, name);
        statement.setInt(4, SEED);

        DatabaseConnection.getInstance().writeStatement(statement);
        ResultSet resultSet = statement.getGeneratedKeys();

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        throw new SQLException("No ID was generated. That should not occur...");
    }

    /**
     * Deletes a instance from the `instance`-table of the database with the given ID of an {@code Instance}.
     *
     * @param iid ID of the {@code Instance} provided
     * @return true if the deletion gets executed properly in the database
     * @throws SQLException Exception thrown when deleting from database fails
     */
    public static boolean deleteInstanceByIID(int iid) throws SQLException {
        // TODO: Delete all occurrences of this instance in the whole db
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM Instance WHERE instanceID=?;");

        statement.setInt(1, iid);

        DatabaseConnection.getInstance().writeStatement(statement);
        // TODO: Add informative return
        return true;
    }

    /**
     * Generates a list of all instance IDs stored in the `instance`-table of the database.
     *
     * @return A list of all instance IDs from the database
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<Integer> generateInstanceIDList() throws SQLException {
        List<Integer> instanceIDs = new ArrayList<>(); // list for storing all instanceIDs
        PreparedStatement statement =
                DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT instanceID FROM Instance;");

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        while (resultSet.next()) {
            int instanceID = resultSet.getInt("instanceID"); // reading instanceid from resultset
            instanceIDs.add(instanceID); // adding username to list
        }
        return instanceIDs;
    }

    /**
     * Method used to find a {@code Instance} in the database by its instanceID
     *
     * @param iid InstanceID to find records for in the database
     * @return A ResultSet of the entry in the database with the given instanceID
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static ResultSet findInstanceByID(int iid) throws SQLException {
        // TODO: Potential ResultSet issue
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM Instance WHERE instanceID=?;");

        statement.setInt(1, iid);

        return DatabaseConnection.getInstance().readStatement(statement);
    }

    /**
     * Method used to change the day of an instance.
     *
     * @param iid InstanceID of the instance to change day of
     * @param day Day to set the instance to.
     * @throws SQLException Exception thrown when writing expected query to database fails
     */
    public static void modifyDay(int iid, int day) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Instance SET day=? WHERE instanceID=?;");

        statement.setInt(1, day);
        statement.setInt(2, iid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Method used to change the dayPhase of an instance.
     *
     * @param iid   InstanceID of the instance to change dayPhase of
     * @param phase Phase to set the instance to.
     * @throws SQLException Exception thrown when writing expected query to database fails
     */
    public static void modifyPhase(int iid, DayPhase phase) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Instance SET dayPhase=? WHERE instanceID=?;");

        statement.setString(1, phase.toString());
        statement.setInt(2, iid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Getting PlayerIdentifiers of all alive players of an instance.
     *
     * @param iid InstanceID to get players in
     * @return List of PlayerIdentifiers of all alive players
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<PlayerIdentifier> getAlivePlayers(int iid) throws SQLException { //consider moving into PlayerDB
        List<PlayerIdentifier> players = new ArrayList<>(); // list for storing all alive players

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM Players WHERE alive='ALIVE' AND instanceID=?;");

        statement.setInt(1, iid);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        while (resultSet.next()) {
            int uid = resultSet.getInt("userID"); // reading userID from resultSet
            PlayerIdentifier player = new PlayerIdentifier(iid, uid);
            players.add(player); // adding player to list
        }
        return players;
    }

    /**
     * Getting whether a certain player is alive.
     *
     * @param player {@code PlayerIdentifier} storing the info of the player to check
     * @return whether the player is alive or not
     * @throws SQLException Exception thrown if reading from the database fails
     */
    public static boolean isAlivePlayer(PlayerIdentifier player) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM Players WHERE userID=? AND instanceID=? AND alive='ALIVE';");

        statement.setInt(1, player.userID());
        statement.setInt(2, player.instanceID());

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        return resultSet.next();
    }

    /**
     * Setting the specified player(s) to be alive in a certain instance.
     *
     * @param iid     ID pf the instance the players should be set alive in
     * @param players List of players to set alive
     * @throws SQLException          Exception thrown if writing to the database fails
     * @throws NoSuchPlayerException Exception thrown if certain players are not found in the database
     */
    public static void setAlivePlayer(int iid, List<PlayerIdentifier> players)
            throws SQLException, NoSuchPlayerException {
        for (PlayerIdentifier player : players) {
            if (player.instanceID() != iid) {
                throw new NoSuchPlayerException("The player is not from this game.");
            }
            int uid = player.userID();
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("UPDATE Players SET alive='ALIVE' WHERE userID=? AND instanceID=?;");

            statement.setInt(1, uid);
            statement.setInt(2, iid);

            DatabaseConnection.getInstance().writeStatement(statement);
        }
    }

    /**
     * Killing a certain player in an instance, setting their alive status to deceased in the db.
     *
     * @param player {@code PlayerIdentifier} with information about the player to kill
     * @throws SQLException Exception thrown if writing to the database fails (updating)
     */
    public static void killPlayer(PlayerIdentifier player) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "UPDATE Players SET alive='DECEASED', toBeExecuted=false WHERE userID=? AND instanceID=?;");

        statement.setInt(1, player.userID());
        statement.setInt(2, player.instanceID());

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Reviving a certain player in an instance, setting their alive status to alive in the db.
     *
     * @param player {@code PlayerIdentifier} with information about the player to kill
     * @throws SQLException Exception thrown if writing to the database fails (updating)
     */
    public static void revivePlayer(PlayerIdentifier player) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "UPDATE Players SET alive='ALIVE', toBeExecuted=false WHERE userID=? AND instanceID=?;");

        statement.setInt(1, player.userID());
        statement.setInt(2, player.instanceID());

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Getting PlayerIdentifiers of all dead players of an instance.
     *
     * @param instanceID InstanceID to get players in
     * @return List of PlayerIdentifiers of all dead players
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<PlayerIdentifier> getDeadPlayers(int instanceID)
            throws SQLException { //consider moving into PlayerDB
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM Players WHERE alive='DECEASED' AND instanceID=?;");
        statement.setInt(1, instanceID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        return readPlayerIdentifiers(resultSet, instanceID);
    }

    public static String getGameNameByInstanceID(int id) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT name FROM Instance WHERE instanceID=?;");
        statement.setInt(1, id);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        if (resultSet.next()) {
            return resultSet.getString("name");
        }
        throw new SQLException("The ID was not valid.");
    }

    /**
     * Getting the gamemaster of a certain {@code Instance}.
     *
     * @param iid ID of the instance to get the gamemaster in
     * @return The UserID of the gamemaster of the given game
     * @throws SQLException Exception thrown if reading from the database fails
     */
    public static int getGameMaster(int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT gameMasterID FROM Instance WHERE instanceID=?;");
        statement.setObject(1, iid);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        if (resultSet.next()) {
            String gameMasterID = resultSet.getString("gameMasterID");
            return Integer.parseInt(gameMasterID);
        }
        throw new SQLException("The ID was not valid.");
    }

    /**
     * Getting all players that are currently set to be executed.
     *
     * @param iid ID of the instance to check players in
     * @return A list of players that are currently set to be executed
     * @throws SQLException Exception thrown if reading from the database fails
     */
    public static List<PlayerIdentifier> getToBeExecuted(int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT userID FROM Players WHERE instanceID=? AND toBeExecuted=?;");
        statement.setObject(1, iid);
        statement.setInt(2, 1);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        List<PlayerIdentifier> playerIdentifiers = new ArrayList<>();
        while (resultSet.next()) {
            playerIdentifiers.add(new PlayerIdentifier(iid, Integer.parseInt(resultSet.getString("userID"))));
        }
        return playerIdentifiers;
    }

    /**
     * Modifying the toBeExecuted-field of a certain player  to the value specified.
     *
     * @param iid          ID of instance the player is in
     * @param playerID     {@code PlayerIdentifier} to update toBeExecuted for
     * @param toBeExecuted Value to change toBeExecuted to
     * @throws SQLException Exception thrown if writing to the database fails
     */
    //FIXME: Can be moved to PlayerDB
    public static void modifyExecuted(int iid, PlayerIdentifier playerID, boolean toBeExecuted) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Players SET toBeExecuted=? WHERE instanceID=? AND userID=?;");
        statement.setBoolean(1, toBeExecuted);
        statement.setInt(2, iid);
        statement.setInt(3, playerID.userID());

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Getting a list of all players in a given instance.
     *
     * @param iid ID of the instance to get players of.
     * @return A list of all players in the given instance
     * @throws SQLException Exception thrown if reading from the database fails
     */
    public static List<PlayerIdentifier> getPlayers(int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT userID FROM Players WHERE instanceID=?;");
        statement.setObject(1, iid);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        return readPlayerIdentifiers(resultSet, iid);
    }


    /**
     * Setting the gamemaster of a given instance.
     *
     * @param iid        ID of the instance to change the gamemaster in.
     * @param gameMaster ID of the user to set the gamemaster to for the said instance
     * @throws SQLException Exception thrown if writing to the database fails
     */
    public static void setGameMasterByInstance(int iid, int gameMaster) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Instance SET gameMasterID=? WHERE instanceID=?;");
        statement.setObject(1, gameMaster);
        statement.setObject(2, iid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Updating all players in an instance to be alive for the initialization of an instance.
     *
     * @param iid ID of the instance.
     * @throws SQLException Exception thrown if writing to the database fails
     */
    public static void initializeInstanceState(int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Players SET alive='ALIVE' WHERE instanceID=?;");
        statement.setObject(1, iid);

        // Update all the players
        DatabaseConnection.getInstance().writeStatement(statement);
    }
}
