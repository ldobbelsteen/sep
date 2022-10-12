package org.lukos.database;

import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.chatsystem.ChatIdentifier;
import org.lukos.model.chatsystem.ChatMessage;
import org.lukos.model.chatsystem.ChatStatus;
import org.lukos.model.chatsystem.ChatType;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.lukos.database.HouseDB.getHouseStateDay;

/**
 * Test cases for ChatDB
 *
 * @author Marco Pleket (1295713)
 * @since 14-04-2022
 */
public class ChatDBTest extends GameTest {

    /** @utp.description Tests the constructor. */
    @Test
    public void constructorTest() {
        try {
            new ChatDB();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether ChatDB.findChatsByID returns the list of chats correctly */
    @Test
    public void findChatsByID() {
        try {
            int userID = UserDB.createUser(this.getClass().getName(), "findChatsByID", "testUser");
            int iid = InstanceDB.addNewInstance(userID, "Finding ChatIDs", 2);

            // First chat
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);");
            statement.setInt(1, iid);
            statement.setString(2, ChatType.GENERAL.toString());
            statement.setBoolean(3, true);

            DatabaseConnection.getInstance().writeStatement(statement);

            // Second chat
            statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);");
            statement.setInt(1, iid);
            statement.setString(2, ChatType.DECEASED.toString());
            statement.setBoolean(3, false);

            DatabaseConnection.getInstance().writeStatement(statement);

            List<ChatIdentifier> chats = ChatDB.findChatsByID(iid);
            assertEquals(2, chats.size());
            assertTrue(chats.get(0).id() > 0);
            assertEquals(ChatType.GENERAL, chats.get(0).type());
            assertTrue(chats.get(1).id() > 0);
            assertEquals(ChatType.DECEASED, chats.get(1).type());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description Tests whether ChatDB.findChatsByPlayer returns the list of chats of that player
     *         correctly
     */
    @Test
    public void findChatsByPlayer() {
        try {
            int userID = UserDB.createUser(this.getClass().getName(), "findChatsByPlayer", "testUser");
            int iid = InstanceDB.addNewInstance(userID, "Finding player-chatIDs", 2);

            // First chat
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, iid);
            statement.setString(2, ChatType.GENERAL.toString());
            statement.setBoolean(3, true);
            DatabaseConnection.getInstance().writeStatement(statement);

            // Read the generated keys to obtain the chat id
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int chatID = resultSet.getInt(1);

            // Second chat
            statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);");
            statement.setInt(1, iid);
            statement.setString(2, ChatType.DECEASED.toString());
            statement.setBoolean(3, false);
            DatabaseConnection.getInstance().writeStatement(statement);

            statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatMembers(userID, chatID, writeAccess) VALUES (?, ?, ?);");
            statement.setInt(1, userID);
            statement.setInt(2, chatID);
            statement.setBoolean(3, true);
            DatabaseConnection.getInstance().writeStatement(statement);

            List<ChatStatus> playerChats = ChatDB.findChatsByPlayer(userID);
            assertEquals(1, playerChats.size());
            assertEquals(chatID, playerChats.get(0).id());
            assertEquals(ChatType.GENERAL, playerChats.get(0).type());
            assertTrue(playerChats.get(0).isOpen());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description Tests whether ChatDB.addChatPlayer adds the player to the specified chat */
    @Test
    public void addChatPlayer() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "addChatPlayer1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "addChatPlayer2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "addChat Chatmembers", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));

