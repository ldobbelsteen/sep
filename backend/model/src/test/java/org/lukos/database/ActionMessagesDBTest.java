package org.lukos.database;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.actionsystem.ActionDT;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.actionsystem.actions.ActionMessageDT;
import org.lukos.model.actionsystem.actions.KillMarkedPlayersLynch;
import org.lukos.model.actionsystem.actions.KillPlayers;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.instances.InstanceState;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.roles.mainroles.Clairvoyant;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.lukos.database.ActionMessagesDB.getAndCompleteMessage;

/**
 * Test cases for {@link ActionMessagesDB}
 *
 * @author Marco Pleket (1295713)
 * @since 15-04-2022
 */
public class ActionMessagesDBTest extends GameTest {

    /** @utp.description Testing the constructor for {@code ActionMessagesDB} */
    @Test
    public void constructorTest() {
        new ActionMessagesDB();
    }

    /**
     * @utp.description tests whether {@code ActionMessagesDB.getAllNotSendMessagesForUser()} returns the correct messages.
     */
    @Test
    public void getAllNotSendMessagesForUser() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "getNotSentMessages1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "getNotSentMessages2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Get Non-Sent", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players)));
            int actionID = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID);

            List<String> data = new ArrayList<String>();
            data.add((new User(userID2)).getUsername());
            int messageID = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data), actionID,
                    new PlayerIdentifier(iid, userID1));

            ActionMessagesDB.unlockMessages(iid);

            List<Integer> messages = ActionMessagesDB.getAllNotSendMessagesForUser(new PlayerIdentifier(iid, userID1));
            assertEquals(messageID, messages.get(0));
            assertEquals(1, messages.size());
        } catch (Exception e) {
            e.getStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description tests whether {@code ActionMessagesDB.unlockMessages()} actually unlocks the messages. */
    @Test
    @Disabled
    public void unlockMessages() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "unlockMessages1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "unlockMessages2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Unlock Messages", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players)));
            int actionID = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID);

            List<String> data = new ArrayList<String>();
            data.add((new User(userID2)).getUsername());
            int messageID = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data), actionID,
                    new PlayerIdentifier(iid, userID1));

            PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT ActionLogs.status FROM ActionLogs, Actions WHERE Actions.instanceID=? AND " +
                            "Actions.actionID=ActionLogs.actionID;");
            statement.setInt(1, iid);
            ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
            resultSet.next();
            assertEquals("LOCKED", resultSet.getString(1));

            ActionMessagesDB.unlockMessages(iid);

            statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT ActionLogs.statusstatus FROM ActionLogs, Actions WHERE Actions.instanceID=? AND " +
                            "Actions.actionID=ActionLogs.actionID;");
            statement.setInt(1, iid);
            resultSet = DatabaseConnection.getInstance().readStatement(statement);
            resultSet.next();
            assertEquals("NOT_SENT", resultSet.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description Tests whether {@code ActionMessagesDB.getMessage()} returns the correct message. */
    @Test
    public void getMessage() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "getMessage1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "getMessage2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Get Message", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players1 = new ArrayList<>();
            players1.add(new PlayerIdentifier(iid, userID2));
            List<PlayerIdentifier> players2 = new ArrayList<>();
            players2.add(new PlayerIdentifier(iid, userID1));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players1)));
            int actionID1 = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID1);

            // Action added
            actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID2),
                            new ActionEnc(new ArrayList<Integer>(), players2)));
            int actionID2 = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID2);

            List<String> data1 = new ArrayList<String>();
            data1.add((new User(userID2)).getUsername());
            int messageID1 = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data1), actionID1,
                    new PlayerIdentifier(iid, userID1));
            List<String> data2 = new ArrayList<String>();
            data2.add((new User(userID2)).getUsername());
            int messageID2 = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.LYNCH_KILL_BROADCAST_MESSAGE, data2), actionID2,
                    new PlayerIdentifier(iid, userID2));

            ActionMessageDT actionMessage = ActionMessagesDB.getMessage(messageID1);
            assertEquals(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, actionMessage.messageType());
            assertEquals(data1, actionMessage.data());

            actionMessage = ActionMessagesDB.getMessage(messageID2);
            assertEquals(ActionMessages.LYNCH_KILL_BROADCAST_MESSAGE, actionMessage.messageType());
            assertEquals(data2, actionMessage.data());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description Tests whether {@code ActionMessagesDB.getAndCompleteMessage()} returns the correct message and marks the message as 'SENT'. */
    @Test
    public void getAndCompleteMessageTest() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "addNewMessage1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "addNewMessage2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Add Message", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1), new ActionEnc(new ArrayList<>(), players)));
            int actionID = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID);

            PreparedStatement statement =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ActionLogs;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(statement);
            assertFalse(result.next());

            List<String> data = new ArrayList<>();
            data.add((new User(userID2)).getUsername());
            int messageID = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data), actionID,
                    new PlayerIdentifier(iid, userID1));

            getAndCompleteMessage(messageID);

            PreparedStatement updateStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT status FROM ActionLogs WHERE messageID=?;");
            updateStatement.setInt(1, messageID);

            ResultSet resultSet = DatabaseConnection.getInstance().readStatement(updateStatement);
            assertTrue(resultSet.next(), "DB should have the message");
            assertEquals("SENT", resultSet.getString("status"), "Status should be send.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description tests whether {@code ActionMessagesDB.addNewMessage()} adds a new message correctly. */
    @Test
    public void addNewMessage() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "addNewMessage1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "addNewMessage2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Add Message", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players)));
            int actionID = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID);

            PreparedStatement statement =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ActionLogs;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(statement);
            assertFalse(result.next());

            List<String> data = new ArrayList<String>();
            data.add((new User(userID2)).getUsername());
            int messageID = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data), actionID,
                    new PlayerIdentifier(iid, userID1));

            statement = DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ActionLogs;");
            result = DatabaseConnection.getInstance().readStatement(statement);
            result.next();
            assertEquals(messageID, result.getInt(1));
            assertEquals(actionID, result.getInt(2));
            assertEquals(userID1, result.getInt(3));
            assertEquals("LOCKED", result.getString(4));
            assertEquals(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE.toString(), result.getString(5));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code addNewMessage()} throws an exception when it receives the wrong action ID input.
     */
    @Test
    public void addNewMessageExceptionTest() {
        Class<?> expected = SQLException.class;
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "addNewMessage1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "addNewMessage2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Add Message", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1), new ActionEnc(new ArrayList<>(), players)));
            int actionID = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID);

            PreparedStatement statement =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ActionLogs;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(statement);
            assertFalse(result.next());

            List<String> data = new ArrayList<>();
            data.add((new User(userID2)).getUsername());

            ActionMessagesDB.addNewMessage(new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data), 666,
                    new PlayerIdentifier(666, 666));
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description tests whether ActionMessagesDB.testAddNewMessage() adds the list of messages correctly. */
    @Test
    public void testAddNewMessage() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "addNewMessages1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "addNewMessages2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Add Messages", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillMarkedPlayersLynch(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players)));
            int actionID = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID);

            List<String> data = new ArrayList<String>();
            data.add((new User(userID2)).getUsername());

            List<PlayerIdentifier> playersIDs = new ArrayList<>();
            playersIDs.add(new PlayerIdentifier(iid, userID1));
            playersIDs.add(new PlayerIdentifier(iid, userID2));

            List<Integer> messageIDs = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.LYNCH_KILL_BROADCAST_MESSAGE, data), actionID, playersIDs);

            PreparedStatement statement =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM ActionLogs;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(statement);

            result.next();
            assertEquals(messageIDs.get(0), result.getInt(1));
            assertEquals(actionID, result.getInt(2));
            assertEquals(userID1, result.getInt(3));
            assertEquals("LOCKED", result.getString(4));
            assertEquals(ActionMessages.LYNCH_KILL_BROADCAST_MESSAGE.toString(), result.getString(5));

            result.next();
            assertEquals(messageIDs.get(1), result.getInt(1));
            assertEquals(actionID, result.getInt(2));
            assertEquals(userID2, result.getInt(3));
            assertEquals("LOCKED", result.getString(4));
            assertEquals(ActionMessages.LYNCH_KILL_BROADCAST_MESSAGE.toString(), result.getString(5));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description tests whether ActionMessagesDB.unlockMessage() unlocks the specified message. */
    @Test
    public void unlockMessage() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "unlockMessage1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "unlockMessage2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Unlock Message", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players1 = new ArrayList<>();
            players1.add(new PlayerIdentifier(iid, userID2));
            List<PlayerIdentifier> players2 = new ArrayList<>();
            players2.add(new PlayerIdentifier(iid, userID1));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players1)));
            int actionID1 = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID1);

            // Action added
            actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID2),
                            new ActionEnc(new ArrayList<Integer>(), players2)));
            int actionID2 = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID2);

            List<String> data1 = new ArrayList<String>();
            data1.add((new User(userID2)).getUsername());
            int messageID1 = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data1), actionID1,
                    new PlayerIdentifier(iid, userID1));
            List<String> data2 = new ArrayList<String>();
            data2.add((new User(userID1)).getUsername());
            int messageID2 = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data2), actionID2,
                    new PlayerIdentifier(iid, userID2));

            PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT ActionLogs.status FROM ActionLogs, Actions WHERE Actions.instanceID=? AND " +
                            "Actions.actionID=ActionLogs.actionID;");
            statement.setInt(1, iid);
            ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
            resultSet.next();
            assertEquals("LOCKED", resultSet.getString(1));
            resultSet.next();
            assertEquals("LOCKED", resultSet.getString(1));

            ActionMessagesDB.unlockMessage(messageID1);

            statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT ActionLogs.messageID, ActionLogs.status FROM ActionLogs, Actions WHERE Actions" +
                            ".instanceID=? AND " + "Actions.actionID=ActionLogs.actionID;");
            statement.setInt(1, iid);
            resultSet = DatabaseConnection.getInstance().readStatement(statement);
            resultSet.next();
            assertEquals(messageID1, resultSet.getInt(1));
            assertEquals("NOT_SENT", resultSet.getString(2));
            resultSet.next();
            assertEquals(messageID2, resultSet.getInt(1));
            assertEquals("LOCKED", resultSet.getString(2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description tests whether ActionMessagesDB.markAsSent() marks the specified message as sent (and not any others).
     */
    @Test
    public void markAsSent() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "markSent1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "markSent2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Mark Sent", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players1 = new ArrayList<>();
            players1.add(new PlayerIdentifier(iid, userID2));
            List<PlayerIdentifier> players2 = new ArrayList<>();
            players2.add(new PlayerIdentifier(iid, userID1));

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players1)));
            int actionID1 = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID1);

            // Action added
            actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID2),
                            new ActionEnc(new ArrayList<Integer>(), players2)));
            int actionID2 = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID2);

            List<String> data1 = new ArrayList<String>();
            data1.add((new User(userID2)).getUsername());
            int messageID1 = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data1), actionID1,
                    new PlayerIdentifier(iid, userID1));
            List<String> data2 = new ArrayList<String>();
            data2.add((new User(userID1)).getUsername());
            int messageID2 = ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE, data2), actionID2,
                    new PlayerIdentifier(iid, userID2));

            PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT ActionLogs.status FROM ActionLogs, Actions WHERE Actions.instanceID=? AND " +
                            "Actions.actionID=ActionLogs.actionID;");
            statement.setInt(1, iid);
            ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
            resultSet.next();
            assertEquals("LOCKED", resultSet.getString(1));
            resultSet.next();
            assertEquals("LOCKED", resultSet.getString(1));

            ActionMessagesDB.markAsSent(messageID1);

            statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT ActionLogs.messageID, ActionLogs.status FROM ActionLogs, Actions WHERE Actions" +
                            ".instanceID=? AND " + "Actions.actionID=ActionLogs.actionID;");
            statement.setInt(1, iid);
            resultSet = DatabaseConnection.getInstance().readStatement(statement);
            resultSet.next();
            assertEquals(messageID1, resultSet.getInt(1));
            assertEquals("SENT", resultSet.getString(2));
            resultSet.next();
            assertEquals(messageID2, resultSet.getInt(1));
            assertEquals("LOCKED", resultSet.getString(2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }
}
