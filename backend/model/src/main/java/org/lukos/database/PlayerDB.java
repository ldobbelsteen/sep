package org.lukos.database;

import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.location.House;
import org.lukos.model.location.states.Repaired;
import org.lukos.model.notes.Deathnote;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling database-operations related to players.
 *
 * @author Lucas Gether-Rønning
 * @author Martijn van Andel (1251104)
 * @since 12-03-22
 */
public class PlayerDB {

    /**
     * Getting the deathnote of a player.
     *
     * @param playerIdentifier Identifier of the player
     * @return the players deathnote
     * @throws SQLException          Exception thrown reading the deathnote from the database fails
     * @throws NoSuchPlayerException Exception thrown when the player does not exist
     */
    public static Deathnote getDeathNotePlayerByID(PlayerIdentifier playerIdentifier)
            throws SQLException, NoSuchPlayerException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT deathNote, deathnoteIsChangeable FROM Players WHERE instanceID=? AND userID=?;");
        statement.setInt(1, playerIdentifier.instanceID());
        statement.setInt(2, playerIdentifier.userID());

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (resultSet.next()) {
            return new Deathnote(resultSet.getString("deathNote"), resultSet.getBoolean("deathnoteIsChangeable"));
        }
        throw new NoSuchPlayerException("The player does not exist");
    }

    /**
     * Setting the deathnote of a player.
     *
     * @param playerIdentifier Identifier of the player
     * @throws SQLException Exception thrown when writing to the database fails
     */
    public static void setDeathNotePlayerByID(PlayerIdentifier playerIdentifier, Deathnote deathNote)
            throws SQLException {
        String content = deathNote.getContent();
        boolean isChangeable = deathNote.getChangeable();

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "UPDATE Players SET deathNote=?, deathnoteIsChangeable=? WHERE userID=? AND instanceID=?;");
        statement.setString(1, content);
        statement.setBoolean(2, isChangeable);
        statement.setInt(3, playerIdentifier.userID());
        statement.setInt(4, playerIdentifier.instanceID());

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Adds a new player to the `players`-table of the database with the values from the {@code Player} object given as
     * parameter.
     *
     * @param player {@code Player} to add to database
     * @throws SQLException Exception thrown when inserting into database fails
     */
    public static void addNewPlayer(PlayerIdentifier player) throws SQLException {
        int userID = player.userID();
        int instanceID = player.instanceID();
        boolean alive = true;

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO Players(instanceID, userID, alive)" + "VALUES (?, ?, ?);");
        statement.setObject(1, instanceID);
        statement.setObject(2, userID);
        statement.setBoolean(3, alive);

        DatabaseConnection.getInstance().writeStatement(statement);
        // FIXME: Find a way to do this without creating the House due to cyclic dependency

        // Initialize the house as well

        // Initialize the Player's CurrentLocation to their newly created House
        visitLocation(new House(player, Repaired.getInstance()).getId(), player);
    }

    /**
     * Deletes a player from the `players`-table of the database with the given userID and instanceID
     *
     * @param uid UserID provided to delete from database
     * @param iid InstanceID provided to delete from the database
     * @throws SQLException Exception thrown if writing to the database fails
     */
    public static void deletePlayerByID(int uid, int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM Players WHERE userID=? AND instanceID=?;");
        statement.setInt(1, uid);
        statement.setInt(2, iid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Getting a player from a given userID.
     *
     * @param uid ID of the user to get player of.
     * @return A player associated with the user with the given ID.
     * @throws SQLException          Exception thrown when reading from the database fails
     * @throws NoSuchPlayerException Exception thrown when no player is associated with the given ID
     */
    public static PlayerIdentifier getPlayerFromUserByID(int uid) throws SQLException, NoSuchPlayerException {
        PreparedStatement statement =
                DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM Players WHERE userID=?;");
        statement.setInt(1, uid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (resultSet.next()) {
            int iid = resultSet.getInt("instanceID");
            return new PlayerIdentifier(iid, uid);
        }
        throw new NoSuchPlayerException("No player found in database based on the user ID");
    }

    /**
     * Method to change the location of a player in the database.
     *
     * @param locID  ID of location to move player to
     * @param player Player to change location of.
     * @throws SQLException Exception thrown when writing to database fails.
     */
    public static void visitLocation(int locID, PlayerIdentifier player) throws SQLException {
        int userID = player.userID();
        int instanceID = player.instanceID();

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Players SET currentLocation=? WHERE userID=? AND instanceID=?;");
        statement.setInt(1, locID);
        statement.setInt(2, userID);
        statement.setInt(3, instanceID);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Method to get a list of all players at a specified location.
     *
     * @param locID ID of the location to check for players at.
     * @return The list of players at the specified location
     * @throws SQLException Exception thrown when reading from the database fails
     */
    public static List<PlayerIdentifier> getPlayersAtLocation(int locID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT userID, instanceID FROM Players WHERE currentLocation=?;");
        statement.setInt(1, locID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        List<PlayerIdentifier> players = new ArrayList<>();
        while (resultSet.next()) {
            int uid = resultSet.getInt("userID");
            int iid = resultSet.getInt("instanceID");
            PlayerIdentifier player = new PlayerIdentifier(iid, uid);
            players.add(player);
        }
        return players;
    }

    /**
     * Get the {@code userID} of the owner of the house with {@code houseID}
     *
     * @param houseId the house to find the owner of
     * @return the {@code userID} of the owner of the house
     * @throws SQLException
     */
    public static int getOwnerByHouseID(int houseId) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT userID FROM Players WHERE houseID=?");
        statement.setInt(1, houseId);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        resultSet.next();

        return resultSet.getInt("userID");
    }

    /**
     * Returns a list of players that are protected from death. Players can be protected from death as a result of the
     * action of a {@code Player} with the role of Guardian Angel.
     *
     * @param instanceId ID of instance to get protected players in
     * @return A list of players that are protected from death at a given point in an instance
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<PlayerIdentifier> getProtectedPlayers(int instanceId) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT userID FROM Players WHERE instanceID=? AND isProtected=1;");
        statement.setInt(1, instanceId);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        List<PlayerIdentifier> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(new PlayerIdentifier(instanceId, resultSet.getInt("userID")));
        }

        return resultList;
    }

    /**
     * Updates {@code isProtected} for a player.
     *
     * @param playerIdentifier the player
     * @param newValue         the new value for {@code isProtected} for the given player
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static void updateProtected(PlayerIdentifier playerIdentifier, boolean newValue) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Players SET isProtected=? WHERE instanceID=? AND userID=?;");
        statement.setBoolean(1, newValue);
        statement.setInt(2, playerIdentifier.instanceID());
        statement.setInt(3, playerIdentifier.userID());

        DatabaseConnection.getInstance().writeStatement(statement);
    }
}
