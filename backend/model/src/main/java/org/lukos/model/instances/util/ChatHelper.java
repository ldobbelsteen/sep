package org.lukos.model.instances.util;

import org.lukos.model.chatsystem.*;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.roles.mainroles.Medium;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * This class is a helper class for {@code Chat}s.
 *
 * @author Rick van der Heijden (1461923)
 * @author Marco Pleket (1295713)
 * @since 07-04-2022
 */
abstract class ChatHelper extends RoleDivisionHelper {

    /**
     * Toggles the access to all the chats that are used by players that are alive to either.
     *
     * @param userID      the user for which the chats need to be toggled
     * @param writeAccess whether to remove or add the write access
     * @throws SQLException        when a database operation fails
     * @throws NoSuchUserException when the given ID is not of an existing user
     */
    public static void toggleWriteAccessAliveChats(int userID, boolean writeAccess)
            throws SQLException, NoSuchUserException {
        List<ChatStatus> playerChats = ChatManager.getInstance().getPlayerChats(userID);
        for (ChatStatus cs : playerChats) {
            if (cs.type() != ChatType.DECEASED) {
                Chat.toggleWriteAccessPlayer(userID, cs.id(), writeAccess);
            }
        }
    }

    /**
     * Adds a player to the chat of the deceased.
     *
     * @param playerID the ID of the player that needs to be added to the deceased chat.
     * @throws NoSuchInstanceException when an {@code Instance} is being accessed without it existing
     * @throws SQLException            when a database operation fails
     */
    public static void addPlayerToDeceasedChat(PlayerIdentifier playerID) throws NoSuchInstanceException, SQLException {
        List<ChatIdentifier> chats = ChatManager.getInstance().getChatIDs(playerID.instanceID());
        for (ChatIdentifier ci : chats) {
            if (ci.type() == ChatType.DECEASED) {
                Chat.addChatPlayer(playerID.userID(), ci.id(), true);
            }
        }
    }

    /**
     * Removes a player to the chat of the deceased.
     *
     * @param player the player that needs to be removed from the deceased chat
     * @throws GameException                when an exception occurs in the game-logic
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void removePlayerFromDeceasedChat(Player player)
            throws GameException, SQLException, ReflectiveOperationException {
        PlayerIdentifier playerID = player.getPlayerIdentifier();
        List<ChatIdentifier> chats = ChatManager.getInstance().getChatIDs(playerID.instanceID());
        for (ChatIdentifier ci : chats) {
            if (ci.type() == ChatType.DECEASED && !(player.getMainRole() instanceof Medium)) {
                Chat.removeChatPlayer(playerID.userID(), ci.id());
                break;
            }
        }
    }

    /**
     * Adds a {@code Player} to the chats it belongs.
     * <p>
     * This means that a wolf will be added to the wolves chat. A cult member will be added to the cult chat. A medium
     * will be added to the deceased chat.
     *
     * @param player          the {@code Player}
     * @param chatIdentifiers a {@code List} with IDs of the available {@code Chat}s
     * @throws GameException                when an exception occurs in the game-logic
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void addPlayerToChats(Player player, List<ChatIdentifier> chatIdentifiers)
            throws SQLException, ReflectiveOperationException, GameException {
        // Chat
        Chat.addChatPlayer(player.getPlayerIdentifier().userID(), chatIdentifiers.get(0).id(), true);

        MainRole role = player.getMainRole();

        switch (role.getGroup()) {
            case WEREWOLVES ->
                    Chat.addChatPlayer(player.getPlayerIdentifier().userID(), chatIdentifiers.get(2).id(), true);
            case CULT -> Chat.addChatPlayer(player.getPlayerIdentifier().userID(), chatIdentifiers.get(3).id(), true);
        }

        if (role instanceof Medium) {
            Chat.addChatPlayer(player.getPlayerIdentifier().userID(), chatIdentifiers.get(1).id(), true);
        }
    }
}
