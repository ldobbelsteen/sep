package org.lukos.model.winhandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.roles.mainroles.*;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code WinWolves}.
 *
 * @author Rick van der Heijden (1461923)
 * @author Sander van Heesch (1436708)
 * @since 07-03-2022
 */
public class WinWolvesTest extends WinHandlerTest {
    private final List<Player> players = new ArrayList<>();
    private Player werewolfPlayer, framerPlayer, cleanerPlayer, elderPlayer;
    private IInstance gameInstance;

    @BeforeEach
    public void beforeTest() {
        try {
            // Get instance manager
            InstanceManager im = InstanceManager.getInstanceManager();
            String issuer = this.getClass().getName();
            IssuerSub werewolfIS = new IssuerSub(issuer, "1");
            User werewolfUser = UserManager.getInstance().createUser(werewolfIS, "Werewolf");

            //Create a new game
            werewolfUser.createGame("WinWolvesTest", 1);
            gameInstance = im.getInstance(werewolfUser.getPlayer().getPlayerIdentifier().instanceID());

            IssuerSub framerIS = new IssuerSub(issuer, "2");
            User framerUser = UserManager.getInstance().createUser(framerIS, "Framer");
            framerUser.joinGame(gameInstance.getIid());

            IssuerSub cleanerIS = new IssuerSub(issuer, "3");
            User cleanerUser = UserManager.getInstance().createUser(cleanerIS, "Cleaner");
            cleanerUser.joinGame(gameInstance.getIid());

            IssuerSub elderIS = new IssuerSub(issuer, "4");
            User elderUser = UserManager.getInstance().createUser(elderIS, "Elder");
            elderUser.joinGame(gameInstance.getIid());

            werewolfPlayer = werewolfUser.getPlayer();
            framerPlayer = framerUser.getPlayer();
            cleanerPlayer = cleanerUser.getPlayer();
            elderPlayer = elderUser.getPlayer();

            //Create the players
            for (int i = 0; i < 7; i++) {
                IssuerSub isSub = new IssuerSub(issuer, String.valueOf((i + 10)));
                User user = UserManager.getInstance().createUser(isSub, "User" + i);
                user.joinGame(gameInstance.getIid());
                Player player = user.getPlayer();
                players.add(player);
            }

            //Start new game
            try {
                gameInstance.startGame(werewolfUser.getUid());
            } catch (Exception e) {
                fail("An exception was thrown" + e);
            }

            for (Player player : players) {
                player.setMainRole(new Townsperson());
            }

            werewolfPlayer.setMainRole(new Werewolf());
            framerPlayer.setMainRole(new WerewolfFramer());
            cleanerPlayer.setMainRole(new WerewolfCleaner());
            elderPlayer.setMainRole(new WerewolfElder());

            assertNull(createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "No one should have won, because the game just started");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have thrown an error.");
        }
    }

    @Override
    protected WinHandler createNewInstance() {
        return new WinWolves();
    }

    @Override
    protected WinHandler createNewInstance(WinHandler next) {
        return new WinWolves(next);
    }

    /**
     * Tests the constructor without parameters
     *
     * @utp.description Tests whether the constructor with no parameters behaves as intended.
     */
    @Test
    public void testEmptyConstructor() {
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.WEREWOLVES, instance.getGroup(), "Group should be werewolves.");
        assertNull(instance.getNext(), "Next should be null.");
    }

    /**
     * Tests the single parameter constructor
     *
     * @utp.description Tests whether the constructor with 1 parameter behaves as intended.
     */
    @Test
    public void testConstructor() {
        WinHandler instance = new WinWolves(this.instance);
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.WEREWOLVES, instance.getGroup(), "Group should be werewolves.");
        assertNotNull(instance.getNext(), "Next should not be null.");
        assertEquals(this.instance, instance.getNext(), "Instance should have this.instance as next.");
    }

    /**
     * All players in a game are part of the werewolves group, the werewolves group should win
     *
     * @utp.description Tests whether if we only have wolves the winhandler returns correctly that the wolves have won.
     */
    @Test
    public void WerewolvesWinTest1() {
        try {
            //kill all players not in the werewolves group
            for (Player player : players) {
                gameInstance.killPlayer(player);
            }
            assertEquals(Group.WEREWOLVES, createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "The werewolves group should have won!");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not throw an error.");
        }
    }

    /**
     * All players in a game are part of the werewolves group, the werewolves group should win
     *
     * @utp.description Tests whether if we kill all non wolves players that the winhandler then returns correctly that
     * the wolves won.
     */
    @Test
    public void WerewolvesWinTest2() {
        try {
            //kill some werewolves group players, but not all
            gameInstance.killPlayer(werewolfPlayer);
            gameInstance.killPlayer(elderPlayer);
            gameInstance.killPlayer(cleanerPlayer);
            assertNull(createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "There are still players left in the game that are not part of the werewolves group");

            //kill all players not in the werewolves group
            for (Player player : players) {
                gameInstance.killPlayer(player);
            }
            assertEquals(Group.WEREWOLVES, createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "The werewolves group should have won!");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not throw an error.");
        }
    }

    /**
     * There are no more players in a game that are part of the werewolves group, the werewolves group should not win.
     *
     * @utp.description Tests whether if there are no wolves over, the winhandler will not return a winner.
     */
    @Test
    public void DeadWerewolvesWinTest() {
        try {
            //kill the werewolves group players
            gameInstance.killPlayer(werewolfPlayer);
            gameInstance.killPlayer(elderPlayer);
            gameInstance.killPlayer(cleanerPlayer);
            gameInstance.killPlayer(framerPlayer);
            assertNull(createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "There are no werewolves players left");

            //kill all players not in the werewolves group
            for (int i = 0; i < players.size() - 1; i++) {
                gameInstance.killPlayer(players.get(i));
            }
            assertNull(createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "There are no werewolves players left");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not throw an error.");
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
