package org.lukos.model.user.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.LocationDB;
import org.lukos.database.PlayerDB;
import org.lukos.database.UserDB;
import org.lukos.model.GameTest;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.exceptions.user.NoSuchRoleException;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.exceptions.voting.NoSuchVoteException;
import org.lukos.model.exceptions.voting.VotingException;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.notes.Deathnote;
import org.lukos.model.rolesystem.*;
import org.lukos.model.rolesystem.jobs.AlphaWolf;
import org.lukos.model.rolesystem.jobs.Mayor;
import org.lukos.model.rolesystem.roles.doubleroles.Follower;
import org.lukos.model.rolesystem.roles.doubleroles.Jester;
import org.lukos.model.rolesystem.roles.mainroles.Graverobber;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.voting.Ballot;
import org.lukos.model.voting.Vote;
import org.lukos.model.voting.VoteType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link Player}, {@link PlayerDBHelper} and {@link PlayerVoteHelper} class.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 22-02-2022
 */
public class PlayerTest extends GameTest {

    /** Instance id */
    private int instanceId;
    private IInstance instance;

    /** First player */
    private Player player;

    /** Second player */
    private Player secondPlayer;

    /** Third player */
    private Player thirdPlayer;

    @BeforeEach
    public void beforeTests() {
        try {
            // Get the instance manager
            InstanceManager im = InstanceManager.getInstanceManager();

            // Create 3 users
            int gameMasterId = UserDB.createUser("is1", "sub1", "User1");
            int user2Id = UserDB.createUser("is2", "sub2", "User2");
            int user3Id = UserDB.createUser("is3", "sub3", "User3");

            // Create a new game and get the player
            instanceId = (new User(gameMasterId)).createGame("PlayerTestGame", 1);
            player = (new User(gameMasterId)).getPlayer();
            instance = InstanceManager.getInstanceManager().getInstance(instanceId);

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
            instance.startGame(gameMasterId);

            // Make sure its day phase on day 1
            instance.getInstanceState().setDay(1);
            instance.getInstanceState().setPhase(DayPhase.EXECUTION);
            instance.nextPhase();

        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * Constructor test.
     *
     * @utp.description Test whether the player is initialized correctly.
     */
    @Test
    public void constructorTest() throws Exception {
        int userId = UserDB.createUser("is20", "is20", "User");
        (new User(userId)).joinGame(instanceId);
        Player consPlayer = (new User(userId)).getPlayer();

        // Test that player was initialized correctly
        assertEquals(instanceId, player.getPlayerIdentifier().instanceID(), "instanceId mismatch!");
        assertEquals(userId, consPlayer.getPlayerIdentifier().userID(), "userId mismatch!!");
        assertNotNull(consPlayer.getDoubleRoles(), "DoubleRoles should not be null!");
        assertEquals(new ArrayList<DoubleRole>(), consPlayer.getDoubleRoles(), "DoubleRoles should be empty!");
        assertNotNull(consPlayer.getDeathnote(), "Deathnote should not be null!");
        assertNull(player.getDeathnote().getContent(), "Deathnote should be empty at creation!");
        assertTrue(consPlayer.getDeathnote().getChangeable(), "Deathnote should be changeable at creation!");
        assertNotNull(consPlayer.getJobs(), "Jobs should not be null!");
        assertEquals(new ArrayList<Job>(), consPlayer.getJobs(), "Jobs should be empty!");
        try {
            player.getMainRole();
        } catch (NoSuchRoleException e) {
            assertNotNull(e.getMessage(), "Exception message should not be null!");
        } catch (Exception e) {
            fail("NoSuchRoleException should have been thrown. Instead the following exception was thrown: " + e);
        }
    }

    //region alive
    /**
     * alive test, player is alive.
     *
     * @utp.description Test whether {@code true} is returned when the player is alive.
     */
    @Test
    public void aliveAliveTest() {
        try {
            assertTrue(player.alive(), "Player should be alive!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * alive test, player is dead.
     *
     * @utp.description Test whether {@code false} is returned when the player is dead.
     */
    @Test
    public void aliveDeadTest() {
        try {
            instance.killPlayer(player);
            assertFalse(player.alive(), "Player should be dead!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
    //endregion
    //region update Deathnote

    /**
     * Simple test for updateNote based on a string.
     *
     * @utp.description Test whether the new deathnote is set correctly if {@code isChangeable == true}, based on {@code String}.
     */
    @Test
    public void updateNoteStringTest() {
        // Create new content
        String newContent = "Test";

        // Set the new content
        try {
            // Get isChangeable for testing
            boolean isC = player.getDeathnote().getChangeable();
            player.updateNote(newContent);

            // Test if everything is equal
            assertEquals(newContent, player.getDeathnote().getContent(), "Content of notes is not equal!");
            assertEquals(isC, player.getDeathnote().getChangeable(), "isChangeable is not equal!");
        } catch (Exception e) {
           fail("An exception was thrown: " + e);
        }
    }

    /**
     * Simple setDeathNote test.
     *
     * @utp.description Test whether a new {@code Deathnote} is set correctly.
     */
    @Test
    public void setDeathNoteTest() {
        try {
            // Create death note
            Deathnote deathnote = new Deathnote();
            deathnote.setContent("newContent");
            deathnote.setChangeable(false);

            // Set the deathnote
            player.setDeathNote(deathnote);

            // Test
            assertEquals("newContent", player.getDeathnote().getContent(), "Content mismatch!");
            assertFalse(player.getDeathnote().getChangeable(), "Changeable should be false!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    //endregion
    //region add/remove doubleRole
    /**
     * Checks if the types of elements in expected list have an one-to-one mapping with the elements in the result list.
     */
    private void listComparison(List expected, List result) {
        boolean found;
        assertEquals(expected.size(), result.size(), "Lists are not equal in size!");
        for (Object o1: expected) {
            found = false;
            for (Object o2: result) {
                if (o1.getClass() == o2.getClass()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // -- Construct fail message --
                StringBuilder stringBuilder = new StringBuilder();
                // Expected list
                stringBuilder.append("Expected: [");
                expected.forEach(object -> stringBuilder.append(object.getClass().getSimpleName() + ", "));
                int strLength = stringBuilder.length();
                stringBuilder.delete(strLength-2, strLength); // remove last comma
                stringBuilder.append("] \n");
                // Real list
                stringBuilder.append("Real list: [");
                result.forEach(object -> stringBuilder.append(object.getClass().getSimpleName() + ", "));
                strLength = stringBuilder.length();
                stringBuilder.delete(strLength-2, strLength); // remove last comma
                stringBuilder.append("] \n");

                // Fail the test
                fail("The lists are not equal! " + o1.getClass().getSimpleName() + " was in expected list, but not in real list. \n" + stringBuilder);
            }
        }
    }

    /** Adds SomeDoubleRole to player and tests that it was done successfully. */
    private List<DoubleRole> addSomeDoubleRole() throws SQLException, ReflectiveOperationException {
        List<DoubleRole> expected = new ArrayList<>();

        // Test that the doubleRoles list is empty
        listComparison(expected, player.getDoubleRoles());

        // Create a new double role
        Jester jester = new Jester();

        // Add the double role to the player, this should go well
        try {
            player.addDoubleRole(jester);
            expected.add(jester);
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
        // Test that the double role was added correctly
        listComparison(expected, player.getDoubleRoles());

        return expected;
    }

    /**
     * addDoubleRole test, no double roles, try to add one double role.
     *
     * @utp.description Test whether {@code SomeDoubleRole} is added correctly when {@code doubleRoles} is empty.
     */
    @Test
    public void addDoubleRoleEmptyTest() {
        try {
            assertEquals(new ArrayList<>(), player.getDoubleRoles(), "Double roles should be empty at the start!");
            addSomeDoubleRole();
            assertEquals(new ArrayList<>(), player.getJobs(), "Job list should be empty!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * addDoubleRole test, no double roles, add multiple double roles one by one.
     *
     * @utp.description Test whether {@code SomeDoubleRole} and {@code AnotherDoubleRole} are added correctly when {@code doubleRoles} is empty.
     */
    @Test
    public void addDoubleRoleAllRolesTest() {
        try {
            List<DoubleRole> expected = addSomeDoubleRole();

            // Create AnotherDoubleRole role
            Follower anotherDoubleRole = new Follower();
            // Add second double role

            player.addDoubleRole(anotherDoubleRole);
            expected.add(anotherDoubleRole);

            // Test
            listComparison(expected, player.getDoubleRoles());

        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    //endregion
    //region add/remove job
    /** Adds SomeJob job to player and test that it was done successfully. */
    private List<Job> addSomeJob() throws SQLException, ReflectiveOperationException {
        List<Job> expected = new ArrayList<>();

        // Test that the job list is empty
        listComparison(expected, player.getJobs());

        // Create a new job
        AlphaWolf alphaWolf = new AlphaWolf();

        // Add the job to the player, this should go well
        try {
            player.addJob(alphaWolf);
            expected.add(alphaWolf);
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
        // Test that the job was added correctly
        listComparison(expected, player.getJobs());

        return expected;
    }

    /**
     * addJob test, no jobs, try to add one job.
     *
     * @utp.description Test whether {@code SomeJob} is added correctly when {@code jobs} is empty.
     */
    @Test
    public void addJobEmptyTest() {
        try {
        assertEquals(new ArrayList<>(), player.getJobs(), "Jobs list should be empty at the start!");
        addSomeJob();
        assertEquals(new ArrayList<>(), player.getDoubleRoles(), "DoubleRoles list should be empty!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * addJob test, no jobs, add multiple jobs one by one.
     *
     * @utp.description Test whether {@code SomeJob} and {@code AnotherJob} are added correctly when {@code jobs} is empty.
     */
    @Test
    public void addJobAllRolesTest()  {
        try {
            // Add someJob job to player
            List<Job> expected = addSomeJob();

            // Create an instance for the other job
            Mayor mayor = new Mayor();

            // Add the anotherJob job to the player
            try {
                player.addJob(mayor);
                expected.add(mayor);
            } catch (Exception e) {
                fail("An exception was thrown when adding the AnotherJob job: " + e);
            }
            // Test that the job was added correctly
            listComparison(expected, player.getJobs());
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * removeJob test, player has one job, remove it.
     *
     * @utp.description Test whether {@code SomeJob} is removed correctly, when the player had this job.
     */
    @Test
    public void removeJobOneTest()  {
        try {
            // Add someJob job to player
            List<Job> expected = addSomeJob();

            // Remove someJob job from player and list
            player.removeJob(new AlphaWolf());
            expected.remove(0);

            // Test that the list are equal
            listComparison(expected, player.getJobs());
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * removeJob test, player has both jobs, remove them one by one.
     *
     * @utp.description Test whether {@code SomeJob} and {@code AnotherJob} are removed correctly.
     */
    @Test
    public void removeJobAllTest()  {
        try {
            // --- Add all jobs to the player ---
            // Add someJob job to player
            List<Job> expected = addSomeJob();

            // Create an instance for the other job
            Mayor mayor = new Mayor();

            // Add the AnotherJob job to the player
            try {
                player.addJob(mayor);
                expected.add(mayor);
            } catch (Exception e) {
                fail("An exception was thrown when adding the AnotherJob job: " + e);
            }
            // Test that the job was added correctly
            listComparison(expected, player.getJobs());

            // --- Remove all jobs ---

            // Remove someJob
            assertTrue(player.removeJob(new AlphaWolf()), "Should have found the SomeJob job!");
            expected.remove(0);
            listComparison(expected, player.getJobs());

            // Remove anotherJob
            assertTrue(player.removeJob(new Mayor()), "Should have found the AnotherJob job!");
            expected.remove(0);
            listComparison(expected, player.getJobs());
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * removeJob test, player has no jobs, try to remove a job this player does not have.
     *
     * @utp.description Test whether {@code false} is returned if a job is removed that the player does not have.
     */
    @Test
    public void removeJobNotExistTest()  {
        try {
            assertTrue(player.removeJob(new AlphaWolf()), "PLayer does not have this job, so operation should have been successful");
            listComparison(new ArrayList<>(), player.getJobs());
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * removeJob test, player has one job, try to remove a job this player does not have.
     *
     * @utp.description Test whether {@code false} is returned if a job is removed that the player does not have.
     */
    @Test
    public void removeJobOneJobTest()  {
        try {
            // Add SomeJob to the player
            List<Job> expected = addSomeJob();
            // Remove AnotherJob from the player
            assertTrue(player.removeJob(new Mayor()), "PLayer does not have this job, so operation should have been successful");
            listComparison(expected, player.getJobs());
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    //endregion
    //region vote
    /** Create a player vote */
    private Vote createPlayerVote() {
        try {
            return instance.startVote(VoteType.MAYOR);
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
            return null;
        }
    }

    /**
     * General test, where three players all votes on another in a {@code PlayerVote}. Each player only votes once.
     *
     * @utp.description Test whether the ballots are correct when three distinct players can vote on each other.
     */
    @Test
    public void voteTest1() throws SQLException {
        // Create a vote
        Vote playerVote = createPlayerVote();
        // Get the vote id
        int vid = playerVote.getVid();

        List<Ballot> expectedBallots = new ArrayList<>();
        List<Ballot> ballots;

        // Player one votes
        try {
            player.vote(vid, secondPlayer);
            expectedBallots.add(new Ballot(player.getPlayerIdentifier(), secondPlayer.getPlayerIdentifier()));
        } catch (Exception e) {
            fail("An exception was thrown when player one tried to vote: " + e);
        }

        // Get ballots and compare
        ballots = playerVote.getBallots();
        ballotEqual(expectedBallots, ballots);

        // Player two votes
        try {
            secondPlayer.vote(vid, thirdPlayer);
            expectedBallots.add(new Ballot(secondPlayer.getPlayerIdentifier(), thirdPlayer.getPlayerIdentifier()));
        } catch (Exception e) {
            fail("An exception was thrown when player two tried to vote: " + e);
        }

        // Get ballots and compare
        ballots = playerVote.getBallots();
        ballotEqual(expectedBallots, ballots);

        // Player three votes
        try {
            thirdPlayer.vote(vid, player);
            expectedBallots.add(new Ballot(thirdPlayer.getPlayerIdentifier(), player.getPlayerIdentifier()));
        } catch (Exception e) {
            fail("An exception was thrown when player three tried to vote: " + e);
        }

        // Get ballots and compare
        ballots = playerVote.getBallots();
        ballotEqual(expectedBallots, ballots);
    }

    /**
     * General test, one player votes three times on different votes.
     *
     * @utp.description Test whether the ballots are correct when one player votes once on three different votes.
     */
    @Test
    public void voteTest2() throws SQLException {
        // Create a votes
        Vote playerVote1 = createPlayerVote();
        Vote playerVote2 = createPlayerVote();
        Vote playerVote3 = createPlayerVote();

        // Get vid
        int vid1 = playerVote1.getVid();
        int vid2 = playerVote2.getVid();
        int vid3 = playerVote3.getVid();

        List<Ballot> expectedBallots1 = new ArrayList<>();
        List<Ballot> ballots1;
        List<Ballot> expectedBallots2 = new ArrayList<>();
        List<Ballot> ballots2;
        List<Ballot> expectedBallots3 = new ArrayList<>();
        List<Ballot> ballots3;

        // Vote three times, on each vote once
        try {
            player.vote(vid1, player);
            expectedBallots1.add(new Ballot(player.getPlayerIdentifier(), player.getPlayerIdentifier()));
        } catch (Exception e) {
            fail("An exception was thrown when voting the first time: " + e);
        }

        try {
            player.vote(vid2, secondPlayer);
            expectedBallots2.add(new Ballot(player.getPlayerIdentifier(), secondPlayer.getPlayerIdentifier()));
        } catch (Exception e) {
            fail("An exception was thrown when voting the second time: " + e);
        }

        try {
            player.vote(vid3, thirdPlayer);
            expectedBallots3.add(new Ballot(player.getPlayerIdentifier(), thirdPlayer.getPlayerIdentifier()));
        } catch (Exception e) {
            fail("An exception was thrown when voting the third time: " + e);
        }

        // Test that all tests where added to the correct vote
        ballots1 = playerVote1.getBallots();
        ballotEqual(expectedBallots1, ballots1);

        ballots2 = playerVote2.getBallots();
        ballotEqual(expectedBallots2, ballots2);

        ballots3 = playerVote3.getBallots();
        ballotEqual(expectedBallots3, ballots3);
    }

    /**
     * vote test, no ongoing votes, test whether NoSuchVoteException is thrown correctly.
     *
     * @utp.description {@code onGoingVotes} is empty, test whether an {@code NoSuchVoteException} is thrown, when trying to vote on a vote that does not exist.
     */
    @Test
    public void voteExceptionNoVotesTest() throws Exception {
        // Player one votes on a vote that does not exist
        try {
            player.vote(1234567, secondPlayer);
            fail("An NoSuchVoteException should have been thrown!");
        } catch (VotingException e) {
            Class expected = NoSuchVoteException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }

    /**
     * vote test, with ongoing votes, but we vote on a vote that does not exist, test whether NoSuchVoteException is thrown correctly.
     *
     * @utp.description {@code onGoingVotes} is not empty, test whether an {@code NoSuchVoteException} is thrown, when trying to vote on a vote that does not exist.
     */
    @Test
    public void voteExceptionOngoingVotesTest() throws Exception {
        // Create a vote
        createPlayerVote();

        // Player one votes on a vote that does not exist
        try {
            player.vote(1234567, secondPlayer);
            fail("An NoSuchVoteException should have been thrown!");
        } catch (VotingException | SQLException | NoSuchUserException e) {
            Class expected = NoSuchVoteException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }

    /** Check whether two list of ballots are equal */
    private void ballotEqual(List<Ballot> expected, List<Ballot> result) {
        // Check if the sizes are equal
        assertEquals(expected.size(), result.size(), "The sizes are different!");

        // Check if the content of the lists are equal
        for (int i = 0; i < expected.size(); i++) {
            // Get the ballot at index i
            Ballot expBallot = expected.get(i);
            Ballot realBallot = result.get(i);

            // Both lists should contain references to the same objects
            assertEquals(expBallot.player(), realBallot.player(), "Player mismatch on i = " + i);
            assertEquals(expBallot.target(), realBallot.target(), "Target mismatch on i = " + i);
        }
    }

    //endregion
    //region performActions

    /**
     * performAction test, only main role.
     *
     * @utp.description Test whether the {@code Action} of one {@code Purpose} is correctly performed when {@code player} has exactly one purpose.
     */
    @Test
    public void performActionsTest1() {
        try {
            // Prepare by killing secondPlayer
            MainRole mainRole = secondPlayer.getMainRole();
            InstanceManager.getInstanceManager().getInstance(instanceId).killPlayer(secondPlayer);
            assertFalse(secondPlayer.alive(), "secondPlayer should have died!");

            // Set players role as Graverobber and perform action
            player.setMainRole(new Graverobber());
            player.getMainRole().initializeActions(player.getPlayerIdentifier());
            player.performAction(new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier()))), Action.ROB_GRAVE);

            // Test that the role as stolen
            assertEquals(mainRole.getClass(), player.getMainRole().getClass(), "MainRole was not changed!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * performAction test, multiple purposes.
     *
     * @utp.description Test whether the {@code Action} of one {@code Purpose} is correctly performed when {@code player} has multiple purposes.
     */
    @Test
    public void performActionsTest2() {
        try {
            // Prepare by killing secondPlayer
            MainRole mainRole = secondPlayer.getMainRole();
            InstanceManager.getInstanceManager().getInstance(instanceId).killPlayer(secondPlayer);
            assertFalse(secondPlayer.alive(), "secondPlayer should have died!");

            // Set players role as werewolf add mayor job and add alpha wolf job
            player.setMainRole(new Graverobber());
            player.addJob(new AlphaWolf(1));
            player.addJob(new Mayor());
            // Initialize all the actions for all purposes
            player.getPurposes().forEach(purpose -> {
                try {
                    purpose.initializeActions(player.getPlayerIdentifier());
                } catch (Exception e) {
                    fail("An exception was thrown when initializing actions: " + e);
                }
            });
            // Try performAction
            player.performAction(new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier()))), Action.ROB_GRAVE);

            // Test that the role as stolen
            assertEquals(mainRole.getClass(), player.getMainRole().getClass(), "MainRole was not changed!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * performAction test, exception must be thrown if the player is not allowed to perform an action.
     *
     * @utp.description Test whether an {@code NoPermissionException} is thrown when the {@code Player} does not have the required {@code Purpose}.
     */
    @Test
    public void performActionsExceptionTest() {
        // Set players role as Graverobber
        try {
            player.setMainRole(new Graverobber());
            player.getMainRole().initializeActions(player.getPlayerIdentifier());
        } catch (Exception e) {
            fail("An exception was thrown when setting new mainRole: " + e);
        }

        try {
            player.performAction(new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier()))), Action.ALPHA_WOLF_KILL);
        } catch (NoPermissionException e) {
            try {
                assertTrue(secondPlayer.alive(), "secondPlayer should still be alive!");
            } catch (Exception ex) {
                fail("An exception was thrown when testing whether secondPlayer was alive: " + ex);
            }
        } catch (Exception e) {
            fail("An exception was thrown when performing the action: " + e);
        }
    }


    //endregion
    //region Remaining setter and getter tests
    // These are simple tests to test the setters and some getters
    /**
     * setMainRole test.
     *
     * @utp.description Test whether {@code player.mainRole} is set correctly.
     */
    @Test
    public void setMainRoleTest() {
        try {
            Werewolf werewolf = new Werewolf();
            player.setMainRole(werewolf);
            assertEquals(werewolf.getClass(), player.getMainRole().getClass(), "MainRole was not set correctly!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * setHouse test.
     *
     * @utp.description Test whether {@code player.house} is set correctly.
     */
    @Test
    public void setHouseTest() {
        try {
            int locationId = LocationDB.createNewLocation(instanceId);
            assertNotEquals(locationId, player.getHouse(), "Player should not own this house");
            player.setHouse(locationId);
            assertEquals(locationId, player.getHouse(), "House was not set correctly!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * setProtected test.
     *
     * @utp.description Test whether {@code player.protected} is set correctly.
     */
    @Test
    public void setProtectedTest() {
        try {
            // Player should not be protected
            List<PlayerIdentifier> protectedPlayersBefore = PlayerDB.getProtectedPlayers(instanceId);
            protectedPlayersBefore.forEach(playerId -> assertNotEquals(player.getPlayerIdentifier(), playerId, "Player should not be protected at the start!"));

            // Make the player protected
            player.setProtected(true);

            // Test
            List<PlayerIdentifier> protectedPlayersMiddle= PlayerDB.getProtectedPlayers(instanceId);
            boolean found = false;
            for (PlayerIdentifier pId: protectedPlayersMiddle) {
                if (pId.equals(player.getPlayerIdentifier())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Player should be protected!");

            // Make the player unprotected again
            player.setProtected(false);

            // Player should not be protected anymore
            List<PlayerIdentifier> protectedPlayersAfter = PlayerDB.getProtectedPlayers(instanceId);
            protectedPlayersAfter.forEach(playerId -> assertNotEquals(player.getPlayerIdentifier(), playerId, "Player should not be protected anymore!"));

        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * getPurposes test, add job, double role and main role and get them
     *
     * @utp.description Test whether the correct purposes (jobs, double roles and main roles) are returned.
     */
    @Test
    public void getPurposesTest() {
        try {
            List<Purpose> expected = new ArrayList<>();

            // expected purposes to be returned
            expected.addAll(addSomeJob());
            expected.addAll(addSomeDoubleRole());
            expected.add(new Werewolf());
            player.setMainRole(new Werewolf());

            listComparison(expected, player.getPurposes());

        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    //endregion
}
