package org.lukos.model.voting;

import org.junit.jupiter.api.Test;
import org.lukos.model.exceptions.voting.*;
import org.lukos.model.user.PlayerIdentifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link VotePermissionChecker}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-04-2022
 */
public class VotePermissionCheckerTest {

    /** @utp.description Tests whether a player can submit a vote when adhering to all voting rules. */
    @Test
    public void noExceptionTest() {
        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        List<PlayerIdentifier> allowed = new ArrayList<>();
        allowed.add(henkID);
        allowed.add(janID);

        Ballot ballot1 = new Ballot(henkID, janID);
        List<Ballot> ballots = new ArrayList<>();

        try {
            VotePermissionChecker.submitVotePermission(allowed, ballots, henkID, ballot1, true, false);
        } catch (Exception e) {
            fail("Should not thrown an exception.");
        }
    }

    /** @utp.description Tests whether a player can submit a vote when the player is not allowed to vote. */
    @Test
    public void notAllowedToVoteExceptionTest() {
        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        List<PlayerIdentifier> allowed = new ArrayList<>();
        allowed.add(janID);

        Ballot ballot1 = new Ballot(henkID, janID);
        List<Ballot> ballots = new ArrayList<>();

        Class<?> expected = NotAllowedToVoteException.class;
        try {
            VotePermissionChecker.submitVotePermission(allowed, ballots, henkID, ballot1, true, false);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether a player can submit a vote even when the target is not allowed to be voted on. */
    @Test
    public void notAllowedTargetExceptionTest() {
        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        List<PlayerIdentifier> allowed = new ArrayList<>();
        allowed.add(henkID);

        Ballot ballot1 = new Ballot(henkID, janID);
        List<Ballot> ballots = new ArrayList<>();

        Class<?> expected = NotAllowedTargetException.class;
        try {
            VotePermissionChecker.submitVotePermission(allowed, ballots, henkID, ballot1, true, false);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether a player can submit a vote when the vote has not started. */
    @Test
    public void voteNotStartedExceptionTest() {
        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        List<PlayerIdentifier> allowed = new ArrayList<>();
        allowed.add(henkID);
        allowed.add(janID);

        Ballot ballot1 = new Ballot(henkID, janID);
        List<Ballot> ballots = new ArrayList<>();

        Class<?> expected = VoteNotStartedException.class;
        try {
            VotePermissionChecker.submitVotePermission(allowed, ballots, henkID, ballot1, false, false);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether a player can submit a vote when the vote is closed. */
    @Test
    public void voteClosedExceptionTest() {
        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        List<PlayerIdentifier> allowed = new ArrayList<>();
        allowed.add(henkID);
        allowed.add(janID);

        Ballot ballot1 = new Ballot(henkID, janID);
        List<Ballot> ballots = new ArrayList<>();

        Class<?> expected = VoteClosedException.class;
        try {
            VotePermissionChecker.submitVotePermission(allowed, ballots, henkID, ballot1, true, true);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether a player can commit voter fraud. */
    @Test
    public void voterFraudExceptionTest() {
        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        List<PlayerIdentifier> allowed = new ArrayList<>();
        allowed.add(henkID);
        allowed.add(janID);

        Ballot ballot1 = new Ballot(henkID, janID);
        List<Ballot> ballots = new ArrayList<>();
        ballots.add(ballot1);

        Class<?> expected = VoterFraudException.class;
        try {
            VotePermissionChecker.submitVotePermission(allowed, ballots, janID, ballot1, true, false);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * Tests whether a player has already voted.
     *
     * @utp.description Tests whether a player has already voted.
     */
    @Test
    public void alreadyVotedExceptionTest1() {
        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        List<PlayerIdentifier> allowed = new ArrayList<>();
        allowed.add(henkID);
        allowed.add(janID);

        Ballot ballot1 = new Ballot(henkID, janID);
        List<Ballot> ballots = new ArrayList<>();
        ballots.add(ballot1);

        Class<?> expected = AlreadyVotedException.class;
        try {
            VotePermissionChecker.submitVotePermission(allowed, ballots, henkID, ballot1, true, false);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * Tests whether a player has already voted.
     *
     * @utp.description Tests whether a player has already voted.
     */
    @Test
    public void alreadyVotedExceptionTest2() {
        PlayerIdentifier henkID = new PlayerIdentifier(1, 1);
        PlayerIdentifier janID = new PlayerIdentifier(1, 2);

        List<PlayerIdentifier> allowed = new ArrayList<>();
        allowed.add(henkID);
        allowed.add(janID);

        Ballot ballot1 = new Ballot(henkID, janID);
        Ballot ballot2 = new Ballot(henkID, henkID);
        List<Ballot> ballots = new ArrayList<>();
        ballots.add(ballot1);

        Class<?> expected = AlreadyVotedException.class;
        try {
            VotePermissionChecker.submitVotePermission(allowed, ballots, henkID, ballot2, true, false);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }
}
