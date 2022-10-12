package org.lukos.model.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.PlayerDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.location.states.Burned;
import org.lukos.model.location.states.Repaired;
import org.lukos.model.location.states.Soaked;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code House}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 24-02-2022
 */
public class HouseTest extends LocationTest {

    /** Holding the player object needed to link a {@link House} to. */
    private Player player;

    /**
     * Creates a player.
     *
     * @throws SQLException  when a database operation fails
     * @throws GameException when a game-logic operation fails
     */
    private void createPlayer() throws SQLException, GameException {
        User user1 = UserManager.getInstance().createUser(new IssuerSub("HouseTest", "User1"), "User1");
        user1.createGame("HouseTestInstance", 1);
        this.player = user1.getPlayer();
    }

    /**
     * Creates a player if the player does not exist.
     *
     * @throws SQLException  when a database operation fails
     * @throws GameException when a game-logic operation fails
     */
    private void createIfNotExistPlayer() throws SQLException, GameException {
        if (player == null) {
            createPlayer();
        }
        try {
            PlayerDB.getPlayerFromUserByID(player.getPlayerIdentifier().userID());
        } catch (NoSuchPlayerException | SQLException e) {
            createPlayer();
        }
    }

    /**
     * Sets up the parts needed to do the tests, before each test.
     */
    @BeforeEach
    public void setUpHouse() {
        try {
            createIfNotExistPlayer();
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Override
    protected Location createNewInstance() {
        try {
            createIfNotExistPlayer();
            return new House(this.player.getPlayerIdentifier(), Repaired.getInstance());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
        return null;
    }

    /**
     * @utp.description Tests whether the constructor for an existing house works as intended.
     */
    @Test
    public void testHouseConstructor() {
        try {
            int id = (new House(this.player.getPlayerIdentifier(), Repaired.getInstance())).getId();
            House house = new House(id);
            assertEquals(Repaired.getInstance(), house.getState());
            assertEquals(0, house.getStateDay());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether constructor for house creation has the state that is injected.
     */
    @Test
    public void testHouseNonEmptyConstructorSoaked() {
        try {
            // Tests soak state
            House house = new House(this.player.getPlayerIdentifier(), Soaked.getInstance());
            assertEquals(Soaked.getInstance(), house.getState());
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether constructor for house creation has the state that is injected.
     */
    @Test
    public void testHouseNonEmptyConstructorBurned() {
        try {
            // Tests burned state
            House house = new House(this.player.getPlayerIdentifier(), Burned.getInstance());
            assertEquals(Burned.getInstance(), house.getState());
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether constructor for house creation has the state that is injected.
     */
    @Test
    public void testHouseNonEmptyConstructorRepaired() {
        try {
            // Tests build state
            House house = new House(this.player.getPlayerIdentifier(), Repaired.getInstance());
            assertEquals(Repaired.getInstance(), house.getState());
            assertEquals(0, house.getStateDay());
        } catch (SQLException | NoSuchPlayerException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code soak()} function works.
     */
    @Test
    public void testSoak() {
        try {
            HouseState state = Repaired.getInstance();
            House house = new House(this.player.getPlayerIdentifier(), state);
            try {
                house.soak();
            } catch (WrongStateMethodException e) {
                fail("Shouldn't have thrown an error.");
            }
            HouseState newState = house.getState();
            assertNotEquals(state, newState);
            assertEquals(Soaked.getInstance(), newState);
            assertEquals(0, house.getStateDay());
        } catch (SQLException | NoSuchPlayerException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code burn()} function works.
     */
    @Test
    public void testBurn() {
        try {
            HouseState state = Soaked.getInstance();
            House house = new House(this.player.getPlayerIdentifier(), state);
            try {
                house.burn();
            } catch (WrongStateMethodException e) {
                fail("Shouldn't have thrown an error.");
            }
            HouseState newState = house.getState();
            assertNotEquals(state, newState);
            assertEquals(Burned.getInstance(), newState);
            assertEquals(0, house.getStateDay());
        } catch (SQLException | NoSuchPlayerException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code build()} function works.
     */
    @Test
    public void testBuild() {
        try {
            HouseState state = Burned.getInstance();
            House house = new House(this.player.getPlayerIdentifier(), state);
            try {
                house.build();
                house.build();
                house.build();
            } catch (WrongStateMethodException e) {
                fail("Shouldn't have thrown an error.");
            }
            HouseState newState = house.getState();
            assertNotEquals(state, newState);
            assertEquals(Repaired.getInstance(), newState);
            assertEquals(0, house.getStateDay());
        } catch (SQLException | NoSuchPlayerException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code repaired()} function works.
     */
    @Test
    public void testRepaired() {
        try {
            HouseState state = Soaked.getInstance();
            House house = new House(this.player.getPlayerIdentifier(), state);
            try {
                house.repaired();
            } catch (WrongStateMethodException e) {
                fail("Shouldn't have thrown an error.");
            }
            HouseState newState = house.getState();
            assertNotEquals(state, newState);
            assertEquals(Repaired.getInstance(), newState);
            assertEquals(0, house.getStateDay());
        } catch (SQLException | NoSuchPlayerException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the players at location list is not {@code null} on initialization.
     */
    @Override @Test
    public void testConstructor() {
        super.testConstructor();
    }

    /**
     * @utp.description Tests whether all constructors for existing locations do not return a list of players at
     * location that is {@code null}.
     */
    @Override @Test
    public void testConstructor1() {
        super.testConstructor1();
    }

    /**
     * @utp.description Tests whether IDs are not equal to each other when creating multiple locations.
     */
    @Override @Test
    public void testConstructorIDEquality() {
        super.testConstructorIDEquality();
    }

    /**
     * @utp.description Tests the visitPlayer() function, whether a player actually visits that location.
     */
    @Override @Test
    public void testVisitPlayer() {
        super.testVisitPlayer();
    }

    /**
     * @utp.description Tests the visitPlayer() function, whether multiple players can visit a location.
     */
    @Override @Test
    public void testVisitPlayer1() {
        super.testVisitPlayer1();
    }

    /**
     * @utp.description Tests whether a player leaves his previous location once it visits a new location.
     */
    @Override @Test
    public void testLeavePlayer() {
        super.testLeavePlayer();
    }

    /**
     * @utp.description Tests whether if multiple players visit a new location they also leave their old location.
     */
    @Override @Test
    public void testLeavePlayer1() {
        super.testLeavePlayer1();
    }
}
