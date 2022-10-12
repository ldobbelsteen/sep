package org.lukos.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.location.HouseDoesNotExistException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.location.House;
import org.lukos.model.location.HouseState;
import org.lukos.model.location.states.Cleaned;
import org.lukos.model.location.states.Repaired;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.lukos.database.HouseDB.*;
import static org.lukos.database.LocationDB.createNewLocation;

/**
 * Test cases for {@link HouseDB}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class HouseDBTest extends GameTest {

    private IInstance instance;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() {
        try {
            User user1 = UserManager.getInstance().createUser(new IssuerSub("HouseIssuer", "Sub1"), "User1");
            User user2 = UserManager.getInstance().createUser(new IssuerSub("HouseIssuer", "Sub2"), "User2");
            int id = user1.createGame("HouseGame1", 1);
            user2.joinGame(id);

            this.player1 = user1.getPlayer();
            this.player2 = user2.getPlayer();

            this.instance = InstanceManager.getInstanceManager().getInstance(id);
            this.instance.startGame(user1.getUid());
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /** @utp.description Testing the constructor for HouseDB */
    @Test
    public void constructorTest() {
        try {
            new HouseDB();
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /** @utp.description Tests whether the {@code getHomelessPlayers()} function returns the homeless players. */
    @Test
    public void getHomelessPlayersTest() {
        try {
            House house = new House(player1.getHouse());
            house.soak();
            house.burn();

            List<PlayerIdentifier> playerIdentifierList = getHomelessPlayers(this.instance.getIid());
            assertEquals(1, playerIdentifierList.size(), "Size should be 1.");
            assertEquals(player1.getPlayerIdentifier(), playerIdentifierList.get(0), "Player 1 should be homeless.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /** @utp.description Tests whether the {@code getHomeOwners()} function returns the homeowners. */
    @Test
    public void getHomeOwnersTest() {
        try {
            List<PlayerIdentifier> playerIDs = new ArrayList<>();
            playerIDs.add(player1.getPlayerIdentifier());
            playerIDs.add(player2.getPlayerIdentifier());

            List<PlayerIdentifier> retrievedIDs = getHomeOwners(this.instance.getIid());
            assertEquals(2, retrievedIDs.size(), "Size should be 2.");
            assertTrue(playerIDs.containsAll(retrievedIDs), "playerIDs should contain retrieveIDs");
            assertTrue(retrievedIDs.containsAll(playerIDs), "retrievedIDs should contain playerIDs");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code setHousePlayerByID()} function sets a house for player with the
     *         given ID.
     */
    @Test
    public void setHousePlayerByIDTest() {
        try {
            User user1 = UserManager.getInstance().createUser(new IssuerSub("HouseIssuer", "Sub3"), "User3");
            int id = user1.createGame("HouseGame2", 1);
            Player player1 = user1.getPlayer();
            IInstance instance = InstanceManager.getInstanceManager().getInstance(id);
            int locationID = createNewLocation(instance.getIid());
            setHousePlayerByID(player1.getPlayerIdentifier(), locationID);
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code getHousePlayerByID()} throws an exception when given a
     *         non-existing ID.
     */
    @Test
    public void getHousePlayerByIDExceptionTest() {
        Class<?> expected = HouseDoesNotExistException.class;
        try {
            getHousePlayerByID(new PlayerIdentifier(666, 666));
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether the {@code getHousePlayerByID()} function returns the correct house ID. */
    @Test
    public void getHousePlayerByIDTest() {
        try {
            assertEquals(player1.getHouse(), getHousePlayerByID(player1.getPlayerIdentifier()),
                    "Houses should be the same");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code existHouseByID()} function correctly whether a house does or
     *         does not exist.
     */
    @Test
    public void existHouseByIDTest() {
        try {
            assertTrue(existHouseByID(player1.getHouse()));
            assertFalse(existHouseByID(666));
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code existOrCreateHouseByID()} function it does nothing when a house
     *         already exist.
     */
    @Test
    public void existOrCreateHouseByIDTest1() {
        try {
            assertTrue(existHouseByID(player1.getHouse()));
            House house = new House(player1.getHouse());
            HouseState state = house.getState();
            int stateDay = house.getStateDay();
            existOrCreateHouseByID(player1.getPlayerIdentifier(), house.getId(), state, stateDay);
            assertTrue(existHouseByID(player1.getHouse()));
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code existOrCreateHouseByID()} function creates a house if it does
     *         not exist.
     */
    @Test
    public void existOrCreateHouseByIDTest2() {
        try {
            User user1 = UserManager.getInstance().createUser(new IssuerSub("HouseIssuer", "Sub3"), "User3");
            int id = user1.createGame("HouseGame2", 1);
            Player player1 = user1.getPlayer();
            IInstance instance = InstanceManager.getInstanceManager().getInstance(id);
            int locationID = createNewLocation(instance.getIid());

            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("DELETE FROM Location WHERE locationID=?;");
            statement.setInt(1, player1.getHouse());
            DatabaseConnection.getInstance().writeStatement(statement);

            assertFalse(existHouseByID(player1.getHouse()), "House should not exist");
            existOrCreateHouseByID(player1.getPlayerIdentifier(), locationID, Cleaned.getInstance(), 1);
            assertTrue(existHouseByID(player1.getHouse()), "House should exist");
            assertEquals(Cleaned.getInstance().getClass(), getHouseState(player1.getHouse()).getClass(),
                    "Should be cleaned.");
            assertEquals(1, getHouseStateDay(player1.getHouse()), "Should be state day 1.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /** @utp.description Tests whether the {@code getHouseState()} function returns the state of the house. */
    @Test
    public void getHouseStateTest() {
        try {
            assertEquals(Repaired.getInstance().getClass(), getHouseState(player1.getHouse()).getClass(),
                    "House state should be the same.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code getHouseState()} throws an exception when given a
     *         non-existing ID.
     */
    @Test
    public void getHouseStateExceptionTest() {
        Class<?> expected = SQLException.class;
        try {
            getHouseState(666);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code getHouseState()} function returns the day of the state of the
     *         house.
     */
    @Test
    public void getHouseStateDayTest() {
        try {
            assertEquals(0, getHouseStateDay(player1.getHouse()), "House day should be the same");
            House house = new House(player1.getHouse());
            house.soak();
            house.burn();
            house.build();
            assertEquals(1, getHouseStateDay(player1.getHouse()), "House day should be 1");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code getHouseStateDay()} throws an exception when given a
     *         non-existing ID.
     */
    @Test
    public void getHouseStateDayExceptionTest() {
        Class<?> expected = NoSuchPlayerException.class;
        try {
            getHouseStateDay(666);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the {@code modifyHouseState()} function correctly modifies the state of
     *         the house.
     */
    @Test
    public void modifyHouseStateTest() {
        try {
            assertEquals(Repaired.getInstance().getClass(), getHouseState(player1.getHouse()).getClass(),
                    "House state should be Repaired.");
            modifyHouseState(player1.getHouse(), Cleaned.getInstance());
            assertEquals(Cleaned.getInstance().getClass(), getHouseState(player1.getHouse()).getClass(),
                    "House state should be Cleaned.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code modifyHouseStateDay()} function correctly modifies the day of
     *         the state of the house.
     */
    @Test
    public void modifyHouseStateDayTest() {
        try {
            assertEquals(0, getHouseStateDay(player1.getHouse()), "House state day should be 0.");
            modifyHouseStateDay(player1.getHouse(), 5);
            assertEquals(5, getHouseStateDay(player1.getHouse()), "House state day should be 5.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }
}
