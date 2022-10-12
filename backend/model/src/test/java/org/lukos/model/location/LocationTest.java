package org.lukos.model.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code Location}.
 *
 * @author Rick van der Heijden (1461923)
 * @author Martijn van Andel (1251104)
 * @since 22-02-2022
 */
public abstract class LocationTest extends GameTest {
    /** Test instance. */
    protected Location instance;
    /** Players used for testing */
    private Player player1;
    private Player player2;
    private Player player3;

    /**
     * Creates a new instance of {@code Location}.
     *
     * @return The new instance.
     */
    protected abstract Location createNewInstance();

    /**
     * Sets instance to the given {@code Location}.
     *
     * @param instance The given {@code Location}
     */
    protected void setInstance(Location instance) {
        this.instance = instance;
    }

    /**
     * Method to set up the parts that are necessary for all tests, before each test.
     */
    @BeforeEach
    public void setUpLocation() {
        try {
            setInstance(createNewInstance());
            User user1 = UserManager.getInstance().createUser(new IssuerSub("LocationTest", "User1"), "User1");
            User user2 = UserManager.getInstance().createUser(new IssuerSub("LocationTest", "User2"), "User2");
            User user3 = UserManager.getInstance().createUser(new IssuerSub("LocationTest", "User3"), "User3");

            int instanceID = user1.createGame("LocationTestInstance", 1);
            user2.joinGame(instanceID);
            user3.joinGame(instanceID);

            this.player1 = user1.getPlayer();
            this.player2 = user2.getPlayer();
            this.player3 = user3.getPlayer();
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests whether the players at location list is not {@code null} on initialization.
     */
    @Test
    public void testConstructor() {
        try {
            assertNotNull(instance.getPlayersAtLocation(), "List should be initialized");
        } catch (SQLException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests whether all constructors for existing locations do not return a list of players at
     * location that is {@code null}.
     */
    @Test
    public void testConstructor1() {
        try {
            int id = instance.getId();
            Location loc = new LocationImpl(id, false);
            assertNotNull(loc.getPlayersAtLocation(), "List should be initialized");
            loc = new LocationImpl(id);
            assertNotNull(loc.getPlayersAtLocation(), "List should be initialized");
        } catch (SQLException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests whether IDs are not equal to each other when creating multiple locations.
     */
    @Test
    public void testConstructorIDEquality() {
        Location loc1 = createNewInstance();
        assertNotEquals(instance.getId(), loc1.getId(), "IDs should not be equal");

        Location loc2 = createNewInstance();
        assertNotEquals(instance.getId(), loc2.getId(), "IDs should not be equal");
        assertNotEquals(loc1.getId(), loc2.getId(), "IDs should not be equal");
    }

    /**
     * @utp.description Tests the visitPlayer() function, whether a player actually visits that location.
     */
    @Test
    public void testVisitPlayer() {
        try {
            // Creating player and getting its identifier
            PlayerIdentifier playerID = player1.getPlayerIdentifier();

            // Checking whether player can visit the location
            assertFalse(instance.getPlayersAtLocation().contains(playerID), "Player should not be at location.");
            instance.visitPlayer(player1);
            assertTrue(instance.getPlayersAtLocation().contains(playerID), "Player should be at location.");

            // Creating player1 and getting its identifier
            PlayerIdentifier playerID1 = player2.getPlayerIdentifier();

            // Checking whether player1 can visit the location
            assertFalse(instance.getPlayersAtLocation().contains(playerID1), "Player1 should not be at location.");
            instance.visitPlayer(player2);
            assertTrue(instance.getPlayersAtLocation().contains(playerID1), "Player1 should be at location.");
        } catch (SQLException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests the visitPlayer() function, whether multiple players can visit a location.
     */
    @Test
    public void testVisitPlayer1() {
        try {
            // Getting identifiers of players
            PlayerIdentifier playerID = player1.getPlayerIdentifier();
            PlayerIdentifier playerID1 = player2.getPlayerIdentifier();
            PlayerIdentifier playerID2 = player3.getPlayerIdentifier();

            // Checking whether none of the player is at the location
            assertFalse(instance.getPlayersAtLocation().contains(playerID), "Player1 should not be at location.");
            assertFalse(instance.getPlayersAtLocation().contains(playerID1), "Player2 should not be at location.");
            assertFalse(instance.getPlayersAtLocation().contains(playerID2), "Player3 should not be at location.");

            // Let all players visit the location
            instance.visitPlayer(player1);
            instance.visitPlayer(player2);
            instance.visitPlayer(player3);

            // Checking whether all players are at the location
            assertTrue(instance.getPlayersAtLocation().contains(playerID), "Player1 should be at location.");
            assertTrue(instance.getPlayersAtLocation().contains(playerID1), "Player2 should be at location.");
            assertTrue(instance.getPlayersAtLocation().contains(playerID2), "Player3 should be at location.");
        } catch (SQLException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests whether a player leaves his previous location once it visits a new location.
     */
    @Test
    public void testLeavePlayer() {
        try {
            // Getting player1's identifier
            PlayerIdentifier playerID = player1.getPlayerIdentifier();

            // Let player1 visit the location instance
            assertFalse(instance.getPlayersAtLocation().contains(playerID));
            instance.visitPlayer(player1);
            assertTrue(instance.getPlayersAtLocation().contains(playerID));

            // Create new location and make player1 visit that location, and checks whether he left the last location
            Location loc = createNewInstance();
            loc.visitPlayer(player1);
            assertFalse(instance.getPlayersAtLocation().contains(playerID));
            assertTrue(loc.getPlayersAtLocation().contains(playerID));
        } catch (SQLException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests whether if multiple players visit a new location they also leave their old location.
     */
    @Test
    public void testLeavePlayer1() {
        try {
            // Getting identifiers of players
            PlayerIdentifier playerID = player1.getPlayerIdentifier();
            PlayerIdentifier playerID1 = player2.getPlayerIdentifier();
            PlayerIdentifier playerID2 = player3.getPlayerIdentifier();

            // Making the players visit instance
            assertFalse(instance.getPlayersAtLocation().contains(playerID), "Player1 should not be at location.");
            assertFalse(instance.getPlayersAtLocation().contains(playerID1), "Player2 should not be at location.");
            assertFalse(instance.getPlayersAtLocation().contains(playerID2), "Player3 should not be at location.");
            instance.visitPlayer(player1);
            instance.visitPlayer(player2);
            instance.visitPlayer(player3);
            assertTrue(instance.getPlayersAtLocation().contains(playerID), "Player1 should be at location.");
            assertTrue(instance.getPlayersAtLocation().contains(playerID1), "Player2 should be at location.");
            assertTrue(instance.getPlayersAtLocation().contains(playerID2), "Player3 should be at location.");

            // Let players visit a new location
            Location location = createNewInstance();

            assertFalse(location.getPlayersAtLocation().contains(playerID), "Player1 should not be at location.");
            assertFalse(location.getPlayersAtLocation().contains(playerID1), "Player2 should not be at location.");
            assertFalse(location.getPlayersAtLocation().contains(playerID2), "Player3 should not be at location.");

            location.visitPlayer(player1);
            location.visitPlayer(player2);
            location.visitPlayer(player3);

            assertFalse(instance.getPlayersAtLocation().contains(playerID), "Player1 should not be at location.");
            assertFalse(instance.getPlayersAtLocation().contains(playerID1), "Player2 should not be at location.");
            assertFalse(instance.getPlayersAtLocation().contains(playerID2), "Player3 should not be at location.");
            assertTrue(location.getPlayersAtLocation().contains(playerID), "Player1 should be at location.");
            assertTrue(location.getPlayersAtLocation().contains(playerID1), "Player2 should be at location.");
            assertTrue(location.getPlayersAtLocation().contains(playerID2), "Player3 should be at location.");
        } catch (SQLException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * An implementation of the abstract {@link Location} used for testing purposes.
     */
    static class LocationImpl extends Location {

        public LocationImpl(int locationID) {
            super(locationID);
        }

        public LocationImpl(int instanceOrLocationID, boolean createNew) throws SQLException {
            super(instanceOrLocationID, createNew);
        }
    }
}
