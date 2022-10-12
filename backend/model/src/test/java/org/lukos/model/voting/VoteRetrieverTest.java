package org.lukos.model.voting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.InstanceDB;
import org.lukos.database.VoteDB;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.voting.NoSuchVoteException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link VoteRetriever}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-04-2022
 */
public class VoteRetrieverTest extends GameTest {

    /** The instance ID of the instance that is created for testing. */
    private int gameID;

    /**
     * Before every test this method initializes all the things that are necessary for running the tests.
     */
    @BeforeEach
    public void init() {
        try {
            String gameName = "test";
            int gameSeed = 1;
            int gameMasterID = 123;

            this.gameID = InstanceDB.addNewInstance(gameMasterID, gameName, gameSeed);
        } catch (SQLException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests the {@code retrieveVote()} function whether it returns the right vote.
     */
    @Test
    public void retrieveVoteTest1() {
        try {
            VoteType type = VoteType.MAYOR;
            int vid = (new VoteTest.VoteImpl(gameID, type)).getVid();

            Vote vote = VoteRetriever.retrieveVote(vid);
            assertEquals(type, vote.getVoteType());
            assertEquals(vid, vote.getVid());
        } catch (SQLException | NoSuchVoteException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests the {@code retrieveVote()} function whether it returns the right vote even after creating
     * multiple votes.
     */
    @Test
    public void retrieveVoteTest2() {
        try {
            VoteType type1 = VoteType.ALPHA_WOLF;
            int vid1 = (new VoteTest.VoteImpl(gameID, type1)).getVid();
            VoteType type2 = VoteType.MAYOR;
            int vid2 = (new VoteTest.VoteImpl(gameID, type2)).getVid();
            VoteType type3 = VoteType.LYNCH;
            int vid3 = (new VoteTest.VoteImpl(gameID, type3)).getVid();

            Vote vote1 = VoteRetriever.retrieveVote(vid1);
            Vote vote2 = VoteRetriever.retrieveVote(vid2);
            Vote vote3 = VoteRetriever.retrieveVote(vid3);

            assertEquals(type1, vote1.getVoteType());
            assertEquals(vid1, vote1.getVid());
            assertTrue(vote1 instanceof AlphaWolfVote);
            assertEquals(type2, vote2.getVoteType());
            assertEquals(vid2, vote2.getVid());
            assertTrue(vote2 instanceof PlayerVote);
            assertEquals(type3, vote3.getVoteType());
            assertEquals(vid3, vote3.getVid());
            assertTrue(vote3 instanceof PlayerVote);
        } catch (SQLException | NoSuchVoteException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests the {@code retrieveVote} function on whether it throws the {@link NoSuchVoteException}
     * when trying to retrieve a {@link Vote} that does not exist.
     */
    @Test
    public void retrieveVoteExceptionTest1() {
        Class<?> expected = NoSuchVoteException.class;
        try {
            VoteRetriever.retrieveVote(69);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests the {@code retrieveVote} function on whether it throws the {@link NoSuchVoteException}
     * when trying to retrieve a {@link Vote} that has a type that does not have an implementation.
     */
    @Test
    public void retrieveVoteExceptionTest2() {
        Class<?> expected = NoSuchVoteException.class;
        try {
            VoteType type = VoteType.MISC;
            int vid = (new VoteTest.VoteImpl(gameID, type)).getVid();

            VoteRetriever.retrieveVote(vid);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests the {@code retrieveVoteByIdIfInInstance()} function, whether it retrieves the right
     * {@link Vote}.
     */
    @Test
    public void retrieveVoteByIdIfInInstanceTest() {
        try {
            VoteType type1 = VoteType.ALPHA_WOLF;
            int vid1 = (new VoteTest.VoteImpl(gameID, type1)).getVid();
            VoteType type2 = VoteType.MAYOR;
            int vid2 = (new VoteTest.VoteImpl(gameID, type2)).getVid();
            VoteType type3 = VoteType.LYNCH;
            int vid3 = (new VoteTest.VoteImpl(gameID, type3)).getVid();

            Vote vote1 = VoteRetriever.retrieveVoteByIdIfInInstance(vid1, gameID);
            Vote vote2 = VoteRetriever.retrieveVoteByIdIfInInstance(vid2, gameID);
            Vote vote3 = VoteRetriever.retrieveVoteByIdIfInInstance(vid3, gameID);

            assertEquals(type1, vote1.getVoteType());
            assertEquals(vid1, vote1.getVid());
            assertEquals(type2, vote2.getVoteType());
            assertEquals(vid2, vote2.getVid());
            assertEquals(type3, vote3.getVoteType());
            assertEquals(vid3, vote3.getVid());
        } catch (SQLException | NoSuchVoteException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code retrieveVoteByIdIfInInstance()} throws a
     * {@link NoSuchVoteException} when we request a vote that does not exist.
     */
    @Test
    public void retrieveVoteByIdIfInInstanceExceptionTest1() {
        Class<?> expected = NoSuchVoteException.class;
        try {
            VoteRetriever.retrieveVoteByIdIfInInstance(69, gameID);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the function {@code retrieveVoteByIdIfInInstance()} throws a
     * {@link NoSuchVoteException} when we request a vote that does not belong to the instance ID given.
     */
    @Test
    public void retrieveVoteByIdIfInInstanceExceptionTest2() {
        Class<?> expected = NoSuchVoteException.class;
        try {
            VoteType type = VoteType.MISC;
            int vid = (new VoteTest.VoteImpl(gameID, type)).getVid();

            VoteRetriever.retrieveVoteByIdIfInInstance(vid, 69);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests the {@code retrieveVoteByIdIfInInstance()} function, whether it retrieves no
     * {@link Vote}s when there are no ongoing {@link Vote}s.
     */
    @Test
    public void retrieveOngoingVotesByInstanceTest1() {
        try {
            List<Vote> votes = VoteRetriever.retrieveOngoingVotesByInstance(gameID);

            assertEquals(0, votes.size());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests the {@code retrieveVoteByIdIfInInstance()} function, whether it retrieves no
     * {@link Vote}s when there are ongoing {@link Vote}s.
     */
    @Test
    public void retrieveOngoingVotesByInstanceTest2() {
        try {
            VoteType type1 = VoteType.ALPHA_WOLF;
            Vote vote1 = new VoteTest.VoteImpl(gameID, type1);
            vote1.start();

            VoteType type2 = VoteType.MAYOR;
            Vote vote2 = new VoteTest.VoteImpl(gameID, type2);
            vote2.start();

            VoteType type3 = VoteType.LYNCH;
            Vote vote3 = new VoteTest.VoteImpl(gameID, type3);
            vote3.start();

            List<Vote> votes = VoteRetriever.retrieveOngoingVotesByInstance(gameID);

            assertEquals(3, votes.size());
            assertTrue(votes.contains(vote1));
            assertTrue(votes.contains(vote2));
            assertTrue(votes.contains(vote3));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests the {@code retrieveVoteByIdIfInInstance()} function, whether it retrieves no
     * {@link Vote}s when there are ongoing {@link Vote}s and not ongoing {@link Vote}s.
     */
    @Test
    public void retrieveOngoingVotesByInstanceTest3() {
        try {
            VoteType type1 = VoteType.ALPHA_WOLF;
            Vote vote1 = new VoteTest.VoteImpl(gameID, type1);
            vote1.start();

            VoteType type2 = VoteType.MAYOR;
            Vote vote2 = new VoteTest.VoteImpl(gameID, type2);

            VoteType type3 = VoteType.LYNCH;
            Vote vote3 = new VoteTest.VoteImpl(gameID, type3);
            vote3.start();

            List<Vote> votes = VoteRetriever.retrieveOngoingVotesByInstance(gameID);

            assertEquals(2, votes.size());
            assertTrue(votes.contains(vote1));
            assertFalse(votes.contains(vote2));
            assertTrue(votes.contains(vote3));
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
