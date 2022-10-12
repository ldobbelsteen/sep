package org.lukos.database;

import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.actions.ActionMessageDT;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionMessagesDB {

    /* --- MESSAGES QUERIES --- */

    /**
     * Get the ids of the messages that need to be sent to the player with id={@code playerIdentifier}.
     *
     * @param playerIdentifier the player to get the messages fpr.
     * @return a list of all the messages that need to be sent
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static List<Integer> getAllNotSendMessagesForUser(PlayerIdentifier playerIdentifier) throws SQLException {
        // Create query
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT messageID " + "FROM ActionLogs log, Actions act " +
                        "WHERE act.instanceID=? AND act.actionID=log.actionID AND receiverID=? AND log" +
                        ".status='NOT_SENT';");

        statement.setInt(1, playerIdentifier.instanceID());
        statement.setInt(2, playerIdentifier.userID());

        // Execute query
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        List<Integer> messages = new ArrayList<>();

        // Process the messages
        while (resultSet.next()) {
            messages.add(resultSet.getInt("messageID"));
        }

        return messages;
    }

    /**
     * Unlock all LOCKED messages for an instance.
     *
     * @param instanceID the instance
     */
    public static void unlockMessages(int instanceID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT messageID " + "FROM ActionLogs log, Actions act " +
                        "WHERE act.instanceID=? AND act.actionID=log.actionID AND log.status='LOCKED';");
        statement.setInt(1, instanceID);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        PreparedStatement updateStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE ActionLogs SET status='NOT_SENT' WHERE messageID=?;");
        int messageId;
        while (resultSet.next()) {
            messageId = resultSet.getInt("messageID");
            updateStatement.setInt(1, messageId);

            DatabaseConnection.getInstance().writeStatement(updateStatement);
        }
    }

    /**
     * Get the message with id {@code messageId}.
     *
     * @param messageId the message to get.
     * @return the message as {@code ActionMessageDT}
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static ActionMessageDT getMessage(int messageId) throws SQLException {
        // Get messageType
        // Create query
        PreparedStatement typeStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM ActionLogs WHERE messageID=?;");

        typeStatement.setInt(1, messageId);

        // Execute query
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(typeStatement);
        resultSet.next();
        ActionMessages messageType = ActionMessages.valueOf(resultSet.getString("messageType"));

        // Get data
        PreparedStatement dataStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT data, position FROM ActionLogsData WHERE messageID=?;");

        dataStatement.setInt(1, messageId);

        ResultSet dataResultSet = DatabaseConnection.getInstance().readStatement(dataStatement);

        // Get field data
        Map<Integer, String> positionMap = new HashMap<>();
        int size = 0;

        while (dataResultSet.next()) {
            positionMap.put(dataResultSet.getInt("position"), dataResultSet.getString("data"));
            size++;
        }

        // Put the field data in the correct order
        List<String> data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            data.add(positionMap.get(i));
        }

        return new ActionMessageDT(messageType, data);
    }

    /**
     * Get the message with id {@code messageId} and mark the message as sent to user {@code userId}
     *
     * @param messageId the message id
     * @return the message as {@code ActionMessageDT}
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static ActionMessageDT getAndCompleteMessage(int messageId) throws SQLException {
        // Get the message
        ActionMessageDT actionMessageDT = getMessage(messageId);

        // Mark as 'SENT'
        markAsSent(messageId);

        // Return
        return actionMessageDT;
    }

    /**
     * Add a new message to the database.
     *
     * @param message  the message to add
     * @param actionId the action that generated the message
     * @param player   the player to send the message to
     * @return the generated message ID
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static int addNewMessage(ActionMessageDT message, int actionId, PlayerIdentifier player)
            throws SQLException {
        // Create query
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "INSERT INTO ActionLogs(actionID, receiverID, status, messageType) VALUES (?, ?, 'LOCKED', ?);",
                Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, actionId);
        statement.setInt(2, player.userID());
        statement.setString(3, message.messageType().name());

        DatabaseConnection.getInstance().writeStatement(statement);

        ResultSet resultSet = statement.getGeneratedKeys();

        if (resultSet.next()) {
            int messageId = resultSet.getInt(1);

            List<String> data = message.data();

            // Add the fields to the ActionLogsData table
            PreparedStatement dataStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ActionLogsData(messageID, position, data) VALUES (?, ?, ?);");
            dataStatement.setInt(1, messageId);
            for (int i = 0; i < data.size(); i++) {
                dataStatement.setInt(2, i);
                dataStatement.setString(3, data.get(i));
                DatabaseConnection.getInstance().writeStatement(dataStatement);
            }

            return messageId;
        }
        throw new SQLException("[addNewMessage] No messageID was generated. That should not occur...");
    }

    /**
     * Add the same message for multiple receivers
     *
     * @param message  the message to add
     * @param actionId the action that generated the message
     * @param players  the list of players to send the message
     * @return the list of generated message IDs
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static List<Integer> addNewMessage(ActionMessageDT message, int actionId,
                                              List<PlayerIdentifier> players)
            throws SQLException {
        List<Integer> messageIds = new ArrayList<>();
        for (PlayerIdentifier playerIdentifier : players) {
            messageIds.add(addNewMessage(message, actionId, playerIdentifier));
        }
        return messageIds;
    }

    /**
     * Set the status of a message to 'NOT_SENT'. The next time the receiver comes online, they will receive this
     * message.
     *
     * @param messageId the message to unlock.
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static void unlockMessage(int messageId) throws SQLException {
        PreparedStatement updateStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE ActionLogs SET status='NOT_SENT' WHERE messageID=?;");
        updateStatement.setInt(1, messageId);

        DatabaseConnection.getInstance().writeStatement(updateStatement);
    }

    /**
     * Set the status of all messages of a certain action to 'NOT_SENT'. The next time the receiver comes online, they will receive this
     * message.
     *
     * @param actionId the action for which to unlock the messages.
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static void unlockMessageWithActionId(int actionId) throws SQLException {
        PreparedStatement updateStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE ActionLogs SET status='NOT_SENT' WHERE actionID=?;");
        updateStatement.setInt(1, actionId);

        DatabaseConnection.getInstance().writeStatement(updateStatement);
    }

    /**
     * Mark a message as 'SENT'.
     *
     * @param messageId the message to mark
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static void markAsSent(int messageId) throws SQLException {
        // Mark the message as sent
        PreparedStatement updateStatement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE ActionLogs SET status='SENT' WHERE messageID=?;");
        updateStatement.setInt(1, messageId);

        DatabaseConnection.getInstance().writeStatement(updateStatement);
    }
}
