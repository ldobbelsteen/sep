package org.lukos.model.instances;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.lukos.database.ActionsDB;
import org.lukos.database.PlayerDB;
import org.lukos.database.UserDB;
import org.lukos.database.util.LocationHelper;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.exceptions.instances.NotEnoughPlayersException;
import org.lukos.model.exceptions.instances.NotEnoughRolesException;
import org.lukos.model.exceptions.instances.TooManyPlayersException;
import org.lukos.model.exceptions.location.NoSuchLocationException;
import org.lukos.model.exceptions.user.AlreadyInGameException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.exceptions.voting.NoSuchVoteException;
import org.lukos.model.exceptions.voting.VotingException;
import org.lukos.model.instances.util.GeneralInstanceHelper;
import org.lukos.model.location.Bridge;
import org.lukos.model.location.House;
import org.lukos.model.location.Location;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.jobs.AlphaWolf;
import org.lukos.model.rolesystem.jobs.Mayor;
import org.lukos.model.rolesystem.roles.MainRoleCategories;
import org.lukos.model.rolesystem.roles.MainRoleList;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;
import org.lukos.model.voting.Vote;
import org.lukos.model.voting.VoteType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code Instance}.
 *
 * @author Martijn van Andel (1251104)
 * @since 22-02-2022
 */
public class InstanceTest extends GameTest {

    /** The creator of our test instances */
    static int gamemasterUserId;
    Instance i;

    /** A number >= MIN_PLAYERS in instance */
    private final int ENOUGH_PLAYERS = 3;

