package org.lukos.database;

import org.lukos.model.chatsystem.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling database operations related to chats
 *
 * @author Marco Pleket (1295713)
 * @since 28-03-2022
 */
public class ChatDB {

    /**
     * Generates a list of all chat IDs stored in the `ChatInstance`-table of the database of the specified game
     * instance.
     *
     * @param iid The game we want to obtain all chatIDs from
     * @return A list of all chat IDs from the database with iid == instanceID
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<ChatIdentifier> findChatsByID(int iid) throws SQLException {
        List<ChatIdentifier> list = new ArrayList<>();

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT chatID, chatType FROM ChatInstance WHERE instanceID=?;");
        statement.setInt(1, iid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        while (resultSet.next()) {
            int chatID = resultSet.getInt("chatID"); // reading chatid from resultset
            ChatType type = ChatType.valueOf(resultSet.getString("chatType")); // reading chatType from resultset
            list.add(new ChatIdentifier(chatID, type)); // adding username to list
        }

        return list;
    }

    /**
     * Generates a list of all chat IDs stored in the `ChatInstance`-table of the database of the specified game
     * instance.
     *
     * @param uid The player we want to obtain all chats from that they have access to
     * @return A list of all chat IDs from the database with uid == instanceID
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<ChatStatus> findChatsByPlayer(int uid) throws SQLException {
        List<ChatStatus> list = new ArrayList<>();

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT ChatInstance.chatID, ChatInstance.chatType, ChatInstance.isOpen FROM ChatInstance, " +
                        "ChatMembers WHERE ChatMembers.userID=? AND ChatInstance.chatID = ChatMembers.chatID;");
        statement.setInt(1, uid);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        while (resultSet.next()) {
            int chatID = resultSet.getInt("chatID"); // reading chatid from resultset
            ChatType type = ChatType.valueOf(resultSet.getString("chatType")); // reading chatType from resultset
            boolean open = resultSet.getBoolean("isOpen");
            list.add(new ChatStatus(chatID, type, open)); // adding username to list
        }

        return list;
    }

    /**
     * Inserts a new entry into ChatMembers, adding a new player to the specified chat
     *
     * @param uid         Player to be added to the chat
     * @param cid         The chat the player should be added to
     * @param writeAccess Whether the player is allowed to write messages in this chat
     * @throws SQLException Exception thrown when writing expected query to database fails
     */
    public static void addChatPlayer(int uid, int cid, boolean writeAccess) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO ChatMembers(userID, chatID, writeAccess) VALUES (?, ?, ?);",
                        // Make sure this uses RETURN_GENERATED_KEYS
                        Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, uid);
        statement.setInt(2, cid);
        statement.setBoolean(3, writeAccess);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Inserts an entry from ChatMembers, removing a player from the specified chat
     *
     * @param uid The player to be removed
     * @param cid The chat the player should be removed from
     * @throws SQLException Exception thrown when writing expected query to database fails
     */
    public static void removeChatPlayer(int uid, int cid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM ChatMembers WHERE ?=userID AND ?=chatID;");
        statement.setInt(1, uid);
        statement.setInt(2, cid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Toggles the access to the chat from read only to read and write, or vice versa for the specified player.
     *
     * @param uid         The player we want to change the writeAccess for
     * @param cid         The chat this player's writeAccess will be updated for
     * @param writeAccess The new writeAccess for that player
     * @throws SQLException Exception thrown when writing expected query to database fails
     */
    public static void toggleWriteAccessPlayer(int uid, int cid, boolean writeAccess) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE ChatMembers SET writeAccess=? WHERE userID=? AND chatID=?;");
        statement.setBoolean(1, writeAccess);
        statement.setInt(2, uid);
        statement.setInt(3, cid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Obtains all players assigned to the specified chat
     *
     * @param cid The chat we want to obtain all players from
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<Integer> getPlayers(int cid) throws SQLException {
        List<Integer> list = new ArrayList<>();

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT userID FROM ChatMembers WHERE ChatMembers.chatID=? AND writeAccess=?;");
        statement.setInt(1, cid);
        statement.setBoolean(2, false);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        while (resultSet.next()) {
            int chatID = resultSet.getInt("chatID"); // reading chatid from resultset
            list.add(chatID); // adding username to list
        }
        return list;
    }

    /**
     * Creates a new chat for the specified game instance of ChatType type
     *
     * @param iid  The game the chat should be created in
     * @param type The type of this new chat
     * @throws SQLException Exception thrown when writing expected query to database fails
     */
    public static int createChat(int iid, ChatType type) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);",
                        // Make sure this uses RETURN_GENERATED_KEYS
                        Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, iid);
        statement.setString(2, type.toString());
        statement.setBoolean(3, true);

        DatabaseConnection.getInstance().writeStatement(statement);
        ResultSet resultSet = statement.getGeneratedKeys();

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        throw new SQLException("No Chat ID was generated. That should not occur...");
    }

    /**
     * Queries the open/closed state of a chat
     *
     * @param cid The chat we want to obtain isOpen from
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static boolean getOpen(int cid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT isOpen FROM ChatInstance WHERE chatID=?;");
        statement.setInt(1, cid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        resultSet.next();

        return resultSet.getBoolean("isOpen");
    }

    /**
     * Submits a new chat message to the ChatMessages table
     *
     * @param uid     The user that submits the message
     * @param cid     The chat the message is submitted to
     * @param message The message that was submitted
     * @param time    The time at which the message was sent (or more precisely, the time at which the message arrived
     *                in the backend)
     * @throws SQLException Exception thrown when writing expected query to database fails
     */
    public static void submitChat(int uid, int cid, String message, Instant time) throws SQLException {

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO ChatMessages(chatID, timeSent, userID, message) VALUES (?, ?, ?, ?);",
                        // Make sure this uses RETURN_GENERATED_KEYS
                        Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, cid);
        statement.setTimestamp(2, java.sql.Timestamp.from(time));
        statement.setInt(3, uid);
        statement.setString(4, message);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Obtains all most recent messages in a chat up to a certain time and amount.
     *
     * @param cid      The chat we want to obtain the message history from
     * @param fromTime The time up to which we search
     * @param amount   The amount of messages we load at most, starting with the most recent messages
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static List<ChatMessage> getMessages(int cid, Instant fromTime, int amount) throws SQLException {
        List<ChatMessage> list = new ArrayList<>();

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT chatID, timeSent, userID, message FROM ChatMessages WHERE chatID=? AND timeSent > ? " +
                        "ORDER BY timeSent ASC limit ?;");
        statement.setInt(1, cid);
        statement.setTimestamp(2, java.sql.Timestamp.from(fromTime));
        statement.setInt(3, amount);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        while (resultSet.next()) {
            list.add(new ChatMessage(resultSet.getInt("chatID"),
                    new MessageEntry(resultSet.getInt("userID"), resultSet.getTimestamp("timeSent").toInstant(),
                            resultSet.getString("message"))));
        }
        return list;
    }

    /**
     * Check whether the user has writing permission in the chat.
     *
     * @param cid The chat we want to check writeAccess for
     * @param uid The user we check the writeAccess for
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static boolean checkWritePermission(int cid, int uid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT writeAccess FROM ChatMembers WHERE chatID=? AND userID=?;");
        statement.setInt(1, cid);
        statement.setInt(2, uid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (resultSet.next()) {
            return resultSet.getBoolean("writeAccess");
        }
        return false;
    }

    /**
     * Check whether the user is muted in the game.
     *
     * @param uid The user whose muted-status will be checked
     * @param iid The game the user is playing in
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static boolean checkMuted(int uid, int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT muted FROM Players WHERE userID=? AND instanceID=?;");
        statement.setInt(1, uid);
        statement.setInt(2, iid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        resultSet.next();
        return resultSet.getBoolean("muted");
    }

    /**
     * Update the isOpen status of the specified chat.
     *
     * @param cid  The chat we want to open or close
     * @param open The updated status of this chat
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static void openChat(int cid, boolean open) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE ChatInstance SET isOpen=? WHERE chatID=?;");
        statement.setInt(1, cid);
        statement.setBoolean(2, open);

        DatabaseConnection.getInstance().writeStatement(statement);
    }
}
