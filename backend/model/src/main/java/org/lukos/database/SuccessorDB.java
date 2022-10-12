package org.lukos.database;

import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for handling database-operations related to the successor database.
 *
 * @author Rick van der Heijden (1461923)
 * @author Lucas Gether-Rønning
 * @since 04-04-2022
 */
public class SuccessorDB {

    /**
     * Private constructor as all methods will be static.
     */
    private SuccessorDB() {
    }

    /**
     * Method to check if there exists a successor in a given instance for a certain type.
     *
     * @param instanceID    The ID of the instance to check for successors in
     * @param successorType Type of successor to check for
     * @return whether there exists a successor of a certain type in the given instance
     * @throws SQLException when reading from the database fails
     */
    public static boolean existSuccessor(int instanceID, SuccessorType successorType) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT userID FROM Successor WHERE instanceID=? AND successorType=?;");
        statement.setInt(1, instanceID);
        statement.setString(2, successorType.toString());

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        return resultSet.next(); // if this is false there are no rows corresponding to the query
    }

    /**
     * Method to modify the successor of a given type in a given instance, or to create it if it doesn't exist.
     *
     * @param instanceID       The ID of the instance to check for successors in
     * @param successorType    Type of successor to check for
     * @param playerIdentifier {@code PlayerIdentifier} of the player to make successor for the given type
     * @throws SQLException when writing to the database fails
     */
    public static void modifyOrCreateSuccessor(int instanceID, SuccessorType successorType,
                                               PlayerIdentifier playerIdentifier) throws SQLException {
        PreparedStatement statement;
        if (existSuccessor(instanceID, successorType)) {
            // modify successor
            statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("UPDATE Successor SET userID=? WHERE instanceID=? AND successorType=?;");
            statement.setInt(1, playerIdentifier.userID());
            statement.setInt(2, instanceID);
            statement.setString(3, successorType.toString());
        } else {
            // create successor
            statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO Successor(instanceID, successorType, userID) VALUES (?, ?, ?);");
            statement.setInt(1, instanceID);
            statement.setString(2, successorType.toString());
            statement.setInt(3, playerIdentifier.userID());
        }
        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Getting a map of all successors and their types in an instance.
     *
     * @param instanceID The ID of the instance to check for successors in
     * @return a map of {@code SuccessorType} and {@code PlayerIdentifier}s of the successor of that type
     * @throws SQLException when reading from the database fails
     */
    public static Map<SuccessorType, PlayerIdentifier> getSuccessors(int instanceID) throws SQLException {
        // get all successors of all types in the instance
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT successorType, userID FROM Successor WHERE instanceID=?;");
        statement.setInt(1, instanceID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        Map<SuccessorType, PlayerIdentifier> successors = new HashMap<>();
        while (resultSet.next()) {
            PlayerIdentifier pi = new PlayerIdentifier(instanceID, resultSet.getInt("userID"));
            successors.put(SuccessorType.valueOf(resultSet.getString("successorType")), pi);
        }
        return successors;
    }

    /**
     * Getting the successor of a certain type in an instance.
     *
     * @param instanceID    The ID of the instance to get the successor in
     * @param successorType The type to check the successor of
     * @return the {@code PlayerIdentifier} of the player that is successor of the given type
     * @throws SQLException          when reading from the database fails
     * @throws NoSuchPlayerException when there is no successor of the asked type in an instance
     */
    public static PlayerIdentifier getSuccessor(int instanceID, SuccessorType successorType)
            throws SQLException, NoSuchPlayerException {
        // gets a successor of an instance of a specific type
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT userID FROM Successor WHERE instanceID=? AND successorType=?;");
        statement.setInt(1, instanceID);
        statement.setString(2, successorType.toString());

        // if the ResultSet is not empty, the id of the successor is fetched from the ResultSet and added to
        // the PlayerIdentifier to be returned
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (resultSet.next()) {
            int successorID = resultSet.getInt("userID");
            return new PlayerIdentifier(instanceID, successorID);
        }
        throw new NoSuchPlayerException("No successor was found of this type in this instance.");
    }

    /**
     * Removing a successor of a given type from a specified instance.
     *
     * @param instanceID    The ID of the instance to remove successor from
     * @param successorType Type of successor to remove
     * @throws SQLException thrown when writing to database fails
     */
    public static void removeSuccessor(int instanceID, SuccessorType successorType) throws SQLException {
        // remove successor from database
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM Successor WHERE instanceID=? and successorType=?;");
        statement.setInt(1, instanceID);
        statement.setString(2, successorType.toString());

        DatabaseConnection.getInstance().writeStatement(statement);
    }
}
