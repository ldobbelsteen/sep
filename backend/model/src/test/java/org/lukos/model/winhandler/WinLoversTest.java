package org.lukos.model.winhandler;

import org.junit.jupiter.api.Test;
import org.lukos.model.rolesystem.Group;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code WinLovers}.
 *
 * @author Rick van der Heijden (1461923)
 * @author Sander van Heesch (1436708)
 * @since 07-03-2022
 */
public class WinLoversTest extends WinHandlerTest {

    @Override
    protected WinHandler createNewInstance() {
        return new WinLovers();
    }

    @Override
    protected WinHandler createNewInstance(WinHandler next) {
        return new WinLovers(next);
    }

    /**
     * Tests the constructor without parameters.
     *
     * @utp.description Tests whether the constructor behaves as intended.
     */
    @Test
    public void testEmptyConstructor() {
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.LOVERS, instance.getGroup(), "Group should be lovers.");
        assertNull(instance.getNext(), "Next should be null.");
    }

    /**
     * Tests the constructor with parameter.
     *
     * @utp.description Tests whether the constructor behaves as intended.
     */
    @Test
    public void testConstructor() {
        WinHandler instance = new WinLovers(this.instance);
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.LOVERS, instance.getGroup(), "Group should be lovers.");
        assertNotNull(instance.getNext(), "Next should not be null.");
        assertEquals(this.instance, instance.getNext(), "Instance should have this.instance as next.");
    }

    /** @utp.description Tests whether the {@code checkWin()} function behaves as intended. */
    @Test
    public void checkWinTest() {
        try {
            assertNull((new WinLovers()).checkWin(new ArrayList<>()));
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

//    private Player playerMatchMaker;
//    private final List<Player> players = new ArrayList<>();
//    private UUID iid;
//    private Instance gameInstance;
//    private UUID gameMaster;
//
//    /** Convert  a list of players to lovers */
//    private void convertToLovers(List<Player> couple) {
//        Player playerSelf;
//            //loop through the list of players that need to be matched
//            for (int i = 0; i < couple.size(); i++) {
//                //the current player is removed from the list
//                playerSelf = couple.remove(0);
//                try {
//                    //give the player the lover role and a list of all other players the player is matched with
//                    playerSelf.addDoubleRole(new Lover(couple));
//                } catch (Exception e) {
//                    fail("An exception was thrown: " + e);
//                }
//                //the current player is added back to the list
//                couple.add(playerSelf);
//            }
//        }
//
//    @BeforeEach
//    public void beforeTest() {
//        // Get instance manager
//        InstanceManager im = InstanceManager.getInstanceManager();
//        //Create UUID
//        gameMaster = UUID.randomUUID();
//        //Create a new game
//        iid = im.createInstance(gameMaster);
//        gameInstance = im.getInstance(iid);
//
//        playerMatchMaker = new Player(iid, gameMaster);
//        gameInstance.addPlayer(playerMatchMaker);
//
//        playerMatchMaker.setMainRole(new Matchmaker());
//
//
//        //Create the other players
//        for (int i = 0; i < 11; i++) {
//            players.add(new Player(iid, UUID.randomUUID()));
//            gameInstance.addPlayer(players.get(i));
//        }
//
//        //Start new game
//        try {
//            gameInstance.startGame(gameMaster);
//        } catch (Exception e) {
//            fail("An exception was thrown" + e);
//        }
//
//        assertNull(createNewInstance().checkWin(gameInstance), "No one should have won, just after the game has started!");
//    }
//
//    /** The only players in a game left alive are lovers of each other, lovers group should win */
//    @Disabled
//    @Test
//    public void CheckLoversWinTest() {
//        //Convert two player to lovers
//        List<Player> couple = new ArrayList<>();
//        couple.add(players.get(players.size() - 1));
//        couple.add(players.get(players.size() - 2));
//        convertToLovers(couple);
//        assertNull(createNewInstance().checkWin(gameInstance), "The only players left are not all lovers of each other");
//
//        //kill all other players
//        for (int i = 0; i < players.size() - 3; i++) {
//            gameInstance.killPlayer(players.get(i));
//        }
//        gameInstance.killPlayer(playerMatchMaker);
//        assertEquals(Group.LOVERS, createNewInstance().checkWin(gameInstance), "The lovers group should have won!");
//    }
//
//    /** The only players in a game left alive are three lovers of each other, lovers group should win */
//    @Disabled
//    @Test
//    public void CheckThreeLoversWinTest() {
//        //Convert three players to lovers
//        List<Player> couple = new ArrayList<>();
//        couple.add(players.get(players.size() - 1));
//        couple.add(players.get(players.size() - 2));
//        couple.add(players.get(players.size() - 3));
//        assertNull(createNewInstance().checkWin(gameInstance), "The only players left are not all lovers of each other");
//
//        //kill all other player
//        for (int i = 0; i < players.size() - 4; i++) {
//            gameInstance.killPlayer(players.get(i));
//        }
//        gameInstance.killPlayer(playerMatchMaker);
//        assertEquals(Group.LOVERS, createNewInstance().checkWin(gameInstance), "The lovers group should have won!");
//    }
//
//    /** one of the lovers in a couple died, lovers group should not win */
//    @Disabled
//    @Test
//    public void DeadCoupleWinTest() {
//        //Convert two player to lovers
//        List<Player> couple = new ArrayList<>();
//        couple.add(players.get(players.size() - 1));
//        couple.add(players.get(players.size() - 2));
//        convertToLovers(couple);
//        assertNull(createNewInstance().checkWin(gameInstance), "The only players left are not all lovers of each other");
//        //kill one of the player that are in the couple
//        gameInstance.killPlayer(players.get(players.size() - 1));
//        assertNull(createNewInstance().checkWin(gameInstance), "The lovers are dead and should not win");
//        //kill all other player
//        for (int i = 0; i < players.size() - 3; i++) {
//            gameInstance.killPlayer(players.get(i));
//        }
//        gameInstance.killPlayer(playerMatchMaker);
//        assertNull(createNewInstance().checkWin(gameInstance), "The lovers are dead and should not win");
//    }
//
//    @Disabled
//    @Test
//    public void MultipleCouplesWinTest() {
//        //TODO: make sure multiple couples are seperated from each other
//    }
