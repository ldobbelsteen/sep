package org.lukos.database;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionDT;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.actionsystem.actions.KillPlayers;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link ActionsDB}
 *
 * @author Marco Pleket (1295713)
 * @since 15-04-2022
 */
public class ActionsDBTest extends GameTest {

    /** @utp.description Testing the constructor for ActionsDB */
    @Test
    public void constructorTest() {
        try {
            new ActionsDB();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description tests whether ActionsDB.addNewAction() adds a new action correctly */
    @Test
    public void addNewAction() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "addAction1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "addAction2", "testUser");
            int userID3 = UserDB.createUser(this.getClass().getName(), "addAction3", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Add Action", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID3));
            InstanceDB.initializeInstanceState(iid);

            List<Integer> locations = new ArrayList<>();
            locations.add(userID2);
            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID3));

            // First and second action added
            Instant instant1 = Instant.now();
            ActionDT actionDT = new ActionDT(instant1, new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1), new ActionEnc(locations, new ArrayList<>())));
            int actionID1 = ActionsDB.addNewAction(actionDT);
            Instant instant2 = Instant.now();
            actionDT = new ActionDT(instant2, new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1), new ActionEnc(new ArrayList<>(), players)));
            int actionID2 = ActionsDB.addNewAction(actionDT);

            // Third action added
            locations = new ArrayList<>();
            locations.add(userID1);
            players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));
            Instant instant3 = Instant.now();
            actionDT = new ActionDT(instant3, new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID3), new ActionEnc(locations, players)));
            int actionID3 = ActionsDB.addNewAction(actionDT);

            // Fourth and fifth action added
            Instant instant4 = Instant.now();
            actionDT = new ActionDT(instant4, new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, -1), new ActionEnc(locations, null)));
            int actionID4 = ActionsDB.addNewAction(actionDT);
            Instant instant5 = Instant.now();
            actionDT = new ActionDT(instant5, new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, -1), new ActionEnc(null, players)));
            int actionID5 = ActionsDB.addNewAction(actionDT);

            PreparedStatement query =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM Actions;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals(actionID1, result.getInt(1));
            assertEquals(iid, result.getInt(2));
            assertEquals(userID1, result.getInt(3));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isAfter(instant1.truncatedTo(ChronoUnit.SECONDS).minus(2, ChronoUnit.SECONDS)));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isBefore(instant1.truncatedTo(ChronoUnit.SECONDS).plus(2, ChronoUnit.SECONDS)));
            assertEquals(KillPlayers.class.getSimpleName(), result.getString(5));
            assertEquals("NOT_EXECUTED", result.getString(6));
            assertEquals("LOCATION", result.getString(7));

            result.next();
            assertEquals(actionID2, result.getInt(1));
            assertEquals(iid, result.getInt(2));
            assertEquals(userID1, result.getInt(3));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isAfter(instant2.truncatedTo(ChronoUnit.SECONDS).minus(2, ChronoUnit.SECONDS)));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isBefore(instant2.truncatedTo(ChronoUnit.SECONDS).plus(2, ChronoUnit.SECONDS)));
            assertEquals(KillPlayers.class.getSimpleName(), result.getString(5));
            assertEquals("NOT_EXECUTED", result.getString(6));
            assertEquals("PLAYER", result.getString(7));

            result.next();
            assertEquals(actionID3, result.getInt(1));
            assertEquals(iid, result.getInt(2));
            assertEquals(userID3, result.getInt(3));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isAfter(instant3.truncatedTo(ChronoUnit.SECONDS).minus(2, ChronoUnit.SECONDS)));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isBefore(instant3.truncatedTo(ChronoUnit.SECONDS).plus(2, ChronoUnit.SECONDS)));
            assertEquals(KillPlayers.class.getSimpleName(), result.getString(5));
            assertEquals("NOT_EXECUTED", result.getString(6));
            assertEquals("BOTH", result.getString(7));

            result.next();
            assertEquals(actionID4, result.getInt(1));
            assertEquals(iid, result.getInt(2));
            assertEquals(0, result.getInt(3));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isAfter(instant4.truncatedTo(ChronoUnit.SECONDS).minus(2, ChronoUnit.SECONDS)));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isBefore(instant4.truncatedTo(ChronoUnit.SECONDS).plus(2, ChronoUnit.SECONDS)));
            assertEquals(KillPlayers.class.getSimpleName(), result.getString(5));
            assertEquals("NOT_EXECUTED", result.getString(6));
            assertEquals("LOCATION", result.getString(7));

            result.next();
            assertEquals(actionID5, result.getInt(1));
            assertEquals(iid, result.getInt(2));
            assertEquals(0, result.getInt(3));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isAfter(instant5.truncatedTo(ChronoUnit.SECONDS).minus(2, ChronoUnit.SECONDS)));
            assertTrue(result.getTimestamp(4).toInstant()
                    .isBefore(instant5.truncatedTo(ChronoUnit.SECONDS).plus(2, ChronoUnit.SECONDS)));
            assertEquals(KillPlayers.class.getSimpleName(), result.getString(5));
            assertEquals("NOT_EXECUTED", result.getString(6));
            assertEquals("PLAYER", result.getString(7));

            query = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM ActionTargetPlayers;");
            result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals(actionID2, result.getInt(1));
            assertEquals(userID3, result.getInt(2));
            result.next();
            assertEquals(actionID3, result.getInt(1));
            assertEquals(userID2, result.getInt(2));

            query = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM ActionTargetLocation;");
            result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals(actionID1, result.getInt(1));
            assertEquals(userID2, result.getInt(2));
            result.next();
            assertEquals(actionID3, result.getInt(1));
            assertEquals(userID1, result.getInt(2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description Tests whether the function {@code addNewAction()} throws an exception when it is given a
     *         wrong input.
     */
    @Test
    @Disabled
    public void addNewActionTest() {
        Class<?> expected = SQLException.class;
        try {
            ActionDT actionDT = new ActionDT(Instant.now(), null,
                    new PreActionDT(new PlayerIdentifier(-1, -2),
                            new ActionEnc(null, null)));
            ActionsDB.addNewAction(actionDT);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description tests whether ActionsDB.executeAction() executes the specified action */
    @Test
    public void executeAction() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "executeAction1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "executeAction2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Execute Action", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));

            // First and second action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players)));
            int actionID = ActionsDB.addNewAction(actionDT);

            PreparedStatement query = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT Actions.status FROM Actions;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals("NOT_EXECUTED", result.getString(1));

            ActionsDB.executeAction(actionID);

            query = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT Actions.status FROM Actions;");
            result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals("EXECUTED", result.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description tests whether ActionsDB.completeAction() complete the specified action */
    @Test
    public void completeAction() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "completeAction1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "completeAction2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Complete Action", 2);
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

            PreparedStatement query = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT Actions.status FROM Actions;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals("NOT_EXECUTED", result.getString(1));

            ActionsDB.completeAction(actionID);

            query = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT Actions.status FROM Actions;");
            result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals("COMPLETED", result.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description tests whether ActionsDB.getNotExecutedActions() obtains all non-executed actions of the
     *         specified instance
     */
    @Test
    public void getNotExecutedActions() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "getNotExecuted1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "getNotExecuted2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Finding ChatIDs", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));
            List<Integer> locations = new ArrayList<>();
            locations.add(userID1);

            // Action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players)));
            int actionID1 = ActionsDB.addNewAction(actionDT);
            actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID2),
                            new ActionEnc(locations, new ArrayList<PlayerIdentifier>())));
            int actionID2 = ActionsDB.addNewAction(actionDT);

            List<Integer> nonExecuted = ActionsDB.getNotExecutedActions(iid);
            assertEquals(actionID1, nonExecuted.get(0));
            assertEquals(actionID2, nonExecuted.get(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /** @utp.description tests whether ActionsDB.getActions() obtains all actions of the specified instance */
    @Test
    public void getActions() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "getActions1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "getActions2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Execute Action", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));
            List<Integer> locations = new ArrayList<>();
            locations.add(userID1);

            // First and second action added
            ActionDT actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players)));
            int actionID1 = ActionsDB.addNewAction(actionDT);
            ActionsDB.executeAction(actionID1);

            actionDT = new ActionDT(Instant.now(), new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID2),
                            new ActionEnc(locations, new ArrayList<PlayerIdentifier>())));
            int actionID2 = ActionsDB.addNewAction(actionDT);
            ActionsDB.completeAction(actionID2);

            List<Integer> actions = ActionsDB.getActions(iid, "EXECUTED");
            assertEquals(actionID1, actions.get(0));
            assertEquals(1, actions.size());

            actions = ActionsDB.getActions(iid, "COMPLETED");
            assertEquals(actionID2, actions.get(0));
            assertEquals(1, actions.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description tests whether ActionsDB.getActionFromID() returns the correct ActionDT of the specified
     *         action
     */
    @Test
    public void getActionFromID() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "getActionFromID1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "getActionFromID2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Complete Action", 2);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID2));
            InstanceDB.initializeInstanceState(iid);

            List<PlayerIdentifier> players = new ArrayList<>();
            players.add(new PlayerIdentifier(iid, userID2));
            List<Integer> locations = new ArrayList<Integer>();
            locations.add(userID1);

            // Action added
            Instant now1 = Instant.now();
            ActionDT actionDT = new ActionDT(now1, new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1),
                            new ActionEnc(new ArrayList<Integer>(), players)));
            int actionID1 = ActionsDB.addNewAction(actionDT);
            Instant now2 = Instant.now();
            actionDT = new ActionDT(now2, new KillPlayers(), new PreActionDT(new PlayerIdentifier(iid, userID2),
                    new ActionEnc(locations, new ArrayList<PlayerIdentifier>())));
            int actionID2 = ActionsDB.addNewAction(actionDT);
            Instant now3 = Instant.now();
            actionDT = new ActionDT(now3, new KillPlayers(),
                    new PreActionDT(new PlayerIdentifier(iid, userID1), new ActionEnc(locations, players)));
            int actionID3 = ActionsDB.addNewAction(actionDT);

            ActionDT action = ActionsDB.getActionFromID(actionID1);
            assertTrue(action.time().isAfter(now1.truncatedTo(ChronoUnit.SECONDS).minus(2, ChronoUnit.SECONDS)));
            assertTrue(action.time().isBefore(now1.truncatedTo(ChronoUnit.SECONDS).plus(2, ChronoUnit.SECONDS)));
            assertEquals(KillPlayers.class.getSimpleName(), action.action().getClass().getSimpleName());
            assertEquals(iid, action.preAction().playerIdentifier().instanceID());
            assertEquals(userID1, action.preAction().playerIdentifier().userID());
            assertEquals(userID2, action.preAction().data().players().get(0).userID());

            action = ActionsDB.getActionFromID(actionID2);
            assertTrue(action.time().isAfter(now2.truncatedTo(ChronoUnit.SECONDS).minus(2, ChronoUnit.SECONDS)));
            assertTrue(action.time().isBefore(now2.truncatedTo(ChronoUnit.SECONDS).plus(2, ChronoUnit.SECONDS)));
            assertEquals(KillPlayers.class.getSimpleName(), action.action().getClass().getSimpleName());
            assertEquals(iid, action.preAction().playerIdentifier().instanceID());
            assertEquals(userID2, action.preAction().playerIdentifier().userID());
            assertEquals(userID1, action.preAction().data().locations().get(0));

            action = ActionsDB.getActionFromID(actionID3);
            assertTrue(action.time().isAfter(now3.truncatedTo(ChronoUnit.SECONDS).minus(2, ChronoUnit.SECONDS)));
            assertTrue(action.time().isBefore(now3.truncatedTo(ChronoUnit.SECONDS).plus(2, ChronoUnit.SECONDS)));
            assertEquals(KillPlayers.class.getSimpleName(), action.action().getClass().getSimpleName());
            assertEquals(iid, action.preAction().playerIdentifier().instanceID());
            assertEquals(userID1, action.preAction().playerIdentifier().userID());
            assertEquals(userID2, action.preAction().data().players().get(0).userID());
            assertEquals(userID1, action.preAction().data().locations().get(0));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed");
        }
    }

    /**
     * @utp.description Tests whether the function {@code getActionFromID} throws an exception when it receives
     *         an action ID that does not exist.
     */
    @Test
    public void getActionFromIDExceptionTest() {
        Class<?> expected = SQLException.class;
        try {
            ActionsDB.getActionFromID(666);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description tests whether ActionsDB.completeAction() checks whether an action is completed and
     *         completes it if that's the case.
     */
    @Test
    @Disabled
    public void checkIfComplete() {
        try {
            int userID1 = UserDB.createUser(this.getClass().getName(), "checkComplete1", "testUser");
            int userID2 = UserDB.createUser(this.getClass().getName(), "checkComplete2", "testUser");
            int iid = InstanceDB.addNewInstance(userID1, "Check Completeness", 2);
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

            PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "INSERT INTO ActionLogs(actionID, receiverID, status, messageType) VALUES (?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, actionID);
            statement.setInt(2, userID1);
            statement.setString(3, "SENT");
            statement.setString(4, "Success");
            DatabaseConnection.getInstance().writeStatement(statement);
            ResultSet res = statement.getGeneratedKeys();
            res.next();

            ActionsDB.CheckIfComplete(iid);

            PreparedStatement query = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT Actions.status FROM Actions;");
            ResultSet result = DatabaseConnection.getInstance().readStatement(query);

            result.next();
            assertEquals("COMPLETED", result.getString("status"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }
}
