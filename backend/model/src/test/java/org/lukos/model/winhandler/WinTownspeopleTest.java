package org.lukos.model.winhandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.roles.mainroles.Townsperson;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code WinTownspeople}.
 *
 * @author Rick van der Heijden (1461923)
 * @author Sander van Heesch (1436708)
 * @since 07-03-2022
 */
public class WinTownspeopleTest extends WinHandlerTest {

    private final List<Player> players = new ArrayList<>();
    private Player varietyPlayer;
    private IInstance gameInstance;

    @BeforeEach
    public void beforeTest() {
        try {
            // Get instance manager
            InstanceManager im = InstanceManager.getInstanceManager();
            String issuer = this.getClass().getName();
            IssuerSub gameMasterIS = new IssuerSub(issuer, "1");
            User gameMasterUser = UserManager.getInstance().createUser(gameMasterIS, "Werewolf");

            //Create a new game
            gameMasterUser.createGame("WinTownspeople", 1);
            gameInstance = im.getInstance(gameMasterUser.getPlayer().getPlayerIdentifier().instanceID());

            IssuerSub varietyIS = new IssuerSub(issuer, "2");
            User varietyUser = UserManager.getInstance().createUser(varietyIS, "Framer");
            varietyUser.joinGame(gameInstance.getIid());
            this.varietyPlayer = varietyUser.getPlayer();

            //Create the players
            for (int i = 0; i < 11; i++) {
                IssuerSub isSub = new IssuerSub(issuer, String.valueOf((i + 10)));
                User user = UserManager.getInstance().createUser(isSub, "User" + i);
                user.joinGame(gameInstance.getIid());
                Player player = user.getPlayer();
                players.add(player);
            }

            //Start new game
            try {
                gameInstance.startGame(gameMasterUser.getUid());
            } catch (Exception e) {
                fail("An exception was thrown" + e);
            }

            for (Player player : players) {
                player.setMainRole(new Townsperson());
            }

            varietyPlayer.setMainRole(new Werewolf());

            assertNull(createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "No one should have won, because not all players are Townspeople");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not throw an error.");
        }
    }

    @Override
    protected WinHandler createNewInstance() {
        return new WinTownspeople();
    }

    @Override
    protected WinHandler createNewInstance(WinHandler next) {
        return new WinTownspeople(next);
    }

    /**
     * Tests the constructor without parameters
     *
     * @utp.description Tests whether the constructor with no parameters behaves as intended.
     */
    @Test
    public void testEmptyConstructor() {
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.TOWNSPEOPLE, instance.getGroup(), "Group should be townspeople.");
        assertNull(instance.getNext(), "Next should be null.");
    }

    /**
     * Tests the multi parameter constructor
     *
     * @utp.description Tests whether the constructor with multiple parameters behaves as intended.
     */
    @Test
    public void testConstructor() {
        WinHandler instance = new WinTownspeople(this.instance);
        assertNotNull(instance.getGroup(), "Group should not be null.");
        assertEquals(Group.TOWNSPEOPLE, instance.getGroup(), "Group should be townspeople.");
        assertNotNull(instance.getNext(), "Next should not be null.");
        assertEquals(this.instance, instance.getNext(), "Instance should have this.instance as next.");
    }

    /**
     * All players in a game are townspeople, the townspeople group should win
     *
     * @utp.description Tests whether if we only have townspeople the winhandler returns correctly that the townspeople
     * have won.
     */
    @Test
    public void TownspeopleWinTest1() {
        try {
            //set the role of the variety player to townsperson
            varietyPlayer.setMainRole(new Townsperson());
            //now all of the players are townspeople
            assertEquals(Group.TOWNSPEOPLE, createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "The townspeople group should have won!");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not throw an error.");
        }
    }

    /**
     * After killing all players that are not part of the townspeople group, the townspeople group should win
     *
     * @utp.description Tests whether if we kill all non townspeople players that the winhandler then returns correctly
     * that the townspeople won.
     */
    @Test
    public void TownspeopleWinTest2() {
        try {
            //set the role of the variety player to a role that is not in the townspeople group
            varietyPlayer.setMainRole(new Werewolf());
            assertNull(createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "Not all players in the game are Townspeople");

            //kill the variety player to make all the remaining players in a game townspeople
            gameInstance.killPlayer(varietyPlayer);
            //now all the players are townspeople
            assertEquals(Group.TOWNSPEOPLE, createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "The townspeople group should have won!");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not throw an error.");
        }
    }

    /**
     * All townspeople are dead, the townspeople should not win
     *
     * @utp.description Tests whether if there are no townspeople over, the winhandler will not return a winner.
     */
    @Test
    public void DeadTownspeopleWinTest() {
        try {
            //set the role of the variety player to a role that is not in the townspeople group
            varietyPlayer.setMainRole(new Werewolf());
            assertNull(createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "Not all players in the game are townspeople");

            //kill the townspeople
            for (int i = 0; i < players.size() - 1; i++) {
                gameInstance.killPlayer(players.get(i));
            }
            assertNull(createNewInstance().checkWin(gameInstance.alivePlayers()),
                    "The only player left is not a townsperson");
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
