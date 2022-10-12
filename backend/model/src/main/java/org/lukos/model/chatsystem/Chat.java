package org.lukos.model.chatsystem;

import lombok.Getter;
import org.lukos.database.ChatDB;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Maintains the state of a single chat.
 *
 * @author Marco Pleket (1295713)
 * @since 28-03-2022
 */
public class Chat {
    // TODO: Add permission checks

    /**
     * Adds the specified player to the specified chat.
     *
     * @throws SQLException if SQL query fails.
     * @param uid  The user that will be added
     * @param cid  ID of the chat the user is added to
     * @param writeAccess  Whether the user is able to write to the chat.
     */
    public static void addChatPlayer(int uid, int cid, boolean writeAccess) throws SQLException {
        ChatDB.addChatPlayer(uid, cid, writeAccess);
    }

    /**
     * Removes the specified player from the specified chat.
     *
     * @throws SQLException if SQL query fails.
     * @param uid  The user that will be removed
     * @param cid  ID of the chat the user is removed from
     */
    public static void removeChatPlayer(int uid, int cid) throws SQLException {
        ChatDB.removeChatPlayer(uid, cid);
    }

    /**
     * Toggles write access to a chat for a player
     *
     * @throws SQLException if SQL query fails.
     * @param uid  The user whose writeAccess is changed
     * @param cid  ID of the chat we change the writeAccess for
     * @param writeAccess   The user's new writeAccess
     */
    public static void toggleWriteAccessPlayer(int uid, int cid, boolean writeAccess) throws SQLException {
        ChatDB.toggleWriteAccessPlayer(uid, cid, writeAccess);
    }

    /**
     * Obtains all (alive) players of a chat
     *
     * @throws SQLException if SQL query fails.
     * @param cid  ID of the chat we obtain the players from
     */
    public static List<Integer> getPlayers(int cid) throws SQLException {
        return ChatDB.getPlayers(cid);
    }

    /**
     * Checks whether the chat is open
     *
     * @throws SQLException if SQL query fails.
     * @param cid  ID of the chat we obtain the open/closed state from
     */
    public static boolean isOpen(int cid) throws Exception {
        return ChatDB.getOpen(cid);
    }

    /**
     * Submits a new chat to the chat messages
     *
     * @throws SQLException if SQL query fails.
     * @param uid  ID of the user that sent the message
     * @param cid  ID of the chat we write the message to
     * @param message   The message that was sent
     * @param time  the time at which the message was sent
     */
    public static void submitChat(int uid, int cid, String message, Instant time) throws SQLException {
        ChatDB.submitChat(uid, cid, message, time);
    }

    /**
     * Get the most recent messages of a chat up to a certain time and amount
     *
     * @throws SQLException if SQL query fails.
     * @param cid  ID of the chat we want to obtain the messages from
     * @param fromTime   The time limit up to which we search for messages
     * @param amount  The amount of messages we load
     */
    public static List<ChatMessage> getMessages(int cid, Instant fromTime, int amount) throws Exception {
        return ChatDB.getMessages(cid, fromTime, amount);
    }

    /**
     * Get the most recent messages of a chat up to a certain time and amount
     *
     * @throws SQLException if SQL query fails.
     * @param cid  ID of the chat we want to set isOpen of
     * @param open  the new value that we want isOpen to have
     */
    public static void openChat(int cid, boolean open) throws SQLException {
        ChatDB.openChat(cid, open);
    }

    public static boolean checkPermission(int cid, int uid, int iid) throws Exception {
        // FIXME: When the DB is finished, uncomment and return boolean result
        boolean result = ChatDB.checkWritePermission(cid, uid) && !ChatDB.checkMuted(uid, iid) && ChatDB.getOpen(cid);
        return result;
    }
}
