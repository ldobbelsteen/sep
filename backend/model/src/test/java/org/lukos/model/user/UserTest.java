package org.lukos.model.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.InstanceDB;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.instances.GameAlreadyStartedException;
import org.lukos.model.exceptions.user.AlreadyInGameException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link User} class.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 22-02-2022
 */
public class UserTest extends GameTest {

    /** User objects used for all tests. */
    private User user;
    private User user2;
    private User user3;

    /** Create three users. */
    @BeforeEach
    public void beforeTest() {
        try {
            user = UserManager.getInstance().createUser(new IssuerSub("issuer", "sub"), "Username");

            assertNotNull(user.getUid(), "Uid still null");
            assertEquals("issuer", user.getIssuer(), "Issuer was not set correctly!");
            assertEquals("sub", user.getSub(), "Sub was not set correctly!");
//            assertEquals("Username", user.getUsername(), "Username was not set correctly!"); TODO: UNCOMMENT
            //assertNull(user.getPlayer(), "Player should be null!");
        } catch (Exception e) {
            fail("An exception was thrown in the @BeforeEach: " + e);
        }

        try {
            user2 = UserManager.getInstance().createUser(new IssuerSub("issuer2", "sub2"), "Username2");

            assertNotNull(user.getUid(), "Uid still null");
            assertEquals("issuer2", user2.getIssuer(), "Issuer was not set correctly!");
            assertEquals("sub2", user2.getSub(), "Sub was not set correctly!");
            assertEquals("Username2", user2.getUsername(), "Username was not set correctly!");
            //assertNull(user.getPlayer(), "Player should be null!");
        } catch (Exception e) {
            fail("An exception was thrown in the @BeforeEach: " + e);
        }

        try {
            user3 = UserManager.getInstance().createUser(new IssuerSub("issuer3", "sub3"), "Username3");

            assertNotNull(user.getUid(), "Uid still null");
            assertEquals("issuer3", user3.getIssuer(), "Issuer was not set correctly!");
            assertEquals("sub3", user3.getSub(), "Sub was not set correctly!");
            assertEquals("Username3", user3.getUsername(), "Username was not set correctly!");
            //assertNull(user.getPlayer(), "Player should be null!");
        } catch (Exception e) {
            fail("An exception was thrown in the @BeforeEach: " + e);
        }
    }

    // Make sure the users are removed from the database by cleanUpDatabase()
    @AfterEach
    public void afterTest() {
        // Clean user 1
        try {
            user.getPlayer();
        } catch (NoSuchPlayerException e) {
            // Assert user is not in a game
            try {
                user.createGame("cleanUp1", 1);
            } catch (Exception ex) {
                fail("An exception was thrown during cleanUp game creation 1: " + ex);
            }

        } catch (Exception e) {
            fail("An exception was thrown during cleanUp 1: " + e);
        }

        // Clean user 2
        try {
            user2.getPlayer();
        } catch (NoSuchPlayerException e) {
            // Assert user is not in a game
            try {
                user2.createGame("cleanUp2", 1);
            } catch (Exception ex) {
                fail("An exception was thrown during cleanUp game creation 2: " + ex);
            }

        } catch (Exception e) {
            fail("An exception was thrown during cleanUp 2: " + e);
        }

        // Clean user 3
        try {
            user3.getPlayer();
        } catch (NoSuchPlayerException e) {
            // Assert user is not in a game
            try {
                user3.createGame("cleanUp3", 1);
            } catch (Exception ex) {
                fail("An exception was thrown during cleanUp game creation 3: " + ex);
            }

        } catch (Exception e) {
            fail("An exception was thrown during cleanUp 3: " + e);
        }
    }

    //region Constructor tests

    /**
     * General test for the constructor.
     *
     * @utp.description Tests whether the {@code User} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        try {
            User consUser = new User(user2.getUid());
            // Test
            assertNotNull(consUser.getUid(), "Uid still null");
            assertEquals(user2.getIssuer(), consUser.getIssuer(), "Issuer was not set correctly!");
            assertEquals(user2.getSub(), consUser.getSub(), "Sub was not set correctly!");
            assertEquals(user2.getUsername(), consUser.getUsername(), "Username was not set correctly!");
            //assertNull(consUser.getPlayer(), "Player should be null!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    //endregion

    //region createGame() tests

    /**
     * General test, a new player creates a game.
     *
     * @utp.description Tests whether a {@code User} can create a new game when {@code player == null}.
     */
    @Test
    public void createGameTest1() {
        try {
            // Create a game
            int instanceId = user.createGame("createGameTest1", 1);

            // Test
            assertNotNull(user.getPlayer(), "Player not created");
            assertTrue(InstanceDB.generateInstanceIDList().contains(instanceId),
                    "Instance with id " + instanceId + " was not found!");
            assertEquals("createGameTest1", InstanceDB.getGameNameByInstanceID(instanceId), "Game name mismatch!");
        } catch (Exception e) {
            fail("Exception was thrown: " + e);
        }
    }

