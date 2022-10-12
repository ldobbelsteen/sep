package org.lukos.model.voting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.InstanceDB;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.voting.NotAllowedTargetException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link Vote}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 23-02-2022
 */
public abstract class VoteTest extends GameTest {

    /** The game master ID of the game master of the instance */
    protected final int gameMasterID = 123;
    /** The game name of the instance */
    protected final String gameName = "test";
    /** The game name of the seed */
    protected final int gameSeed = 1;
    /** Test instance. */
    protected Vote instance;
    /** The ID of the instance */
    protected int gameInstanceID;

    /**
     * Creates a new instance of {@code Vote}.
     *
     * @return The new instance.
     */
    protected abstract Vote createNewInstance();

    /**
     * Initializes the instance before every test case.
     */
    @BeforeEach
    public void init() {
        try {
            gameInstanceID = InstanceDB.addNewInstance(gameMasterID, gameName, gameSeed);

            this.instance = createNewInstance();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    // TODO: Fix them
//    /**
//     * Tests the constructor with 1 parameter.
//     *
//     * @utp.description Tests the constructor with 1 parameter.
//     */
//    @Test
//    public void constructorVoteSingleParameterTest1() {
//        try {
//            // Create the vote
//            VoteType type = VoteType.ALPHA_WOLF;
//            new VoteImpl(gameInstanceID, type);
//
//            // Test the single constructor
//            Vote vote = new VoteImpl(gameInstanceID);
//            assertEquals(type, vote.getVoteType(), "Type should be the same.");
//            assertEquals(new ArrayList<>(), vote.getBallots(), "Ballots list should be the same as an empty one.");
//            assertFalse(vote.started(), "Vote should not be started.");
//            assertFalse(vote.ended(), "Vote should not be ended.");
//        } catch (SQLException e) {
//            fail("Should not have thrown an SQL exception.");
//        }
//    }
//
//    /**
//     * Tests the constructor with 1 parameter.
//     *
//     * @utp.description Tests the constructor with 1 parameter.
//     */
//    @Test
//    public void constructorVoteSingleParameterTest2() {
//        try {
//            // Create the vote
//            VoteType type = VoteType.MISC;
//            new VoteImpl(gameInstanceID, type);
//
//            // Test the single constructor
//            Vote vote = new VoteImpl(gameInstanceID);
//
//            assertEquals(type, vote.getVoteType(), "Type should be the same.");
//            assertEquals(new ArrayList<>(), vote.getBallots(), "Ballots list should be the same as an empty one.");
//            assertFalse(vote.started(), "Vote should not be started.");
//            assertFalse(vote.ended(), "Vote should not be ended.");
//        } catch (SQLException e) {
//            fail("Should not have thrown an SQL exception.");
//        }
//    }
//
//    /**
//     * Tests the constructor with 2 parameters.
//     *
//     * @utp.description Tests the constructor with 2 parameters.
//     */
//    @Test
//    public void constructorVoteMultiParameterTest1() {
//        try {
//            VoteType type = VoteType.MAYOR;
//            Vote vote = new VoteImpl(gameInstanceID, type);
//
//            assertEquals(gameInstanceID, vote.getVid(), "IDs should be equal.");
//            assertEquals(type, vote.getVoteType(), "Type should be the same.");
//            assertEquals(new ArrayList<>(), vote.getBallots(), "Ballots list should be the same as an empty one.");
//            assertFalse(vote.started(), "Vote should not be started.");
//            assertFalse(vote.ended(), "Vote should not be ended.");
//        } catch (SQLException e) {
//            fail("Should not have thrown an SQL exception.");
//        }
//    }
//
//    /**
//     * Tests the constructor with 2 parameters.
//     *
//     * @utp.description Tests the constructor with 2 parameters.
//     */
//    @Test
//    public void constructorVoteMultiParameterTest2() {
//        try {
//            VoteType type = VoteType.LYNCH;
//            int vid = 123456789;
//            Vote vote = new VoteImpl(vid, type);
//
//            assertEquals(vid, vote.getVid(), "IDs should be equal.");
//            assertEquals(type, vote.getVoteType(), "Type should be the same.");
//            assertEquals(new ArrayList<>(), vote.getBallots(), "Ballots list should be the same as an empty one.");
//            assertFalse(vote.started(), "Vote should not be started.");
//            assertFalse(vote.ended(), "Vote should not be ended.");
//        } catch (SQLException e) {
//            fail("Should not have thrown an SQL exception.");
//        }
//    }

    /**
     * Tests whether IDs do not overlap and are not {@code null}.
     *
     * @utp.description Tests whether IDs do not overlap and are not {@code null}.
     */
    @Test
    public void getVidTest() {
        Vote vote = this.instance;
        Vote vote1 = createNewInstance();
        Vote vote2 = createNewInstance();

        assertNotEquals(vote.getVid(), vote1.getVid(), "IDs should not be equal. (vote)");
        assertNotEquals(vote1.getVid(), vote2.getVid(), "IDs should not be equal. (vote1)");
        assertNotEquals(vote2.getVid(), vote.getVid(), "IDs should not be equal. (vote2)");
    }

    /**
     * @utp.description Tests whether the {@code submitVote()} function throws an {@code SQLException} when we try to
     * submit a ballot to a vote that does not exist.
     */
    @Test
    public void submitVoteExceptionTest() {
        // Create vote that does not exist
        Vote vote = new VoteImpl(69);

        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        Ballot ballot = new Ballot(henkID, janID);

        Class<?> expected = SQLException.class;
        try {
            vote.submitVote(henkID, ballot);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * Tests whether the VoteTypes are the same as what we put into it.
     *
     * @utp.description Tests whether the VoteTypes are the same as what we put into it.
     */
    @Test
    public void getVoteTypeTest() {
        try {
            Vote voteAW = new VoteImpl(gameInstanceID, VoteType.ALPHA_WOLF);
            Vote voteL = new VoteImpl(gameInstanceID, VoteType.LYNCH);
            Vote voteMayor = new VoteImpl(gameInstanceID, VoteType.MAYOR);
            Vote voteMisc = new VoteImpl(gameInstanceID, VoteType.MISC);

            assertEquals(voteAW.getVoteType(), VoteType.ALPHA_WOLF, "VoteType should be ALPHA_WOLF.");
            assertEquals(voteL.getVoteType(), VoteType.LYNCH, "VoteType should be LYNCH.");
            assertEquals(voteMayor.getVoteType(), VoteType.MAYOR, "VoteType should be MAYOR.");
            assertEquals(voteMisc.getVoteType(), VoteType.MISC, "VoteType should be MISC.");
        } catch (SQLException e) {
            fail("Should not have thrown an SQL exception.");
        }
    }

    /**
     * @utp.description Tests whether the {@code isBusy()} only returns true when it is supposed to, i.e. it returns
     * true when it is started but not ended.
     */
    @Test
    public void getBusyTest() {
        try {
            Vote vote = new VoteImpl(gameInstanceID, VoteType.MISC);

            // At creation, it shouldn't be started
            assertFalse(vote.isBusy());

            // After starting and not ending it should be busy
            vote.start();
            assertTrue(vote.isBusy());

            // After ending, it should not be busy
            vote.end();
            assertFalse(vote.isBusy());
        } catch (SQLException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests the overridden {@code equal()} method.
     */
    @Test
    public void equalsTest() {
        try {
            Vote vote = new VoteImpl(gameInstanceID, VoteType.MISC);

            assertEquals(vote, new VoteImpl(vote.getVid()));
            assertNotEquals(vote, new VoteImpl(vote.getVid() + 1));
            assertNotEquals(vote, new Object());
        } catch (SQLException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Simple implementation of the Vote class for testing purposes.
     */
    static class VoteImpl extends Vote {
        public VoteImpl(int instanceID) {
            super(instanceID);
        }

        public VoteImpl(int iid, VoteType voteType) throws SQLException {
            super(iid, voteType);
        }
    }
}
