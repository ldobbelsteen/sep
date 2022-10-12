package org.lukos.model.winhandler;

import org.junit.jupiter.api.Test;
import org.lukos.model.rolesystem.Group;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code WinArsonist}.
 *
 * @author Rick van der Heijden (1461923)
 * @author Sander van Heesch (1436708)
 * @author Valentijn van den Berg (1457446)
 * @since 07-03-2022
 */
public class WinArsonistTest extends WinHandlerTest {

    @Override
    protected WinHandler createNewInstance() {
        return new WinArsonist();
    }

    @Override
    protected WinHandler createNewInstance(WinHandler next) {
        return new WinArsonist(next);
    }

    /**
     * Tests the constructor without parameters.
     *
     * @utp.description Tests whether the constructor behaves as intended.
     */
    @Test
    public void testEmptyConstructor() {
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.ARSONIST, instance.getGroup(), "Group should be arsonist.");
        assertNull(instance.getNext(), "Next should be null.");
    }

    /**
     * Tests the constructor with parameter.
     *
     * @utp.description Tests whether the constructor behaves as intended.
     */
    @Test
    public void testConstructor() {
        WinHandler instance = new WinArsonist(this.instance);
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.ARSONIST, instance.getGroup(), "Group should be arsonist.");
        assertNotNull(instance.getNext(), "Next should not be null.");
        assertEquals(this.instance, instance.getNext(), "Instance should have this.instance as next.");
    }

    /** @utp.description Tests whether the {@code checkWin()} function behaves as intended. */
    @Test
    public void checkWinTest() {
        try {
            assertNull((new WinArsonist()).checkWin(new ArrayList<>()));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the constructor with 1 parameter behaves as intended.
     */
    @Override @Test
    public void testConstructorSingleParameter() {
        super.testConstructorSingleParameter();
    }

    /**
     * @utp.description Tests whether the constructor with multiple parameters behaves as intended.
     */
    @Override @Test
    public void testConstructorMultiParameter() {
        super.testConstructorMultiParameter();
    }

    /**
     * @utp.description Tests whether the constructor with 1 parameter behaves as intended.
     */
    @Override @Test
    public void testInstanceConstructorNoParameter() {
        super.testInstanceConstructorNoParameter();
    }

    /**
     * @utp.description Tests whether the constructor with multiple parameters behaves as intended.
     */
    @Override @Test
    public void testInstanceConstructorSingleParameter() {
        super.testInstanceConstructorSingleParameter();
    }

    /**
     * @utp.description Tests whether the {@code setNext()} method behaves as intended.
     */
    @Override @Test
    public void testSetNext() {
        super.testSetNext();
    }

    /**
     * @utp.description Tests whether the {@code setNext()} method throws an error when someone tries to cut off the
     * handler chain.
     */
    @Override @Test
    public void testSetNextException() {
        super.testSetNextException();
    }

    /**
     * @utp.description Tests whether the function {@code checkWin()} behaves as intended.
     */
    @Override @Test
    public void testCheckWin() {
        super.testCheckWin();
    }

    /** @utp.description Tests whether the function {@code listGroups()} returns the right groups. */
    @Override @Test
    public void listGroupsTest() {
        super.listGroupsTest();
    }
}

// TODO: The code below here are test cases for an implemented Arsonist winhandler, but is has not been implemented yet.

//    private Player playerArsonist;
//    private House arsonistHouse;
//    private final ArrayList<Player> players = new ArrayList<>();
//    private UUID iid;
//    private Instance gameInstance;
//    private UUID gameMaster;
//    private final ArrayList<House> houses = new ArrayList<>();
//
//    /** Soak all houses expect the house of the arsonist */
//    private void soakOtherHouses() {
//        //Soak all the other houses
//        for (House house: houses) {
//            try {
//                house.soak();
//            } catch (Exception e) {
//                fail("An exception was thrown when soaking house " + houses.indexOf(house) + ": " + e);
//            }
//        }
//    }
//
//    /** Burns all soaked houses, except the house of the arsonist */
//    private void burnOtherHouses() {
//        //Burn all the other houses
//        for (House house: houses) {
//            try {
//                house.burn();
//            } catch (Exception e) {
//                fail("An exception was thrown when burning house " + houses.indexOf(house) + ": " + e);
//            }
//        }
//    }
//
//    @BeforeEach
//    public void beforeTests() {
//        // Get instance manager
//        InstanceManager im = InstanceManager.getInstanceManager();
//        //Create UUID
//        gameMaster = UUID.randomUUID();
//        //Create a new game
//        iid = im.createInstance(gameMaster);
//        gameInstance = im.getInstance(iid);
//
//        playerArsonist = new Player(iid, gameMaster);
//        gameInstance.addPlayer(playerArsonist);
//
//        //Give the player the Arsonist role
//        playerArsonist.setMainRole(new Arsonist(1));
//
//        //Give the Arsonist a house
//        arsonistHouse = new House();
//        //Add the player to the house of the Arsonist
//        playerArsonist.setHouse(arsonistHouse);
//
//        //Create the other players and their houses
//        for (int i = 0; i < 11; i++) {
//            players.add(new Player(iid, UUID.randomUUID()));
//            gameInstance.addPlayer(players.get(i));
//            houses.add(new House());
//            players.get(i).setHouse(houses.get(i));
//        }
//
//        //Start new game
//        try {
//            gameInstance.startGame(gameMaster);
//        } catch (Exception e) {
//            fail("An exception was thrown" + e);
//        }
//
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, just after the game has
//        started!");
//
//    }
//
//    /** Burn all houses except for the arsonists house, Arsonist group should win */
//    @Test
//    @Disabled
//    public void CheckArsonistWinTest() {
//        // Soak and burn houses
//        soakOtherHouses();
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, before burning!");
//        burnOtherHouses();
//        assertEquals(Group.ARSONIST, createNewInstance().checkWin(gameInstance), "The arsonist should have won!");
//    }
//
//    /** Kill the arsonist before the win condition of the arsonist is met */
//    @Disabled
//    @Test
//    public void ArsonistDeadWinTest() {
//        // Soak houses
//        soakOtherHouses();
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, before burning!");
//        // Kill the arsonist
//        gameInstance.killPlayer(playerArsonist);
//        // Burn houses
//        burnOtherHouses();
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, the arsonist is dead!");
//    }
//
//    /** Repair one of the burned houses before the win condition of the arsonist is met */
//    @Disabled
//    @Test
//    public void ArsonistRepairedHouseWinTest(){
//        // Soak and burn all houses except for two (arsonists and one other house)
//        for (int i = 0; i < houses.size() - 1; i++) {
//            try {
//                houses.get(i).soak();
//                houses.get(i).burn();
//            } catch (Exception e) {
//                fail("An exception was thrown when soaking or burning house " + houses.get(i) + ": " + e);
//            }
//        }
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, win condition is not met!");
//        //repair one of the houses that was burned
//        try {
//            houses.get(2).repaired();
//        } catch (Exception e) {
//            fail("An exception was thrown when repairing house " + houses.get(2) + ": " + e);
//        }
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, after repairing!");
//        //Soak and burn the one house that was not burned yet and is not from the arsonist.
//        try {
//            houses.get(houses.size() - 1).soak();
//            houses.get(houses.size() - 1).burn();
//        } catch (Exception e) {
//            fail("An exception was thrown when soaking or burning house " + houses.get(12) + ": " + e);
//        }
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, because house 3 is still
//        standing!");
//    }
//
//    /** Revive the arsonist when the win condition of the arsonist is met, Arsonist group should win */
//    @Disabled
//    @Test
//    public void ArsonistRevivedWinTest() {
//    }
//
//    /** have multiple arsonist in a game, only one of them should win */
//    @Disabled
//    @Test
//    public void MultipleArsonistWinTest() {
//        //Add another arsonist to the game
//        Player playerArsonist2 = new Player(iid, UUID.randomUUID());
//        gameInstance.addPlayer(playerArsonist2);
//        playerArsonist2.setMainRole(new Arsonist(1));
//
//        //create house for second arsonist
//        House arsonistHouse2 = new House();
//        playerArsonist2.setHouse(arsonistHouse2);
//
//        // Soak and burn houses
//        soakOtherHouses();
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, before burning!");
//        burnOtherHouses();
//
//        // Soak and burn the arsonistHouse
//        try {
//            arsonistHouse.soak();
//            arsonistHouse.burn();
//        } catch (Exception e) {
//            fail("An exception was thrown when soaking or burning the arsonistHouse: " + e);
//        }
//        assertEquals(Group.ARSONIST, createNewInstance().checkWin(gameInstance), "The arsonist should have won!");
//    }
