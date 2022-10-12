package org.lukos.database;

import org.lukos.model.exceptions.location.BridgeDoesNotExistException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucas Gether-Rønning
 * @since 04-04-2022
 */
public class LocationDB {

    /**
     * Checks if a certain bridge exists in a given instance.
     *
     * @param id ID of bridge to check
     * @return if bridge exists
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static boolean bridgeExists(int id) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT name FROM Bridge WHERE bridgeID=?;");
        statement.setInt(1, id);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        return resultSet.next(); // if this is false there are no rows corresponding to the query
    }

    /**
     * Checks if a certain bridge exists in a given instance, if it does not exist it is created.
     *
     * @param iid  InstanceID of instance to check
     * @param id   ID of bridge to check
     * @param name Name of the bridge to check/create
     * @throws SQLException Exception thrown when writing expected query to database fails
     */
    public static void loadOrCreateBridge(int iid, int id, String name) throws SQLException {
        if (!bridgeExists(id)) {
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO Bridge(bridgeID, instanceID, name) VALUES (?, ?, ?);");
            statement.setInt(1, id);
            statement.setInt(2, iid);
            statement.setString(3, name);

            DatabaseConnection.getInstance().writeStatement(statement);
        }
    }

    /**
     * Getting the name of a bridge by its ID.
     *
     * @param id ID of the bridge
     * @return the name of the bridge
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static String getNameBridgeByID(int id) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT name FROM Bridge WHERE bridgeID=?;");
        statement.setInt(1, id);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        if (resultSet.next()) {
            return resultSet.getString("name");
        }
        throw new SQLException("The ID was not valid.");
    }

    /**
     * Getting the instance ID of a bridge by its ID.
     *
     * @param id ID of the bridge
     * @return the name of the bridge
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static int getInstanceIDBridgeByID(int id) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT instanceID FROM Bridge WHERE bridgeID=?;");
        statement.setInt(1, id);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        if (resultSet.next()) {
            return resultSet.getInt("instanceID");
        }
        throw new SQLException("The ID was not valid.");
    }

    /**
     * Gets a  list of all bridges in an {@code Instance}.
     *
     * @param iid The ID of the instance to retrieve bridges of
     * @return A list of all bridges in the instance
     * @throws SQLException                Exception thrown when reading expected query from database fails
     * @throws BridgeDoesNotExistException Exception thrown when bridge does not exist
     */
    public static List<Integer> getBridgesByInstance(int iid) throws SQLException, BridgeDoesNotExistException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT bridgeID, name FROM Bridge WHERE instanceID=?;");
        statement.setObject(1, iid);
        ResultSet rs = DatabaseConnection.getInstance().readStatement(statement);

        List<Integer> bridges = new ArrayList<>();
        while (rs.next()) {
            bridges.add(rs.getInt("bridgeID"));
        }
        return bridges;
    }

    /**
     * Removing a bridge from the database by its instance and a {@code Bridge} object with its ID.
     *
     * @param iid      ID of the instance the bridge belongs in
     * @param bridgeID The ID of the bridge
     * @return true if the bridge was removed successfully
     * @throws SQLException Exception thrown if writing to the database fails
     */
    public static boolean removeBridgeByInstance(int iid, int bridgeID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM Bridge WHERE instanceID=? AND bridgeID=?;");
        statement.setObject(1, iid);
        statement.setObject(2, bridgeID);

        DatabaseConnection.getInstance().writeStatement(statement);
        return true;
    }

    /**
     * Returns a list of all {@code House}s and {@code Bridge}s in the {@code Instance} indicated by {@code iid}.
     *
     * @param iid ID of the instance to get locations of
     * @return A list of locations of a given instance
     * @throws SQLException Exception thrown if reading from the database fails
     */
    public static List<Integer> getLocationsFromInstanceID(int iid) throws SQLException {
        // FIXME: Remove the Location creation with location IDs. Need to create Locations later, but how to differ
        //  between Bridge and House?
        List<Integer> locations = new ArrayList<>();

        // BEGIN retrieve bridges
        PreparedStatement bridgeStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT bridgeID FROM Bridge WHERE instanceID=?;");
        bridgeStatement.setInt(1, iid);

        ResultSet bridgeResultSet = DatabaseConnection.getInstance().readStatement(bridgeStatement);
        while (bridgeResultSet.next()) {
            locations.add(bridgeResultSet.getInt("BridgeID"));
        }

        // BEGIN retrieve houses
        PreparedStatement houseStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT houseID FROM Players WHERE instanceID=?;");
        houseStatement.setInt(1, iid);

        ResultSet houseResultSet = DatabaseConnection.getInstance().readStatement(houseStatement);
        while (houseResultSet.next()) {
            locations.add(houseResultSet.getInt("houseID"));
        }
        return locations;
    }

    /**
     * Creates a new {@code Location} ID.
     * <p>
     * Based on https://stackoverflow.com/a/4246732/2378368
     *
     * @param iid the ID of the {@code Instance} for which this {@code Location} is being created
     * @return the ID of the newly created {@code Location}
     * @throws SQLException Exception thrown if writing to the database fails
     */
    public static int createNewLocation(int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO Location(instanceID) VALUES (?);", Statement.RETURN_GENERATED_KEYS
                        //generated location-id
                );
        statement.setInt(1, iid);

        DatabaseConnection.getInstance().writeStatement(statement);

        ResultSet resultSet = statement.getGeneratedKeys();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        throw new SQLException("No Location ID was generated. That should not occur...");
    }
}
