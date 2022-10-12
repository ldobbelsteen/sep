package org.lukos.database;

import org.lukos.model.actionsystem.*;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for handling database-operations related to actions.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 28-03-2022
 */
public class ActionsDB {

    /* --- ACTION QUERIES --- */

    /**
     * Add a new action to the database.
     *
     * @param action information about the action
     * @return the ID of the newly created action
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static int addNewAction(ActionDT action) throws SQLException {
        //BEGIN execute main query, containing general information
        // Create main query
        PreparedStatement mainStatement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "INSERT INTO Actions(instanceID, userID, time, name, status, targetType) VALUES (?, ?, ?, ?, " +
                        "'NOT_EXECUTED', ?);", Statement.RETURN_GENERATED_KEYS);

        boolean hasPlayers =
                action.preAction().data().players() != null && action.preAction().data().players().size() > 0;
        boolean hasLocations =
                action.preAction().data().locations() != null && action.preAction().data().locations().size() > 0;
        String targetType;
        if (hasLocations && hasPlayers) {
            targetType = "BOTH";
        } else if (hasPlayers) {
            targetType = "PLAYER";
        } else if (hasLocations) {
            targetType = "LOCATION";
        } else {
            return -1;
        }

        mainStatement.setInt(1, action.preAction().playerIdentifier().instanceID());
        mainStatement.setObject(2,
                action.preAction().playerIdentifier().userID() == -1 ?
                        null : action.preAction().playerIdentifier().userID());
        mainStatement.setTimestamp(3, java.sql.Timestamp.from(action.time()));
        mainStatement.setString(4, action.action().getClass().getSimpleName());
        mainStatement.setString(5, targetType);

        // Execute query
        DatabaseConnection.getInstance().writeStatement(mainStatement);

        // Get the actionID
        ResultSet resultSet = mainStatement.getGeneratedKeys();
        int actionId;
        if (resultSet.next()) {
            // Index 1 returns the generated key
            actionId = resultSet.getInt(1);
        } else {
            throw new SQLException("[addNewAction] No actionID was generated. That should not occur...");
        }
        //END execute main query

        //BEGIN target queries
        // player targets
        if (action.preAction().data().players() != null) {
            for (PlayerIdentifier player : action.preAction().data().players()) {
                PreparedStatement playerStatement = DatabaseConnection.getInstance().getConnect()
                        .prepareStatement("INSERT INTO ActionTargetPlayers(actionID, targetUserID) VALUES (?, ?);");
                playerStatement.setInt(1, actionId);
                playerStatement.setInt(2, player.userID());
                DatabaseConnection.getInstance().writeStatement(playerStatement);
            }
        }

        // location targets
        if (action.preAction().data().locations() != null) {
            for (int locationID : action.preAction().data().locations()) {
                PreparedStatement locationStatement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                        "INSERT INTO ActionTargetLocation(actionID, targetLocationID) VALUES (?, ?);");
                locationStatement.setInt(1, actionId);
                locationStatement.setInt(2, locationID);
                DatabaseConnection.getInstance().writeStatement(locationStatement);
            }
        }

        //END target queries

        return actionId;
    }

    /**
     * Set the status of an {@code Action} to 'EXECUTED'.
     *
     * @param actionID the id of the action
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static void executeAction(int actionID) throws SQLException {
        // Create query
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Actions SET status='EXECUTED' WHERE actionID=?;");
        statement.setInt(1, actionID);

        // Execute query
        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Set the status of an {@code Action} to 'COMPLETED'.
     *
     * @param actionID the id of the action
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static void completeAction(int actionID) throws SQLException {
        // Create query
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Actions SET status='COMPLETED' WHERE actionID=?;");
        statement.setInt(1, actionID);

        // Execute query
        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * get a list of action that have not been executed for an instance with instanceId.
     *
     * @param instanceId the instance id
     * @return a list of non-executed actions
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static ArrayList<Integer> getNotExecutedActions(int instanceId) throws SQLException {
        return ActionsDB.getActions(instanceId, "NOT_EXECUTED");
    }

    /**
     * Get all action that are at a given status.
     *
     * @param instanceId for which instance to get the actions for
     * @param status     the status of the actions
     * @return a list of actions with {@code status}
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static ArrayList<Integer> getActions(int instanceId, String status) throws SQLException {
        // Create query
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT actionID FROM Actions WHERE instanceID=? AND status=?;");
        statement.setInt(1, instanceId);
        statement.setString(2, status);

        // Execute query
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        // Convert result to list
        ArrayList<Integer> returnList = new ArrayList<>();
        while (resultSet.next()) {
            returnList.add(resultSet.getInt("actionID"));
        }
        return returnList;
    }

    /**
     * Get the {@code ActionDT} for an {@code actionId}  from the database.
     *
     * @param actionId the action ID of the action to be fetched
     * @return the {@code ActionDT} for that {@code actionId}
     * @throws SQLException                 Exception thrown for database errors
     * @throws ReflectiveOperationException Catch-all exception
     */
    public static ActionDT getActionFromID(int actionId) throws SQLException, ReflectiveOperationException {

        // Create query for general information
        PreparedStatement generalStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM Actions WHERE actionID=?;");
        generalStatement.setInt(1, actionId);

        // Execute query
        ResultSet generalResultSet = DatabaseConnection.getInstance().readStatement(generalStatement);

        if (!generalResultSet.next()) {
            throw new SQLException("Action with id " + actionId + " was not found!");
        }
        // Assert: found the action in the DB

        int instanceID = generalResultSet.getInt("instanceID");
        ArrayList<PlayerIdentifier> playerList = new ArrayList<>();
        ArrayList<Integer> locationList = new ArrayList<>();
        boolean playerTargets = false;
        boolean locationTargets = false;

        // Check what targets we need to query
        switch (generalResultSet.getString("targetType")) {
            case "PLAYER" -> playerTargets = true;
            case "LOCATION" -> locationTargets = true;
            case "BOTH" -> {
                playerTargets = true;
                locationTargets = true;
            }
        }

        // Get the targets
        if (playerTargets) {
            // Create player query
            PreparedStatement playerStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT targetUserID FROM ActionTargetPlayers WHERE actionID=?;");
            playerStatement.setInt(1, actionId);

            // Execute query
            ResultSet playerResultSet = DatabaseConnection.getInstance().readStatement(playerStatement);

            // Process players
            while (playerResultSet.next()) {
                playerList.add(new PlayerIdentifier(instanceID, playerResultSet.getInt("targetUserID")));
            }
        }
        if (locationTargets) {
            // -- Get all the houses --
            PreparedStatement houseStatement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT targetLocationID FROM ActionTargetLocation WHERE actionID=? AND " +
                            "targetLocationID NOT IN (SELECT bridgeID FROM Bridge);"
                    // IMPORTANT: This query assumes that if the location is not a Bridge, it must be a House!
            );
            houseStatement.setInt(1, actionId);

            // Execute query
            ResultSet housesResultSet = DatabaseConnection.getInstance().readStatement(houseStatement);

            // Process houses
            while (housesResultSet.next()) {
                locationList.add(housesResultSet.getInt("targetLocationID"));
            }

            // -- Get all the bridges --
            PreparedStatement bridgeStatement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT targetLocationID FROM ActionTargetLocation WHERE actionID=? AND " +
                            "targetLocationID IN (SELECT bridgeID FROM Bridge);");
            bridgeStatement.setInt(1, actionId);