    /**
     * Two distinct users should be able to create two distinct games.
     *
     * @utp.description Tests whether two distinct {@code User} instances can create two distinct games.
     */
    @Test
    public void createGameTest2() {

        // create user 1s game
        try {
            int instanceId1 = user.createGame("createGameTest2User1", 1);

            // Test
            assertNotNull(user.getPlayer(), "Player not created");
            assertTrue(InstanceDB.generateInstanceIDList().contains(instanceId1),
                    "Instance with id " + instanceId1 + " was not found!");
            assertEquals("createGameTest2User1", InstanceDB.getGameNameByInstanceID(instanceId1),
                    "Game name mismatch!");
        } catch (Exception e) {
            fail("Exception was thrown when user 1 tried to make a game: " + e);
        }

        // create user 2s game
        try {
            int instanceId2 = user2.createGame("createGameTest2User2", 2);

            // Test
            assertNotNull(user2.getPlayer(), "Player not created");
            assertTrue(InstanceDB.generateInstanceIDList().contains(instanceId2),
                    "Instance with id " + instanceId2 + " was not found!");
            assertEquals("createGameTest2User2", InstanceDB.getGameNameByInstanceID(instanceId2),
                    "Game name mismatch!");
        } catch (Exception e) {
            fail("Exception was thrown when user 2 tried to make a game: " + e);
        }

        // Test that users and players are distinct
        try {
            assertNotEquals(user.getUid(), user2.getUid(), "Users not distinct");
            assertNotEquals(user.getPlayer().getPlayerIdentifier().instanceID(),
                    user2.getPlayer().getPlayerIdentifier().instanceID(), "Players not distinct");
        } catch (Exception e) {
            fail("Exception was thrown when comparing players: " + e);
        }

    }

