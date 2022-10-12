package org.lukos.model.winhandler;

import org.junit.jupiter.api.Test;
import org.lukos.model.rolesystem.Group;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code WinCult}.
 *
 * @author Rick van der Heijden (1461923)
 * @author Sander van Heesch (1436708)
 * @author Valentijn van den Berg (1457446)
 * @since 07-03-2022
 */
public class WinCultTest extends WinHandlerTest {

    @Override
    protected WinHandler createNewInstance() {
        return new WinCult();
    }

    @Override
    protected WinHandler createNewInstance(WinHandler next) {
        return new WinCult(next);
    }

    /**
     * Tests the constructor without parameters.
     *
     * @utp.description Tests whether the constructor behaves as intended.
     */
    @Test
    public void testEmptyConstructor() {
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.CULT, instance.getGroup(), "Group should be cult.");
        assertNull(instance.getNext(), "Next should be null.");
    }

    /**
     * Tests the constructor with parameter.
     *
     * @utp.description Tests whether the constructor behaves as intended.
     */
    @Test
    public void testConstructor() {
        WinHandler instance = new WinCult(this.instance);
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.CULT, instance.getGroup(), "Group should be cult.");
        assertNotNull(instance.getNext(), "Next should not be null.");
        assertEquals(this.instance, instance.getNext(), "Instance should have this.instance as next.");
    }

    /** @utp.description Tests whether the {@code checkWin()} function behaves as intended. */
    @Test
    public void checkWinTest() {
        try {
            assertNull((new WinCult()).checkWin(new ArrayList<>()));
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

// TODO: The code below here are test cases for an implemented Cult winhandler, but is has not been implemented yet.

//    private Player playerCultLeader;
//    private final List<Player> players = new ArrayList<>();
//    private UUID iid;
//    private Instance gameInstance;
//    private UUID gameMaster;
//
//    /** Convert a single player at index {@code playersConverted} to the cult */
//    private void convertSinglePlayer(int index) {
//        List<Integer> list = new ArrayList<>();
//        list.add(index);
//        convertPlayers(list);
//    }
//    /**
//     * Convert the players at the indices in {@code playersConverted} to the cult,
//     * i.e. add the {@code Follower} double role to those players.
//     */
//    private void convertPlayers(List<Integer> playersConverted) {
//        for (Integer i: playersConverted) {
//            try {
//                players.get(i).addDoubleRole(new Follower());
//            } catch (Exception e) {
//                fail("An exception was thrown: " + e);
//            }
//        }
//    }
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
//        playerCultLeader = new Player(iid, gameMaster);
//        gameInstance.addPlayer(playerCultLeader);
//
//        //Give the player the cult leader role
//        playerCultLeader.setMainRole(new CultLeader());
//        try {
//            playerCultLeader.addDoubleRole(new Follower());
//        } catch (Exception e) {
//            fail("An exception was thrown: " + e);
//        }
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
//    /** Convert all the player in a game to a follower, cult group should win */
//    @Disabled
//    @Test
//    public void CheckCultWinTest() {
//        //specify which players need to be converted (all of them)
//        List<Integer> range = IntStream.rangeClosed(0, players.size() - 1).boxed().collect(Collectors.toList());
//        //convert specified players to followers
//        convertPlayers(range);
//        assertEquals(Group.CULT, createNewInstance().checkWin(gameInstance), "The cult group should have won!");
//    }
//
//    /** Convert every player except for one, then kill the cult leader, then kill the unconverted player,
//     * only after this the cult should win.
//     */
//    @Disabled
//    @Test
//    public void DeadCultLeaderWinTest() {
//        //specify which players need to be converted
//        List<Integer> range = IntStream.rangeClosed(0, players.size() - 2).boxed().collect(Collectors.toList());
//        //convert specified players to followers
//        convertPlayers(range);
//        //kill the cult leader
//        gameInstance.killPlayer(playerCultLeader);
//        assertNull(createNewInstance().checkWin(gameInstance), "not every player in the game is a follower");
//        //kill the last unconverted player
//        gameInstance.killPlayer(players.get(players.size() - 1));
//        assertEquals(Group.CULT, createNewInstance().checkWin(gameInstance), "The cult group should have won!");
//    }
//
//    /** Convert evert player except for one, then kill every player in the cult and leave the unconverted player alive,
//     * after this the cult should not win.
//     */
//    @Disabled
//    @Test
//    public void DeadCultWinTest() {
//        //specify which players need to be converted
//        List<Integer> range = IntStream.rangeClosed(0, players.size() - 2).boxed().collect(Collectors.toList());
//        //convert specified players to followers
//        convertPlayers(range);
//        assertNull(createNewInstance().checkWin(gameInstance), "not every player in the game is a follower");
//        //kill the cult leader and the followers
//        gameInstance.killPlayer(playerCultLeader);
//        for (int i = 0; i < players.size() - 2; i++) {
//            gameInstance.killPlayer(players.get(i));
//        }
//        //only the unconverted player is left alive
//        assertNull(createNewInstance().checkWin(gameInstance));
//    }
//
//    @Disabled
//    @Test
//    public void MultipleCultsWinTest() {
//        //TODO: make sure multiple cults are seperated from each other
//    }
