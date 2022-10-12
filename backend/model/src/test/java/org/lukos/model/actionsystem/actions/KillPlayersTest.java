package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.ActionMessagesDB;
import org.lukos.database.InstanceDB;
import org.lukos.database.LocationDB;
import org.lukos.database.PlayerDB;
import org.lukos.model.actionsystem.ActionDT;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.ActionManager;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.location.Bridge;
import org.lukos.model.location.House;
import org.lukos.model.location.Location;
import org.lukos.model.rolesystem.jobs.AlphaWolf;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link KillPlayers}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class KillPlayersTest extends ActionTest {

    @BeforeEach
    public void beforeEach() {
        try {
            // Make sure all (relevant) players are alive
            assertTrue(player.alive(), "player should be alive!");
            assertTrue(secondPlayer.alive(), "secondPlayer should be alive!");
            assertTrue(thirdPlayer.alive(), "thirdPlayer should be alive!");
        } catch (Exception e) {
            fail("An exception was thrown in beforeEach: " + e);
        }
    }

    /**
     * Creates an {@code ActionDT} for {@code KillPlayers} {@code Action}.
     *
     * @param destination the id of the destination {@code Location} where everyone must be killed.
     * @param players the {@code Players} who will die.
     * @return The created {@code ActionDT}.
     */
    private ActionDT createActionDT(List<Integer> destination, List<PlayerIdentifier> players) {
        ActionEnc actionEnc = new ActionEnc(destination, players);
        PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);
        return new ActionDT(Instant.now(), new KillPlayers(), preActionDT);
    }

    /**
     * Most execute test use this code as the test, with different parameters.
     *
     * @param destination the id of the destination {@code Location} where everyone must be killed.
     * @param players the {@code Players} who will die.
     */
    private void testKillPlayers(List<Integer> destination, List<PlayerIdentifier> players, List<PlayerIdentifier> protectedPlayers) {
        try {
            // Add killPlayers action
            ActionManager.addAction(createActionDT(destination, players));

            // Perform action
            ActionManager.performActions(instanceId);
            ActionMessagesDB.unlockMessages(instanceId);

            if (!destination.isEmpty()) {
                // Test what location we have
                Location location;
                int dest = destination.get(0);
                if (LocationDB.bridgeExists(dest)) {
                    location = new Bridge(dest);
                } else {
                    location = new House(dest);
                }
                players.addAll(location.getPlayersAtLocation());
                players.remove(player.getPlayerIdentifier());
            }

            List<PlayerIdentifier> markedPlayers = InstanceDB.getToBeExecuted(instanceId);

            // All target players should be dead
            for (PlayerIdentifier playerId: players) {
                if (protectedPlayers.contains(playerId)) {
                    assertFalse(markedPlayers.contains(playerId), "Player should not be marked, they are protected:  instance-" + playerId.instanceID() + ", user-" + playerId.userID());
                } else {
                    assertTrue(markedPlayers.contains(playerId), "Player should be marked:  instance-" + playerId.instanceID() + ", user-" + playerId.userID());
                }
            }

        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code KillPlayers} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("KillPlayers", new KillPlayers().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * execute test, have one player target.
     *
     * @utp.description Test whether one {@code Player} is marked for death when there is exactly one {@code Player} target.
     */
    @Test
    public void onePlayerTargetTest() {
        testKillPlayers(new ArrayList<>(),
                        new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier())),
                        new ArrayList<>());
    }

    /**
     * execute test, have multiple player targets.
     *
     * @utp.description Test whether multiple {@code Players} are marked for death when there are multiple {@code Player} targets.
     */
    @Test
    public void multiplePlayerTargetTest() {
        try {
            ArrayList<PlayerIdentifier> players = new ArrayList<>(InstanceDB.getAlivePlayers(instanceId));
            players.remove(player.getPlayerIdentifier());

            testKillPlayers(new ArrayList<>(), players, new ArrayList<>());
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * execute test, one location with one {@code Player} at location.
     *
     * @utp.description Test whether one {@code Player} is marked for death when there is exactly one {@code Location} target.
     */
    @Test
    public void oneLocationTargetTest() {
        try {
            testKillPlayers(new ArrayList<>(Collections.singleton(secondPlayer.getHouse())), new ArrayList<>(), new ArrayList<>());
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * execute test, one location with multiple {@code Players} at location.
     *
     * @utp.description Test whether multiple {@code Players} is marked for death when there is exactly one {@code Location} target.
     */
    @Test
    public void oneLocationMultiplePlayersTargetTest() {
        try {
            InstanceManager.getInstanceManager().getInstance(instanceId).movePlayer(thirdPlayer, new House(secondPlayer.getHouse()));
            testKillPlayers(new ArrayList<>(Collections.singleton(secondPlayer.getHouse())), new ArrayList<>(), new ArrayList<>());
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * execute test, have multiple player targets, but one player is protected.
     *
     * @utp.description Test whether a {@code Player} is not marked if they are protected.
     */
    @Test
    public void protectedPlayerTest() {
        try {
            PlayerDB.updateProtected(secondPlayer.getPlayerIdentifier(), true);
            ArrayList<PlayerIdentifier> players = new ArrayList<>(InstanceDB.getAlivePlayers(instanceId));
            players.remove(player.getPlayerIdentifier());

            testKillPlayers(new ArrayList<>(), players, new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier())));
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * execute test, werewolves should not be marked if the killPlayer is initiated by the alpha wolf.
     *
     * @utp.description Test whether a {@code Player} is not marked if they are a {@code Werewolf} and the action is initiated by the {@code AlphaWolf}.
     */
    @Test
    public void alphaWolfKillTest() {
        try {
            // Add alpha wolf and werewolf
            player.setMainRole(new Werewolf());
            player.addJob(new AlphaWolf());

            secondPlayer.setMainRole(new Werewolf());
            List<PlayerIdentifier> protectedPlayers = new ArrayList<>();

            // Add all werewolves to protected list
            for (PlayerIdentifier pid: InstanceDB.getPlayers(instanceId)) {
                if (new Player(pid).getMainRole().getClass() == Werewolf.class) {
                    protectedPlayers.add(pid);
                }
            }

            ArrayList<PlayerIdentifier> players = new ArrayList<>(InstanceDB.getAlivePlayers(instanceId));
            players.remove(player.getPlayerIdentifier());

            testKillPlayers(new ArrayList<>(), players, protectedPlayers);
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

}