            // Execute query
            ResultSet bridgeResultSet = DatabaseConnection.getInstance().readStatement(bridgeStatement);

            // Process bridges
            while (bridgeResultSet.next()) {
                locationList.add(bridgeResultSet.getInt("targetLocationID"));
            }
        }

        // --- Process data ---
        // Package data in correct format
        ActionEnc data = new ActionEnc(locationList, playerList);

        // Define the player that performs the action
        PlayerIdentifier performingPlayer =
                new PlayerIdentifier(generalResultSet.getInt("instanceID"), generalResultSet.getInt("userID"));

        // Create preAction
        PreActionDT preActionDT = new PreActionDT(performingPlayer, data);

        // Create correct Action object
        // Based on https://makeinjava.com/create-object-instance-class-name-using-class-forname-java-examples/
        String actionName = generalResultSet.getString("name");
        Class<?> actionClass = Class.forName("org.lukos.model.actionsystem.actions." + actionName);
        Action actionObject = (Action) actionClass.getDeclaredConstructor().newInstance();

        return new ActionDT(generalResultSet.getTimestamp("time").toInstant(), actionObject, preActionDT);
    }

    /**
     * Find all actions that are marked as 'EXECUTED' for a given instance, if all messages for that actions have been
     * sent, mark the action as 'COMPLETED'.
     *
     * @param instanceID the instance to check.
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static void CheckIfComplete(int instanceID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT act.actionID, log.status FROM Actions act, ActionLogs log " +
                        "WHERE act.instanceID=? AND act.actionID=log.actionID AND act.status='EXECUTED';");

        statement.setInt(1, instanceID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        HashMap<Integer, Boolean> result = new HashMap<>();

        // Check all messages and whether they have been sent
        while (resultSet.next()) {
            int actionId = resultSet.getInt("actionID");
            boolean sent = resultSet.getBoolean("actionID");

            if (result.containsKey(actionId)) {
                result.put(actionId, result.get(actionId) && sent);
            } else {
                result.put(actionId, sent);
            }

            PreparedStatement markAsComplete = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("UPDATE Actions SET status='COMPLETE' WHERE actionID=?;");

            for (int actId : result.keySet()) {
                // If all messages have been sent, we mark the action as 'COMPLETE'
                if (result.get(actId)) {
                    markAsComplete.setInt(1, actId);
                    DatabaseConnection.getInstance().writeStatement(markAsComplete);
                }
            }
        }
    }
}
