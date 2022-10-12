package org.lukos.model.chatsystem;

import org.lukos.database.ChatDB;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;
import org.lukos.model.exceptions.user.NoSuchUserException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class implementing the chat manager
 * <p>
 * A ChatManager has a list of chats for every game and the option to add, remove and return chats.
 *
 * @author Marco Pleket (1295713)
 * @since 28-03-2022
 */
public class ChatManager {

    /** Returns the singleton of ChatManager */
    public static ChatManager getInstance() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * Finds all chats of the specified game instance with gameID iid.
     *
     * @throws NoSuchInstanceException if no instance with given id is found.
     * @param iid  ID of the game we want to obtain all chat IDs for
     * @return List of ChatIdentifier records.
     */
    public List<ChatIdentifier> getChatIDs(int iid) throws NoSuchInstanceException, SQLException {
        return ChatDB.findChatsByID(iid);
    }

    /**
     * Finds all available chats for player with userID uid.
     *
//     * @throws NoSuchInstanceException if no instance with given id is found.
     * @param uid  ID of the player we want to obtain all chat IDs for
     * @return List of ChatIdentifier records.
     */
    public List<ChatStatus> getPlayerChats(int uid) throws NoSuchUserException, SQLException {
        return ChatDB.findChatsByPlayer(uid);
    }

    /**
     * Finds all available chats for player with userID uid.
     *
//     * @throws NoSuchInstanceException if no instance with given id is found.
     * @param iid  ID of the instance we want the chat to exist in
     * @param type  type of the chat to be created
     * @return the chat id of the newly created chat
     */
    public int createChat(int iid, ChatType type) throws SQLException {
        // Check whether chat of this type doesn't already exist

        return ChatDB.createChat(iid, type);
    }

    /**
     * Finds all available chats for player with userID uid.
     *
//     * @throws NoSuchInstanceException if no instance with given id is found.
     * @param iid  ID of the instance we want the chat to exist in
     * @param type  type of the chat to be created
     * @param players   list of players that should be added to the newly created chat
     */
    public void createChatPlayers(int iid, ChatType type, List<Integer> players) throws SQLException {
        int cid = createChat(iid, type);

        for (Integer i : players) {
            // Check whether player is in the correct game/has permission

            ChatDB.addChatPlayer(i, cid, false);
        }
    }


//    public void togglePlayerMuted(int uid, int iid, boolean muted) {
//
//    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account thread-safety.
     */
    private static class SingletonHelper {
        private static final ChatManager uniqueInstance = new ChatManager();
    }
}
