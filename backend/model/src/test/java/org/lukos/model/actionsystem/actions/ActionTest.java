package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.BeforeEach;
import org.lukos.database.UserDB;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.user.NoSuchRoleException;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.user.User;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contains some code used by all {@code Action} Tests.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 15-04-2022
 */
public class ActionTest extends GameTest {

    /** Instance id */
    protected int instanceId;

    /** ID of the game master (user1) */
    protected int gameMasterId;

    /** First player */
    protected Player player;

    /** Second player */
    protected int user2Id;
    protected Player secondPlayer;

    /** Third player */
    protected int user3Id;
    protected Player thirdPlayer;

    @BeforeEach
    public void createGame() {
        try {
            // Get the instance manager
            InstanceManager im = InstanceManager.getInstanceManager();

            // Create 3 users
            gameMasterId = UserDB.createUser("is1", "sub1", "User1");
            user2Id = UserDB.createUser("is2", "sub2", "User2");
            user3Id = UserDB.createUser("is3", "sub3", "User3");

            // Create a new game and get the player
            instanceId = (new User(gameMasterId)).createGame("PlayerTestGame", 1);
            player = (new User(gameMasterId)).getPlayer();

            // Test that player was initialized correctly
            assertEquals(instanceId, player.getPlayerIdentifier().instanceID(), "instanceId mismatch!");
            assertEquals(gameMasterId, player.getPlayerIdentifier().userID(), "uid should be the id of the 'gamemaster'!");
            assertNotNull(player.getDoubleRoles(), "DoubleRoles should not be null!");
            assertEquals(new ArrayList<DoubleRole>(), player.getDoubleRoles(), "DoubleRoles should be empty!");
            assertNotNull(player.getDeathnote(), "Deathnote should not be null!");
            assertNull(player.getDeathnote().getContent(), "Deathnote should be empty at creation!");
            assertTrue(player.getDeathnote().getChangeable(), "Deathnote should be changeable at creation!");
            assertNotNull(player.getJobs(), "Jobs should not be null!");
            assertEquals(new ArrayList<Job>(), player.getJobs(), "Jobs should be empty!");
            try {
                player.getMainRole();
            } catch (NoSuchRoleException e) {
                assertNotNull(e.getMessage(), "Exception message should not be null!");
            } catch (Exception e) {
                fail("NoSuchRoleException should have been thrown. Instead the following exception was thrown: " + e);
            }

            // Create enough players and tart the game
            // Second player joins the game
            (new User(user2Id)).joinGame(instanceId);
            secondPlayer = (new User(user2Id)).getPlayer();

            // Create third player
            (new User(user3Id)).joinGame(instanceId);
            thirdPlayer = (new User(user3Id)).getPlayer();

            // Add 9 more dummy players to reach minimum number of player
            int userId;
            for (int i = 0; i < 9; i++) {
                userId = UserDB.createUser("is" + (i + 4), "sub" + (i + 4), "User" + (i + 4));
                (new User(userId)).joinGame(instanceId);
            }

            // Start the game
            InstanceManager.getInstanceManager().getInstance(instanceId).startGame(gameMasterId);

        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * Get the username of a player.
     *
     * @param player the player
     * @return the username
     * @throws SQLException database error
     */
    protected String getUsername(Player player) throws SQLException {
        return (new User(player.getPlayerIdentifier().userID())).getUsername();
    }
}