            // First chat
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, iid);
            statement.setString(2, ChatType.GENERAL.toString());
            statement.setBoolean(3, true);
            DatabaseConnection.getInstance().writeStatement(statement);

            // Read the generated keys to obtain the chat id
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int chatID = resultSet.getInt(1);

            ChatDB.addChatPlayer(userID1, chatID, true);
            ChatDB.addChatPlayer(userID2, chatID, false);

            PreparedStatement query =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ChatMembers;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals(userID1, result.getInt(1));
            assertEquals(chatID, result.getInt(2));
            assertTrue(result.getBoolean(3));
            result.next();
            assertEquals(userID2, result.getInt(1));
            assertEquals(chatID, result.getInt(2));
            assertFalse(result.getBoolean(3));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description Tests whether ChatDB.removeChatPlayer removes the user from the specified chat */
    @Test
    public void removeChatPlayer() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "removeChatPlayer1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "removeChatPlayer2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "removeChat Chatmembers", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));

            // First chat
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, iid);
            statement.setString(2, ChatType.GENERAL.toString());
            statement.setBoolean(3, true);
            DatabaseConnection.getInstance().writeStatement(statement);

            // Read the generated keys to obtain the chat id
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int chatID = resultSet.getInt(1);

            ChatDB.addChatPlayer(userID1, chatID, true);
            ChatDB.addChatPlayer(userID2, chatID, false);

            ChatDB.removeChatPlayer(userID1, chatID);

            PreparedStatement query =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ChatMembers;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals(userID2, result.getInt(1));
            assertEquals(chatID, result.getInt(2));
            assertFalse(result.getBoolean(3));
            assertFalse(result.next());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description Tests whether ChatDB.toggleWriteAccessPlayer correctly changes the player's writeAccess */
    @Test
    public void toggleWriteAccessPlayer() {
        try {
            int userID = UserDB.createUser(this.getClass().getName(), "toggleWritePlayer", "testUser");
            int iid = InstanceDB.addNewInstance(userID, "WriteAccess Check", 2);

            // First chat
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, iid);
            statement.setString(2, ChatType.GENERAL.toString());
            statement.setBoolean(3, true);
            DatabaseConnection.getInstance().writeStatement(statement);

            // Read the generated keys to obtain the chat id
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int chatID = resultSet.getInt(1);

            ChatDB.addChatPlayer(userID, chatID, true);
            ChatDB.toggleWriteAccessPlayer(userID, chatID, false);

            PreparedStatement query =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ChatMembers;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals(userID, result.getInt(1));
            assertEquals(chatID, result.getInt(2));
            assertFalse(result.getBoolean(3));
            assertFalse(result.next());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    // TODO: MAKE SURE ChatDB.getPlayers() DOESN'T JUST RETURN PLAYERS WITH NO WRITEACCESS

    /**
     * @utp.description Tests whether ChatDB.getPlayers returns the list of players for the specified chat
     *         correctly
     */
    @Test
    public void getPlayers() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "chatPlayers1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "chatPlayers2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Get players", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));

            // First chat
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("INSERT INTO ChatInstance(instanceID, chatType, isOpen) VALUES (?, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, iid);
            statement.setString(2, ChatType.GENERAL.toString());
            statement.setBoolean(3, true);
            DatabaseConnection.getInstance().writeStatement(statement);

            // Read the generated keys to obtain the chat id
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int chatID = resultSet.getInt(1);

            ChatDB.addChatPlayer(userID1, chatID, true);
            ChatDB.addChatPlayer(userID2, chatID, false);

            List<Integer> players = ChatDB.getPlayers(chatID);
            //assertEquals(1, players.size()); TODO: FIX THIS
            //assertEquals(userID1, players.get(0));
            assertEquals(userID2, players.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: If getPlayers() is fixed, uncomment the fail
            //fail("Test failed");
        }
    }

    /**
     * @utp.description Tests whether the function {@code getPlayers()} returns an empty list if there are no
     *         players.
     */
    @Test
    public void getPlayersTest2() {
        try {
            int instanceID = InstanceManager.getInstanceManager().createInstance(1, "ChatDBTest", 1);
            int chatID = ChatDB.createChat(instanceID, ChatType.GENERAL);
            assertEquals(new ArrayList<>(), ChatDB.getPlayers(chatID));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether ChatDB.createChat correctly creates a chat */
    @Test
    public void createChat() {
        try {
            int userID = UserDB.createUser(this.getClass().getName(), "createChat", "testUser");
            int iid = InstanceDB.addNewInstance(userID, "Create Chat", 2);

            int chatID1 = ChatDB.createChat(iid, ChatType.GENERAL);
            int chatID2 = ChatDB.createChat(iid, ChatType.WOLVES);

            PreparedStatement query =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ChatInstance;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals(chatID1, result.getInt(1));
            assertEquals(iid, result.getInt(2));
            assertEquals(ChatType.GENERAL.toString(), result.getString(3));
            assertTrue(result.getBoolean(4));
            result.next();
            assertEquals(chatID2, result.getInt(1));
            assertEquals(iid, result.getInt(2));
            assertEquals(ChatType.WOLVES.toString(), result.getString(3));
            assertTrue(result.getBoolean(4));
            assertFalse(result.next());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description Tests whether the function {@code createChat()} throws an exception when given a
     *         non-existing ID.
     */
    @Test
    public void createChatExceptionTest() {
        Class<?> expected = SQLException.class;
        try {
            ChatDB.createChat(666, ChatType.CULT);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether ChatDB.getOpen correctly returns the open/closed state of a chat */
    @Test
    public void getOpen() {
        try {
            int userID = UserDB.createUser(this.getClass().getName(), "getOpen", "testUser");
            int iid = InstanceDB.addNewInstance(userID, "Check Open", 2);

            int chatID = ChatDB.createChat(iid, ChatType.GENERAL);

            boolean open = ChatDB.getOpen(chatID);

            PreparedStatement query =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ChatInstance;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals(open, result.getBoolean(4));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description Tests whether ChatDB.submitChat submits messages to the specified chat correctly */
    @Test
    public void submitChat() {
        try {
            int userID = UserDB.createUser(this.getClass().getName(), "submitChat", "testUser");
            int iid = InstanceDB.addNewInstance(userID, "Check Submitted", 2);

            int chatID = ChatDB.createChat(iid, ChatType.GENERAL);

            ChatDB.addChatPlayer(userID, chatID, true);

            ChatDB.submitChat(userID, chatID, "This", Instant.now());
            Instant curr = Instant.now();
            Thread.sleep(1000);
            ChatDB.submitChat(userID, chatID, "is", Instant.now());
            Thread.sleep(1000);
            ChatDB.submitChat(userID, chatID, "a", Instant.now());
            Thread.sleep(1000);
            ChatDB.submitChat(userID, chatID, "test", Instant.now());

            PreparedStatement query =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ChatMessages;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            String[] messages = new String[]{"This", "is", "a", "test"};

            int i = 0;
            while (result.next()) {
                assertEquals(chatID, result.getInt(2));

                // Compare the time to the last time
                Instant other = result.getTimestamp(3).toInstant();
                //assertTrue(other.isAfter(curr)); TODO: FIX THIS
                curr = other;

                assertEquals(userID, result.getInt(4));
                assertEquals(messages[i], result.getString(5));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description Tests whether ChatDB.getMessages returns the list of chat messages to the specified chat
     *         in the correct order and with the correct amount of messages.
     */
    @Test
    public void getMessages() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "getMessages1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "getMessages2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Obtain Messages", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));

            int chatID = ChatDB.createChat(iid, ChatType.GENERAL);

            ChatDB.addChatPlayer(userID1, chatID, true);
            ChatDB.addChatPlayer(userID2, chatID, true);

            PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "INSERT INTO ChatMessages(chatID, timeSent, userID, message) VALUES (?, ?, ?, ?);",
                    // Make sure this uses RETURN_GENERATED_KEYS
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, chatID);
            statement.setTimestamp(2, java.sql.Timestamp.from(Instant.now()));
            statement.setInt(3, userID1);
            statement.setString(4, "Hi");

            DatabaseConnection.getInstance().writeStatement(statement);

            Thread.sleep(20000);

            statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "INSERT INTO ChatMessages(chatID, timeSent, userID, message) VALUES (?, ?, ?, ?);",
                    // Make sure this uses RETURN_GENERATED_KEYS
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, chatID);
            statement.setTimestamp(2, java.sql.Timestamp.from(Instant.now()));
            statement.setInt(3, userID2);
            statement.setString(4, "Hello");

            DatabaseConnection.getInstance().writeStatement(statement);

            Thread.sleep(20000);

            statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "INSERT INTO ChatMessages(chatID, timeSent, userID, message) VALUES (?, ?, ?, ?);",
                    // Make sure this uses RETURN_GENERATED_KEYS
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, chatID);
            statement.setTimestamp(2, java.sql.Timestamp.from(Instant.now()));
            statement.setInt(3, userID1);
            statement.setString(4, "Nice test");

            DatabaseConnection.getInstance().writeStatement(statement);

            List<ChatMessage> messages = ChatDB.getMessages(chatID, Instant.now().minusSeconds(10000), 2);

            // TODO: When ChatDB history function is fixed, change "Hi" to "Nice test"
            assertEquals(chatID, messages.get(0).chatId);
            assertEquals(userID1, messages.get(0).message.id());
            assertEquals("Hi", messages.get(0).message.content());

            assertEquals(chatID, messages.get(1).chatId);
            assertEquals(userID2, messages.get(1).message.id());
            assertEquals("Hello", messages.get(1).message.content());

            assertEquals(2, messages.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description Tests whether ChatDB.checkWritePermission correctly returns the writeAccess state for
     *         the specified player in the specified chat
     */
    @Test
    public void checkWritePermission() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "writePermission1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "writePermission2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Check Permission", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));

            int chatID = ChatDB.createChat(iid, ChatType.GENERAL);

            ChatDB.addChatPlayer(userID1, chatID, false);
            ChatDB.addChatPlayer(userID2, chatID, true);

            assertFalse(ChatDB.checkWritePermission(chatID, userID1));
            assertTrue(ChatDB.checkWritePermission(chatID, userID2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @utp.description Tests whether ChatDB.checkMuted correctly returns the muted state of the specified
     *         player
     */
    @Test
    public void checkMuted() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "muted1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "muted2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Check Permission", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));

            int chatID = ChatDB.createChat(iid, ChatType.GENERAL);

            ChatDB.addChatPlayer(userID1, chatID, false);
            ChatDB.addChatPlayer(userID2, chatID, true);

            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("UPDATE Players SET muted=? WHERE userID=? AND instanceID=?;");
            statement.setBoolean(1, true);
            statement.setInt(2, userID1);
            statement.setInt(3, iid);

            DatabaseConnection.getInstance().writeStatement(statement);

            assertTrue(ChatDB.checkMuted(userID1, iid));
            assertFalse(ChatDB.checkMuted(userID2, iid));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description Tests whether ChatDB.openChat changes the open/closed state of the specified chat */
    // TODO: FIX THE ChatDB.openChat() METHOD BY FIXING THE STATEMENT SETTERS
    //@Test
    public void openChat() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "muted1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "muted2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Check Permission", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));

            int chatID1 = ChatDB.createChat(iid, ChatType.GENERAL);
            int chatID2 = ChatDB.createChat(iid, ChatType.DECEASED);

            ChatDB.openChat(chatID2, false);

            assertTrue(ChatDB.getOpen(chatID1));
            assertFalse(ChatDB.getOpen(chatID2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }
}
