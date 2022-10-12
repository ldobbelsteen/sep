package org.lukos.model.actionsystem.actions.util;

import org.lukos.database.ActionMessagesDB;
import org.lukos.database.ChatDB;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.actions.ActionMessageDT;
import org.lukos.model.chatsystem.Chat;
import org.lukos.model.chatsystem.ChatType;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a facade for action messages database and the chat system.
 *
 * @author Rick van der Heijden (1461923)
 * @since 08-04-2022
 */
abstract class ActionMessageHelper {

    /**
     * Adds an action message with a single receiver to the message database.
     *
     * @param message  the action message
     * @param fields   the fields of the action message
     * @param actionID the ID of the action message
     * @param playerID the ID of the receiver
     * @return the ID of the action message
     * @throws SQLException when a database operation fails
     */
    public static int addMessage(ActionMessages message, List<String> fields, int actionID, PlayerIdentifier playerID)
            throws SQLException {
        return ActionMessagesDB.addNewMessage(new ActionMessageDT(message, fields), actionID, playerID);
    }

    /**
     * Adds an action message with a multiple receivers to the message database.
     *
     * @param message    the action message
     * @param fields     the fields of the action message
     * @param actionID   the ID of the action message
     * @param recipients the IDs of the receivers
     * @return the IDs of the action messages
     * @throws SQLException when a database operation fails
     */
    public static List<Integer> addMessages(ActionMessages message, List<String> fields, int actionID,
                                            List<PlayerIdentifier> recipients) throws SQLException {
        return ActionMessagesDB.addNewMessage(new ActionMessageDT(message, fields), actionID, recipients);
    }

    /**
     * Unlocks an action message.
     *
     * @param message the ID of the action message to be unlocked
     * @throws SQLException when a database operation fails
     */
    public static void unlockMessageByMessageID(int message) throws SQLException {
        ActionMessagesDB.unlockMessage(message);
    }

    /**
     * Adds a {@code Player} to the wolves chat.
     *
     * @param playerID the ID of the {@code Player} to be added to the wolves chat
     * @throws SQLException when a database operation fails
     */
    public static void addPlayerToWolvesChat(PlayerIdentifier playerID) throws SQLException {
        int cid = ChatDB.findChatsByID(playerID.instanceID()).stream()
                .filter(chatIdentifier -> chatIdentifier.type() == ChatType.WOLVES).toList().get(0).id();
        Chat.addChatPlayer(playerID.userID(), cid, true);
    }
}
