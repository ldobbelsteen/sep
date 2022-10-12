package org.lukos.model.chatsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.DatabaseConnection;
import org.lukos.model.GameTest;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link ChatManager}.
 *
 * @author Marco Pleket (1295713)
 * @since 15-04-2022
 */
public class ChatManagerTest extends GameTest {

    private int instanceID;
    private int otherInstance;
    private int playerUID1;
    private int playerUID2;
    private int otherPlayer1;
    private int otherPlayer2;
    private int chatID1;
    private int chatID2;
    private int chatID3;

    private ChatManager chatManager = ChatManager.getInstance();

    @BeforeEach
    public void setUp() {
        try {
            User user1 = UserManager.getInstance().createUser(new IssuerSub("ChatManagerTest", "Sub1"), "User1");
            this.instanceID = user1.createGame("ChatTestGame", 1);
            User user2 = UserManager.getInstance().createUser(new IssuerSub("ChatManagerTest", "Sub2"), "User2");
            user2.joinGame(instanceID);

            User otherUser1 = UserManager.getInstance().createUser(new IssuerSub("ChatManagerTest", "Sub3"), "otherUser1");
            this.otherInstance = otherUser1.createGame("ChatTestGame", 1);
            User otherUser2 = UserManager.getInstance().createUser(new IssuerSub("ChatManagerTest", "Sub4"), "otherUser2");
            otherUser2.joinGame(otherInstance);

            this.playerUID1 = user1.getPlayer().getPlayerIdentifier().userID();
            this.playerUID2 = user2.getPlayer().getPlayerIdentifier().userID();

            this.otherPlayer1 = otherUser1.getPlayer().getPlayerIdentifier().userID();
            this.otherPlayer2 = otherUser2.getPlayer().getPlayerIdentifier().userID();

            chatID1 = ChatManager.getInstance().createChat(this.instanceID, ChatType.GENERAL);
            chatID2 = ChatManager.getInstance().createChat(this.instanceID, ChatType.DECEASED);
            chatID3 = ChatManager.getInstance().createChat(this.otherInstance, ChatType.GENERAL);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description test whether ChatManager.getChatIDs returns the correct chat IDs only */
    @Test
    public void getChatIDsTest() {
        try {
            List<ChatIdentifier> chats = chatManager.getChatIDs(instanceID);
            assertTrue(chats.contains(new ChatIdentifier(chatID1, ChatType.GENERAL)));
            assertTrue(chats.contains(new ChatIdentifier(chatID2, ChatType.DECEASED)));
            assertFalse(chats.contains(new ChatIdentifier(chatID3, ChatType.GENERAL)));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description test whether ChatManager.getPlayerChats returns the correct chat IDs only */
    @Test
    public void getPlayerChatsTest() {
        try {
            Chat.addChatPlayer(playerUID1, chatID1, false);
            Chat.addChatPlayer(playerUID1, chatID2, true);
            Chat.addChatPlayer(playerUID2, chatID1, true);

            List<Integer> chats = chatManager.getPlayerChats(playerUID1).stream().map(ChatStatus::id).toList();
            assertTrue(chats.contains(chatID1));
            assertTrue(chats.contains(chatID2));
            assertFalse(chats.contains(chatID3));

            chats = chatManager.getPlayerChats(playerUID2).stream().map(ChatStatus::id).toList();
            assertTrue(chats.contains(chatID1));
            assertFalse(chats.contains(chatID2));
            assertFalse(chats.contains(chatID3));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description test whether ChatManager.createChat correctly creates a new chat */
    @Test
    public void createChatTest() {
        try {
            List<ChatIdentifier> chats = chatManager.getChatIDs(otherInstance);
            assertTrue(chats.contains(new ChatIdentifier(chatID3, ChatType.GENERAL)));

            int chatID4 = chatManager.createChat(otherInstance, ChatType.DECEASED);
            chats = chatManager.getChatIDs(otherInstance);
            assertTrue(chats.contains(new ChatIdentifier(chatID3, ChatType.GENERAL)));
            assertTrue(chats.contains(new ChatIdentifier(chatID4, ChatType.DECEASED)));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description test whether ChatManager.createChatPlayers correctly creates a new chat while adding the specified players */
    @Test
    public void createChatPlayersTest() {
        try {
            List<ChatIdentifier> chats = chatManager.getChatIDs(otherInstance);
            assertTrue(chats.contains(new ChatIdentifier(chatID3, ChatType.GENERAL)));

            List<Integer> players = new ArrayList<>();
            players.add(otherPlayer1);
            players.add(otherPlayer2);
            chatManager.createChatPlayers(otherInstance, ChatType.DECEASED, players);

            List<Integer> chatIDs = chatManager.getChatIDs(otherInstance).stream().map(ChatIdentifier::id).toList();
            int chatID4 = -1;
            for (int id : chatIDs) {
                if (id != chatID3) {
                    chatID4 = id;
                    break;
                }
            }
            chats = chatManager.getChatIDs(otherInstance);

            assertTrue(chats.contains(new ChatIdentifier(chatID3, ChatType.GENERAL)));
            assertTrue(chats.contains(new ChatIdentifier(chatID4, ChatType.DECEASED)));

            List<Integer> chatPlayers = getPlayers(chatID4);
            assertTrue(chatPlayers.contains(otherPlayer1));
            assertTrue(chatPlayers.contains(otherPlayer2));
            assertFalse(chatPlayers.contains(playerUID1));
            assertFalse(chatPlayers.contains(playerUID2));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Obtains all players assigned to the specified chat.
     *
     * @param cid The chat we want to obtain all players from
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    private List<Integer> getPlayers(int cid) throws SQLException {
        List<Integer> list = new ArrayList<>();

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT userID FROM ChatMembers WHERE ChatMembers.chatID=?;");
        statement.setInt(1, cid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        while (resultSet.next()) {
            int userID = resultSet.getInt("userID"); // reading userid from resultset
            list.add(userID); // adding username to list
            //System.out.println("User ID found! " + userID);
        }
        return list;
    }
}
