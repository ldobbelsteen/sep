package org.lukos.database;

import org.lukos.model.exceptions.location.HouseDoesNotExistException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.location.states.Burned;
import org.lukos.model.location.HouseState;
import org.lukos.model.location.states.Cleaned;
import org.lukos.model.location.states.Repaired;
import org.lukos.model.location.states.Soaked;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.lukos.database.util.ReadingHelper.readPlayerIdentifiers;

/**
 * Class for handling database-operations related to houses
 *
 * @author Rick van der Heijden (1461923)
 * @since 05-04-2022
 */
public class HouseDB {

    /**
     * Getting PlayerIdentifiers of all players of an instance without a repaired house.
     *
     * @param instanceID InstanceID to get players in
     * @return List of PlayerIdentifiers of all homeless players
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<PlayerIdentifier> getHomelessPlayers(int instanceID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT * FROM Players WHERE alive='ALIVE' AND instanceID=? AND houseState='BURNED';");
        statement.setInt(1, instanceID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        return readPlayerIdentifiers(resultSet, instanceID);
    }

    /**
     * Getting PlayerIdentifiers of all players of an instance with a repaired house.
     *
     * @param instanceID InstanceID to get players in
     * @return List of PlayerIdentifiers of all homeowners
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<PlayerIdentifier> getHomeOwners(int instanceID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT * FROM Players WHERE alive='ALIVE' AND instanceID=? AND userID NOT IN (SELECT userID FROM " +
                        "Players WHERE houseState='BURNED');");
        statement.setInt(1, instanceID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        return readPlayerIdentifiers(resultSet, instanceID);
    }

    /**
     * Setting the house of a player.
     *
     * @param playerIdentifier Identifier of the player
     * @param houseID          id of the house to assign to the said player
     * @throws SQLException Exception thrown when writing to the database fails
     */
    public static void setHousePlayerByID(PlayerIdentifier playerIdentifier, int houseID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Players SET houseID=? WHERE instanceID=? AND userID=?;");
        statement.setInt(1, houseID);
        statement.setInt(2, playerIdentifier.instanceID());
        statement.setInt(3, playerIdentifier.userID());

        // Execute the database query
        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Getting the house of a player by their identifier.
     *
     * @param playerIdentifier Identifier of the player to retrieve the house of
     * @return The house of the player
     * @throws SQLException               Exception thrown when reading from the database fails
     * @throws HouseDoesNotExistException Exception thrown when a house does not exist for the player
     */
    public static int getHousePlayerByID(PlayerIdentifier playerIdentifier)
            throws SQLException, HouseDoesNotExistException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT houseID FROM Players WHERE instanceID=? AND userID=?;");
        statement.setInt(1, playerIdentifier.instanceID());
        statement.setInt(2, playerIdentifier.userID());

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (resultSet.next()) {
            return resultSet.getInt("houseID");
        }
        throw new HouseDoesNotExistException("This player does not have a house yet!");
    }

    /**
     * Getting whether a given house exists.
     *
     * @param houseID LocationID of the house to check for
     * @return Whether the house exists
     * @throws SQLException Exception thrown if reading from the database fails
     */
    public static boolean existHouseByID(int houseID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM Players WHERE houseID=?;");
        statement.setInt(1, houseID);

        return DatabaseConnection.getInstance().readStatement(statement).next();
    }

    /**
     * If a house does not exist, create a house for the specified player.
     *
     * @param pi            PlayerIdentifier of the player the house belongs to
     * @param houseID       HouseID of the house
     * @param state         state of the house
     * @param houseStateDay stateday of the house
     * @throws SQLException thrown when database-operations fail
     */
    public static void existOrCreateHouseByID(PlayerIdentifier pi, int houseID, HouseState state, int houseStateDay)
            throws SQLException {
        int userID = pi.userID();
        int instanceID = pi.instanceID();
        if (!existHouseByID(houseID)) {
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "UPDATE Players SET houseID=?, houseState=?, houseStateDay=? WHERE userID=? and instanceID=?;");
            statement.setInt(1, houseID);
            statement.setString(2, state.getClass().getSimpleName().toUpperCase());
            statement.setInt(3, houseStateDay);
            statement.setInt(4, userID);
            statement.setInt(5, instanceID);

            DatabaseConnection.getInstance().writeStatement(statement);
        }
    }

    /**
     * Method to get the HouseState of a house by its ID from the database.
     *
     * @param houseID ID of the house to get the HouseState of
     * @return HouseState of the house
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static HouseState getHouseState(int houseID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT houseState FROM Players WHERE houseID=?;");
        statement.setInt(1, houseID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (!resultSet.next()) {
            throw new SQLException("That is an invalid ID!");
        }

        String houseState = resultSet.getString("houseState");
        return switch (houseState) {
            case "REPAIRED" -> Repaired.getInstance();
            case "SOAKED" -> Soaked.getInstance();
            case "BURNED" -> Burned.getInstance();
            case "CLEANED" -> Cleaned.getInstance();
            default -> throw new SQLException(
                    "Invalid ENUM value returned."); // not the most pretty, but it works (for now)
        };
    }

    /**
     * Method to get the HouseStateDay of a house by its ID from the database.
     *
     * @param houseID ID of the house to get the HouseStateDay of
     * @return HouseStateDay of the house
     * @throws SQLException          Exception thrown when reading expected query from database fails
     * @throws NoSuchPlayerException Exception thrown when no player is found with the given house
     */
    public static int getHouseStateDay(int houseID) throws SQLException, NoSuchPlayerException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT houseStateDay FROM Players WHERE houseID=?;");
        statement.setInt(1, houseID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (resultSet.next()) {
            return resultSet.getInt("houseStateDay");
        }
        throw new NoSuchPlayerException("Player has no house.");
    }

    /**
     * Modifies the state of a house in the database.
     *
     * @param houseID ID of house to modify state of.
     * @param state   State to change the houses HouseState into.
     * @throws SQLException Exception thrown when writing to database fails
     */
    public static void modifyHouseState(int houseID, HouseState state) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Players SET houseState=? WHERE houseID=?;");
        statement.setString(1, state.getClass().getSimpleName());
        statement.setInt(2, houseID);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Modifies the stateday of a house in the database.
     *
     * @param houseID  ID of house to modify state of.
     * @param stateDay Stateday to change the house's HouseState into.
     * @throws SQLException Exception thrown when writing to database fails
     */
    public static void modifyHouseStateDay(int houseID, int stateDay) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Players SET houseStateDay=? WHERE houseID=?;");
        statement.setInt(1, stateDay);
        statement.setInt(2, houseID);

        DatabaseConnection.getInstance().writeStatement(statement);
    }
}