    /**
     * Before each test, an Instance is initialized.
     */
    @BeforeEach
    public void init() {
        try {
            // First, create a user A to be the gamemaster
            int userIdA = UserDB.createUser("InstanceTest", "testUserA",
                    String.valueOf(UserCounter.getInstance().getCounter()));

            // Make user A create a game
            User userA = new User(userIdA);
            int instanceId = userA.createGame("InstanceTest", 1);

            for (int i = 2; i <= 12; i++) {
                String currentCounter = String.valueOf(UserCounter.getInstance().getCounter());
                int userId = UserDB.createUser("InstanceTest", "testUser" + currentCounter, currentCounter);
                User user = new User(userId);
                user.joinGame(instanceId);
            }

            gamemasterUserId = userIdA;
            i = new Instance(instanceId, gamemasterUserId);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether an instance is created.
     */
    @Test
    public void constructorTest1() {
        assertNotNull(i);
    }

    /**
     * @utp.description Tests whether the game master is properly assigned.
     */
    @Test
    public void constructorTest2() {
        try {
            assertEquals(gamemasterUserId, i.getGameMaster());
        } catch (SQLException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests whether a player is added to the player list.
     */
    @Test
    public void addPlayerTest() {
        try {
            // retrieve a unique counter value, so that we can create a unique user
            String currentCounter = String.valueOf(UserCounter.getInstance().getCounter());

            // create a new user, and retrieve its ID
            int userId = UserDB.createUser("InstanceTest", "testUser" + currentCounter, currentCounter);

            // create a User object to hold the newly created user
            User user = new User(userId);

            // Make the user join the game
            try {
                user.joinGame(i.getIid());
            } catch (AlreadyInGameException e) {
                e.printStackTrace();
                fail("Newly created user should not be in game, but AlreadyInGameException was thrown.");
            }

            assertTrue(i.getPlayerList().stream().anyMatch(p -> p.getPlayerIdentifier().userID() == user.getUid()),
                    "User's player has not been found in the player list.");
        } catch (SQLException | GameException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests whether trying to kick a player without permission is handled properly.
     */
    @Test
    public void kickPlayerTest1() {
        try {
            Player q = i.getPlayerList().get(1);

            // Unauthorized person calls kickPlayer, expecting exception
            //i.addPlayer(q);
            i.kickPlayer(1234567, q);
            fail("Should have thrown an exception.");
        } catch (NoPermissionException e) {
            assertTrue(true);
        } catch (SQLException | GameException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a player is removed from the player list.
     */
    @Test
    public void kickPlayerTest3() {
        try {
            Player p = i.getPlayerList().get(2);
            //i.addPlayer(p);
            i.kickPlayer(gamemasterUserId, p);
            assertFalse(i.getPlayerList().contains(p));
        } catch (SQLException | GameException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }


    /**
     * @utp.description Tests whether a player is successfully killed.
     */
    @Test
    public void killPlayerTest() {
        try {
            startGame();

            // Get a player from the game
            Player p = i.getPlayerList().get(0);

            i.killPlayer(p);
            assertFalse(i.getInstanceState().getAlive().contains(p.getPlayerIdentifier()));
        } catch (SQLException | GameException | ReflectiveOperationException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * @utp.description Tests whether trying to kill a non-existing player is properly handled.
     */
    @Test
    public void killNonExistingPlayerTest() {

        try {
            startGame();

            // Get a player from the game
            Player p = new Player(12345678, 1234567);
            i.killPlayer(p);
            fail("Should have thrown exception");
        } catch (NoSuchPlayerException e) {
            assertTrue(true);
        } catch (SQLException | GameException | ReflectiveOperationException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a player is successfully revived.
     */
    @Test
    public void revivePlayerTest() {
        try {
            startGame();

            // Get a player from the game
            Player p = i.getPlayerList().get(0);

            i.killPlayer(p);

            // Assert player is indeed killed, just to be sure
            assertFalse(i.getInstanceState().getAlive().contains(p.getPlayerIdentifier()));

            i.revivePlayer(p);
            assertTrue(i.getInstanceState().getAlive().contains(p.getPlayerIdentifier()));
        } catch (NoSuchPlayerException e) {
            fail("Unexpected NoSuchPlayerException thrown");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether trying to revive a non-existing player is properly handled.
     */
    @Test
    public void reviveNonExistingPlayerTest() {
        try {
            startGame();

            // Get a player from the game
            Player p = new Player(12345678, 1234567);

            i.revivePlayer(p);
            fail("Should have thrown NoSuchPlayerException");
        } catch (NoSuchPlayerException e) {
            assertEquals("Player does not exist", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether trying to revive an already alive player is properly handled.
     */
    @Test
    public void reviveAlivePlayerTest() {
        try {

            startGame();

            // Get a player from the game
            Player p = i.getPlayerList().get(0);

            i.revivePlayer(p);
            fail("Should have thrown NoSuchPlayerException");
        } catch (NoSuchPlayerException e) {
            assertEquals("Player is already alive", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a game can be started without any problems.
     */
    @Test
    public void startGameTest() {
        try {
            i.startGame(gamemasterUserId);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a game does not start when there are not enough players.
     */
    @Test
    public void startGameTestNotEnoughPlayers() {
        try {
            User user = UserManager.getInstance().createUser((new IssuerSub("InstanceTest", "sub")), "testUserA");
            int instanceID = user.createGame("Small instance", user.getUid());
            IInstance instance = InstanceManager.getInstanceManager().getInstance(instanceID);
            instance.startGame(user.getUid());

            fail("Should have thrown exception");
        } catch (NotEnoughPlayersException e) {
            assertTrue(true);
        } catch (NoPermissionException e) {
            fail("Unexpected exception thrown. NoPermissionException");
        } catch (TooManyPlayersException e) {
            fail("Unexpected exception thrown: TooManyPlayersException.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a game does not start when there are too many players.
     */
    @Test
    public void startGameTestTooManyPlayers() {
        try {
            // Add too many players
            for (int j = 2; j <= 40; j++) {
                String currentCounter = String.valueOf(UserCounter.getInstance().getCounter());
                int userId = UserDB.createUser("InstanceTest", "testUser" + currentCounter, currentCounter);
                User user = new User(userId);
                user.joinGame(i.getIid());
            }
            i.startGame(gamemasterUserId);
            fail("Should have thrown exception");
        } catch (TooManyPlayersException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a game is not started if startGame() is called by someone other than the game
     * master.
     */
    @Test
    public void startGameNoPermissionTest() {
        try {

            i.startGame(gamemasterUserId + 1);

            fail("Should have thrown exception.");
        } catch (NoPermissionException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether null is returned when there is no instanceState yet.
     */
    @Test
    public void getNonExistingInstanceStateTest() {
        try {
            assertNull(i.getInstanceState());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the InstanceState is retrieved correctly.
     */
    @Test
    public void getInstanceStateTestCorrectUsage() {
        try {
            startGame();

            assertNotNull(i.getInstanceState());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a vote is started correctly.
     */
    @Test
    public void startVoteTest() {
        try {
            // Start game
            startGame();
            i.getInstanceState().setDay(1);

            // Lynch vote
            Vote v = i.startVote(VoteType.LYNCH);
            assertTrue(i.getOngoingVotes().contains(v));

            // Mayor vote
            Vote v2 = i.startVote(VoteType.MAYOR);
            assertTrue(i.getOngoingVotes().contains(v2));

            // Alpha wolf vote
            Vote v3 = i.startVote(VoteType.ALPHA_WOLF);
            assertTrue(i.getOngoingVotes().contains(v3));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a vote is properly ended.
     */
    @Test
    public void endVoteTest() {
        try {
            startGame();
            i.getInstanceState().setDay(1);

            Vote v = i.startVote(VoteType.MAYOR);
            // End vote
            assertTrue(i.getOngoingVotes().contains(v));
            try {
                i.endVote(VoteType.MAYOR);
                assertFalse(i.getOngoingVotes().contains(v));
            } catch (NoSuchVoteException e) {
                fail("Unexpected NoSuchVoteException thrown.");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a Mayor vote result is executed correctly.
     */
    @Test
    public void processMayorVoteTest() {
        try {
            // Have a voting result (Map<Player, Integer>)
            Map<PlayerIdentifier, Integer> voteResults = prepareVoteResults();

            VoteType voteType = VoteType.MAYOR;
            i.processVote(voteResults, voteType);

            // Check if MAYOR job is added to one player only
            assertEquals(1, ActionsDB.getNotExecutedActions(i.getIid()).size());
        } catch (Exception e) {
            fail("Unexpected exception thrown: "+ e);
        }
    }

    /**
     * Tests whether an Alpha Wolf vote result is executed correctly.
     * This test covers the case where all werewolves have voted for the same player to be alpha wolf.
     * @utp.description Tests whether the results of an Alpha Wolf vote with unanimity are processed correctly.
     */
    @Test
    public void processAlphaWolfVoteUnanimousTest() {
        try {
            startGame();
            i.getInstanceState().setDay(1);
            // Get a list of werewolves
            List<Player> werewolves = i.getPlayerList().stream().filter(p -> {
                try {
                    return p.getMainRole().getGroup() == Group.WEREWOLVES;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }).toList();
            // Have a voting result (Map<Player, Integer>)
            Map<PlayerIdentifier, Integer> voteResults = prepareVoteResults(werewolves);
            i.processVote(voteResults, VoteType.ALPHA_WOLF);

            assertEquals(1, (new ArrayList<>(i.getPlayerList()).stream().filter(
                    p -> {
                        try {
                            return p.getJobs().stream().anyMatch(job -> job instanceof AlphaWolf);
                        } catch (SQLException | ReflectiveOperationException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })).toList().size());
        } catch (Exception e) {
            fail("Unexpected exception thrown: "+ e);
        }
    }

    /**
     * @utp.description Tests whether a Lynch vote result is executed correctly.
     */
    @Test
    public void processLynchVoteTest() {
        try {
            startGame();
            i.getInstanceState().setDay(1);
            // Have a voting result (Map<Player, Integer>)
            Map<PlayerIdentifier, Integer> voteResults = prepareVoteResults();

            VoteType voteType = VoteType.LYNCH;
            i.processVote(voteResults, voteType);

            assertEquals(1, ActionsDB.getNotExecutedActions(i.getIid()).size(), "Only one player must be executed.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: "+ e);
        }
    }

    /**
     * @utp.description Tests whether a Lynch vote result is executed correctly when the gameSpeed has increased.
     */
    @Test
    public void processLynchVoteSpeedTest() {
        try {
            startGame();
            // Have a voting result (Map<Player, Integer>)
            Map<PlayerIdentifier, Integer> voteResults = prepareVoteResults();

            // 2 will be executed
            i.getInstanceState().setDay(5);
            VoteType voteType = VoteType.LYNCH;
            i.processVote(voteResults, voteType);

            assertEquals(1, ActionsDB.getNotExecutedActions(i.getIid()).size(), "Only one player must be executed.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: "+ e);
        }
    }

    /**
     * @utp.description Tests whether a Lynch vote result is executed correctly when the gameSpeed has increased.
     */
    @Test
    public void processLynchVoteMayorTest() {
        try {
            startGame();
            // Have a voting result (Map<Player, Integer>)
            Map<PlayerIdentifier, Integer> voteResults = prepareVoteResults(5, 2, 2);

            // 2 will be executed
            i.getInstanceState().setDay(5);
            VoteType voteType = VoteType.LYNCH;

            i.processVote(voteResults, voteType);

            assertEquals(1, ActionsDB.getNotExecutedActions(i.getIid()).size(), "Only one player must be executed. The other spot must be left empty.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: "+ e);
        }
    }

    /**
     * @utp.description Tests whether roles are properly selected from the MainRoleList, based on their category.
     */
    @Test
    public void roleLUTTest() {
        // For each role in MainRoleList, check if getRole returns expected role.
        List<MainRole> returnedMainRoles = new ArrayList<>();

        // Collect each MainRole that is returned.
        for (int category = 0; category < MainRoleCategories.values().length; category++) {
            if (category == 9) {
                // Skip CHILISAUS_TOT
                continue;
            }
            for (int j = 0; j < MainRoleCategories.values()[category].groupSize; j++) {
                returnedMainRoles.add(GeneralInstanceHelper.rolesLUT(category, j));
            }
        }

        // Make a list of all MainRoles
        List<MainRole> allMainRoles = new ArrayList<>();
        for (MainRoleList mainRole : MainRoleList.values()) {
            allMainRoles.add(mainRole.role);
        }

        // Compare the returnedRoles with all MainRoles
        assertEquals(allMainRoles.size(), returnedMainRoles.size(),
                "The number of roles returned must be the same as there are MainRoles");

        for (int role = 0; role < allMainRoles.size(); role++) {
            assertSame(allMainRoles.get(role).getClass(), returnedMainRoles.get(role).getClass(),
                    "returned role at index "+role+" is not "+allMainRoles.get(role).getClass().toString());
        }
    }

    /**
     * @utp.description Tests whether every player has a role.
     */
    @Test
    public void assignRolesTest() {
        try {
            startGame();

            for (Player p : i.getPlayerList()) {
                assertNotNull(p.getMainRole());
            }
        } catch (NotEnoughRolesException e) {
            assertTrue(true, "FILE NOT FOUND");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the phase is shifted correctly.
     */
    @Test
    public void nextPhaseTest() {
        try {

            startGame();

            i.getInstanceState().setPhase(DayPhase.values()[0]);

            for (DayPhase phase : DayPhase.values()) {
                try {
                    i.nextPhase();
                    assertEquals(phase.next(), i.getInstanceState().getPhase(), "Run: " + phase);
                } catch (VotingException | NoSuchUserException e) {
                    fail("Unexpected exception thrown: " + e);
                }
            }
        } catch (SQLException | GameException | ReflectiveOperationException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a vote is created and ended based on the phase change.
     */
    @Test
    public void nextPhaseVoteTest() {
        try {

            startGame();

            i.getInstanceState().setPhase(DayPhase.DAY);
            i.getInstanceState().setDay(1);

            i.nextPhase();
            List<Vote> v = i.getOngoingVotes().stream().filter(x -> {
                try {
                    return x.getVoteType() == VoteType.LYNCH;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            }).toList();
            System.out.println("nextPhaseVoteTest() - ongoing votes: ");
            i.getOngoingVotes().forEach(vote -> {
                try {
                    System.out.println(vote.getVoteType());
                } catch (SQLException e) {
                    System.out.println("No votetype found. Throwing SQLException.");
                    e.printStackTrace();
                }
            });
            assertTrue(v.size() > 0);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether a game is correctly identified as 'started'.
     */
    @Test
    public void isStartedTest() {
        try {

            assertFalse(i.isStarted());

            startGame();
            assertTrue(i.isStarted());
        } catch (SQLException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether all locations are returned.
     */
    @Test
    public void getLocationsTest() {
        try {

            startGame();

            List<Location> allLocations = new ArrayList<>();
            for (int locID : i.getLocations()) {
                allLocations.add(LocationHelper.getLocationByID(locID));
            }

            int houses = 0;
            int bridges = 0;
            for (Location l : allLocations) {
                if (l instanceof House) {
                    houses++;
                } else if (l instanceof Bridge) {
                    bridges++;
                }
            }
            assertEquals(i.getPlayerList().size(), houses);
            assertEquals(2, bridges);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }


    /**
     * @utp.description Tests whether trying to move a player to a non-existing location is handled properly.
     */
    @Test
    public void moveNonExistingPlayerTest() {

        startGame();

        // Try movePlayer(), expect NoSuchLocationException
        try {
            i.movePlayer(new Player(12345678, 1234567890), LocationHelper.getLocationByID(i.getBridges().get(0)));
            fail("Should have thrown NoSuchLocationException.");
        } catch (NoSuchPlayerException e) {
            assertTrue(true);
        } catch (NoSuchLocationException e) {
            fail("Unexpected NoSuchLocationException thrown.");
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
        }
    }

    /**
     * @utp.description Tests whether trying to move a player to a non-existing location is handled properly.
     */
    @Test
    public void movePlayerToNonExistingLocationTest() {
        startGame();

        // Try movePlayer(), expect NoSuchLocationException
        try {
            User user = UserManager.getInstance().createUser(new IssuerSub("MovePlayerTest", "Sub1"), "User1");
            int id = user.createGame("TestGame", 1);
            User user1 = UserManager.getInstance().createUser(new IssuerSub("MovePlayerTest", "Sub2"), "User2");
            user1.joinGame(id);
            IInstance instance = InstanceManager.getInstanceManager().getInstance(id);
            instance.startGame(user.getUid());
            Location house = LocationHelper.getLocationByID(user.getPlayer().getHouse());

            i.movePlayer(i.getPlayerList().get(0), house);
            fail("Should have thrown NoSuchLocationException.");
        } catch (NoSuchLocationException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
        }
    }


    /**
     * @utp.description Tests whether player locations are reset correctly.
     */
    @Test
    public void resetPlayerLocationTest() {
        try {

            startGame();
            List<Player> playerList = i.getPlayerList();
            // Move each player to another house, e.g. the neighbours house.
            for (int j = 0; j < ENOUGH_PLAYERS; j++) {
                i.movePlayer(playerList.get(j), new House(playerList.get((j + 1) % ENOUGH_PLAYERS).getHouse()));
            }

            i.resetPlayerLocation();
            // Check whether all players are back at their houses
            for (Player p : playerList) {
                assertTrue(PlayerDB.getPlayersAtLocation(p.getHouse()).contains(p.getPlayerIdentifier()));
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Runs for 10 seconds to test whether updatePhase() is periodically executed. (Be sure to replace
     * {@code TimeUnit.MINUTES} on Instance.class:109 to {@code TimeUnit.SECONDS}
     */
    @Test
    @Disabled
    public void updatePhaseTest() {

        startGame();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            assertTrue(true);
        }

    }

    /**
     * Starts the game. Assumes {@code startGameTest()} passed.
     */
    private void startGame() {
        try {
            i.startGame(gamemasterUserId);
        } catch (SQLException | GameException | ReflectiveOperationException e) {
            fail("Unexpected " + e + " thrown");
        }
    }

    /** Calls prepareVoteResults() using predefined values 5, 2 and 3. */
    private Map<PlayerIdentifier, Integer> prepareVoteResults() {
        return prepareVoteResults(5, 2, 3);
    }

    private Map<PlayerIdentifier, Integer> prepareVoteResults(int A, int B, int C) {
        return prepareVoteResults(A, B, C, null);
    }

    private Map<PlayerIdentifier, Integer> prepareVoteResults(List<Player> allowed) {
        return prepareVoteResults(3, 0, 0, allowed);
    }

    /** Creates a vote result using given vote counts A, B and C for player a, b and c, respectively. */
    private Map<PlayerIdentifier, Integer> prepareVoteResults(int A, int B, int C, List<Player> allowed) {
        try {
            List<Player> playerList;
            if (allowed != null) {
                playerList = allowed;
            } else {
                playerList = i.getPlayerList();
            }

            HashMap<PlayerIdentifier, Integer> voteResults = new HashMap<>();

            voteResults.put(playerList.get(0).getPlayerIdentifier(), A);
            voteResults.put(playerList.get(1).getPlayerIdentifier(), B);
            voteResults.put(playerList.get(2).getPlayerIdentifier(), C);

            return voteResults;
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
        return new HashMap<>();
    }

    /**
     * Helper class to generate unique user subs for every test execution.
     */
    private static class UserCounter {
        // the singleton UserCounter object
        private static UserCounter instance;

        // the value of the counter
        private int userCount;

        private UserCounter() {
            this.userCount = 0;
        }

        public static UserCounter getInstance() {
            if (instance == null) {
                instance = new UserCounter();
            }
            return UserCounter.instance;
        }

        /**
         * Returns the value of {@code userCount}, then increments the value by 1.
         *
         * @return {@code userCount}
         */
        int getCounter() {
            return userCount++;
        }
    }
}
