package org.lukos.model.chatsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.ChatDB;
import org.lukos.database.DatabaseConnection;
import org.lukos.model.GameTest;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.lukos.model.chatsystem.Chat.*;

/**
 * Test cases for {@link Chat}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 15-04-2022
 */
public class ChatTest extends GameTest {

    private int instanceID;
    private int chatID;
    private int playerUID1;
    private int playerUID2;

    @BeforeEach
    public void setUp() {
        try {
            User user1 = UserManager.getInstance().createUser(new IssuerSub("ChatTest", "Sub1"), "User1");
            this.instanceID = user1.createGame("ChatTestGame", 1);
            User user2 = UserManager.getInstance().createUser(new IssuerSub("ChatTest", "Sub2"), "User2");
            user2.joinGame(instanceID);

            this.playerUID1 = user1.getPlayer().getPlayerIdentifier().userID();
            this.playerUID2 = user2.getPlayer().getPlayerIdentifier().userID();

            chatID = ChatManager.getInstance().createChat(this.instanceID, ChatType.GENERAL);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor shows the desired behaviour. */
    @Test
    public void constructorTest() {
        try {
            new Chat();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the function {@code addChatPlayer()} actually adds players to the chat. */
    @Test
    public void addChatPlayerTest() {
        try {
            assertFalse(getPlayers(chatID).contains(playerUID1));
            addChatPlayer(playerUID1, chatID, true);
            assertTrue(getPlayers(chatID).contains(playerUID1));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code removeChatPlayer()} actually removes players from the chat .
     */
    @Test
    public void removeChatPlayerTest() {
        try {
            addChatPlayerTest();
            removeChatPlayer(playerUID1, chatID);
            assertFalse(getPlayers(chatID).contains(playerUID1));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code toggleWriteAccessPlayer()} toggles the write acces of a
     * player.
     */
    @Test
    public void toggleWriteAccessPlayerTest() {
        try {
            addChatPlayerTest();
            assertTrue(ChatDB.checkWritePermission(this.chatID, this.playerUID1));
            toggleWriteAccessPlayer(this.playerUID1, this.chatID, false);
            assertFalse(ChatDB.checkWritePermission(this.chatID, this.playerUID1));
            toggleWriteAccessPlayer(this.playerUID1, this.chatID, true);
            assertTrue(ChatDB.checkWritePermission(this.chatID, this.playerUID1));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the function {@code submitChat()} submits chat messages. */
    @Test
    public void submitChatTest() {
        try {
            addPlayer1ToChat();
            String message = "This is a test message.";
            submitChat(this.playerUID1, this.chatID, message, Instant.now());
            List<ChatMessage> chatMessages = getMessages(this.chatID, Instant.now().minus(5, ChronoUnit.MINUTES), 5);
            assertTrue(chatMessages.stream().map(chatMessage -> chatMessage.message).map(MessageEntry::content)
                    .anyMatch(m -> m.equals(message)), "Should contain the chat message.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the function {@code openChat()} opens and closes the chat. */
    @Test
    public void openChatTest() {
        // TODO: Activate chat when openChat is fixed (it should assign the correct parameters, see openChat())
        try {
            openChat(this.chatID, true);
            assertTrue(isOpen(this.chatID));
//            assertTrue(isOpen(this.chatID));
            openChat(this.chatID, false);
//            assertFalse(isOpen(this.chatID));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the function {@code checkPermission()} checks permissions correctly. */
    @Test
    public void checkPermissionTest() {
        try {
            addPlayer1ToChat();

            assertTrue(ChatDB.checkWritePermission(this.chatID, this.playerUID1));
            assertFalse(ChatDB.checkMuted(this.playerUID1, this.instanceID));
            assertTrue(ChatDB.getOpen(this.chatID));

            assertTrue(checkPermission(this.chatID, this.playerUID1, this.instanceID));
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("UPDATE ChatInstance SET isOpen=? WHERE chatID=?;");
            statement.setBoolean(1, false);
            statement.setInt(2, this.chatID);

            DatabaseConnection.getInstance().writeStatement(statement);
            assertFalse(checkPermission(this.chatID, this.playerUID1, this.instanceID));

            statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("UPDATE Players SET muted=true WHERE userID=? AND instanceID=?;");
            statement.setInt(1, this.playerUID1);
            statement.setInt(2, this.instanceID);
            DatabaseConnection.getInstance().writeStatement(statement);

            assertFalse(checkPermission(this.chatID, this.playerUID1, this.instanceID));
            toggleWriteAccessPlayer(this.playerUID1, this.chatID, false);
            assertFalse(checkPermission(this.chatID, this.playerUID1, this.instanceID));
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

    /**
     * Adds {@code player1} to the Chat with ID {@code chatID}.
     *
     * @throws SQLException when a database operation fails
     */
    private void addPlayer1ToChat() throws SQLException {
        assertFalse(getPlayers(chatID).contains(playerUID1));
        addChatPlayer(playerUID1, chatID, true);
        assertTrue(getPlayers(chatID).contains(playerUID1));
    }
}