    /**
     * Try to create two games, AlreadyInGameException should be thrown.
     *
     * @utp.description Tests whether when an exception is thrown when one {@code User} creates a game, when {@code
     * player != null}.
     */
    @Test
    public void createGameExceptionTest() {
        // creating the first game (should go fine)
        try {
            user.createGame("createGameExceptionTest1", 3);
            assertNotNull(user.getPlayer(), "Player not created");
        } catch (Exception e) {
            fail("Exception was thrown while creating the first game: " + e);
        }

        // try to create second game (exception should be thrown)
        try {
            user.createGame("createGameExceptionTest2", 4);
            fail("Should have thrown AlreadyInGameException");
        } catch (Exception e) {
            Class expected = AlreadyInGameException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }
    //endregion

    //region joinGame Tests

    /** Helper function that creates a new game instance. */
    private IInstance createNewGame(User gameleader) {
        // create instance manager
        int instanceId = 0;
        try {
            instanceId = gameleader.createGame("joinGameTests", 5);
            return InstanceManager.getInstanceManager().getInstance(instanceId);
        } catch (Exception e) {
            fail("An exception was thrown when creating a game: " + e);
        }
        fail("Should never get here!");
        // return null to make compiler happy
        return null;
    }

    /**
     * General test, where a new player joins a game.
     *
     * @utp.description Test whether a {@code User} can join a game, when {@code player == null}.
     */
    @Test
    public void joinGameTest1() {
        // create game
        IInstance game = createNewGame(user);

        // Join the game
        try {
            user2.joinGame(game.getIid());

            assertNotNull(user2.getPlayer(), "Player of user1 still null");
            assertEquals(game.getIid(), user2.getPlayer().getPlayerIdentifier().instanceID(), "Instance ID mismatch!");
            assertNotEquals(user.getPlayer(), user2.getPlayer(), "Players are the same.");
            assertNotEquals(user.getPlayer().getPlayerIdentifier().userID(),
                    user2.getPlayer().getPlayerIdentifier().userID(), "User IDs are the same!");

        } catch (Exception e) {
            fail("An exception was thrown after joining the game (or during testing): " + e);
        }

    }

    /**
     * Try to join two distinct games, AlreadyInGameException should be thrown.
     *
     * @utp.description Tests whether when an exception is thrown when one {@code User} joins a game, when {@code player
     * != null}.
     */
    @Test
    public void joinGameExceptionTest1() {
        // create game
        IInstance game = createNewGame(user);

        // joining the first game (should go fine)
        try {
            user2.joinGame(game.getIid());

            assertNotNull(user2.getPlayer(), "Player of user1 still null");
            assertEquals(game.getIid(), user2.getPlayer().getPlayerIdentifier().instanceID(), "Instance ID mismatch!");
            assertNotEquals(user.getPlayer(), user2.getPlayer(), "Players are the same.");
            assertNotEquals(user.getPlayer().getPlayerIdentifier().userID(),
                    user2.getPlayer().getPlayerIdentifier().userID(), "User IDs are the same!");
        } catch (Exception e) {
            fail("Exception was thrown while joining the first game: " + e);
        }

        //create second game
        IInstance game2 = createNewGame(user3);

        //try to join second game (exception should be thrown)
        try {
            user2.joinGame(game2.getIid());
            fail("Should have thrown AlreadyInGameException");
        } catch (Exception e) {
            Class expected = AlreadyInGameException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }

    /**
     * Try to join the same game twice, AlreadyInGameException should be thrown.
     *
     * @utp.description Tests whether an exception is thrown when {@code User} joins the same game instance twice.
     */
    @Test
    public void joinGameExceptionTest2() {
        // create game
        IInstance game = createNewGame(user);
        // Create a new user in the database

        // joining the first game (should go fine)
        try {
            user2.joinGame(game.getIid());

            assertNotNull(user2.getPlayer(), "Player of user1 still null");
            assertEquals(game.getIid(), user2.getPlayer().getPlayerIdentifier().instanceID(), "Instance ID mismatch!");
            assertNotEquals(user.getPlayer(), user2.getPlayer(), "Players are the same.");
            assertNotEquals(user.getPlayer().getPlayerIdentifier().userID(),
                    user2.getPlayer().getPlayerIdentifier().userID(), "User IDs are the same!");
        } catch (Exception e) {
            fail("Exception was thrown while joining the first game: " + e);
        }

        //try to join second game (exception should be thrown)
        try {
            user2.joinGame(game.getIid());
            fail("Should have thrown AlreadyInGameException");
        } catch (Exception e) {
            Class expected = AlreadyInGameException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }

    //endregion

    /**
     * leaveGame test, user joins and then leaves a game.
     *
     * @utp.description Test whether a {@code user} leaves a game correctly when the {@code instance} has not started.
     */
    @Test
    public void leaveGameTest() {
        // create game
        IInstance game = createNewGame(user);
        // user 2 joins the game
        try {
            user2.joinGame(game.getIid());
        } catch (Exception e) {
            fail("Exception was thrown while joining the game: " + e);
        }
        // user 2 leaves the game again
        try {
            user2.leaveGame(game.getIid());
        } catch (Exception e) {
            fail("Exception was thrown while leaving the game: " + e);
        }
    }

    /**
     * leaveGame test, user joins a game, the game is started and user tries to leave the game. An exception is thrown.
     *
     * @utp.description Test whether an {@code GameAlreadyStartedException} is thrown when an {@code user} tries to
     * leave a started game.
     */
    @Test
    public void leaveGameExceptionTest() {
        // create game
        IInstance game = createNewGame(user);
        // user 2 joins the game
        try {
            user2.joinGame(game.getIid());
        } catch (Exception e) {
            fail("Exception was thrown while joining the game: " + e);
        }
        // start the game
        try {
            game.startGame(user.getUid());
        } catch (Exception e) {
            fail("Exception was thrown while starting the game: " + e);
        }
        // user 2 tries to leave the game again
        try {
            user2.leaveGame(game.getIid());
            fail("User should not be able to leave the game, GameAlreadyStartedException should have beent thrown!");
        } catch (GameAlreadyStartedException e) {
            assertNotNull(e.getMessage(), "message should not be null!");
        } catch (Exception e) {
            fail("Should have thrown GameAlreadyStartedException. Instead the following was thrown: " + e);
        }
    }

    /**
     * setUsername test, set new username and test
     *
     * @utp.description Test whether the new username is set correctly.
     */
    @Test
    public void setUsernameTest() {
        try {
            assertEquals("Username", user.getUsername(), "Username is not equal before setting!");
            user.setUsername("NoobMaster69");
            assertEquals("NoobMaster69", user.getUsername(), "Username is not equal after setting!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
}
