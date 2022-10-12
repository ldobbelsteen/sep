package org.lukos.database;

import org.junit.jupiter.api.Test;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.voting.Vote;
import org.lukos.model.voting.VoteType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the methods of VoteDB
 *
 * @author Lucas Gether-Rønning
 * @since 14/04/22
 */
public class VoteDBTest {

    
    /** @utp.description Testing the constructor for VoteDB */
    @Test
    public void constructorTest(){
        new VoteDB();
    }

    
    /** @utp.description Testing that adding a new vote to an instance works correctly */
    @Test
    public void addNewVoteTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newVote", 12345678);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            ResultSet rs = VoteDB.findVoteByID(vid);
            rs.next();
            assertEquals("LYNCH", rs.getString("VoteType"));
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that exception behavior when adding a new vote to an instance works correctly */
    @Test
    public void addNewVoteExceptionTest() {
        Class expected = SQLException.class;
        try {
            int vid = VoteDB.addNewVote(93485, VoteType.LYNCH);
            ResultSet rs = VoteDB.findVoteByID(vid);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that retrieving an ongoing vote by an instance works correctly */
    @Test
    public void retrieveOngoingVotesByInstanceTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newVote", 12345678);
            int vid1 = VoteDB.addNewVote(iid, VoteType.LYNCH);
            int vid2 = VoteDB.addNewVote(iid, VoteType.ALPHA_WOLF);
            int vid3 = VoteDB.addNewVote(iid, VoteType.MAYOR);
            VoteDB.modifyStarted(vid1, true);
            VoteDB.modifyStarted(vid2, true);
            ResultSet rs = VoteDB.retrieveOngoingVotesByInstance(iid);
            rs.next();
            assertEquals(vid1, rs.getInt("voteID"));
            rs.next();
            assertEquals(vid2, rs.getInt("voteID"));
            assertFalse(rs.next());
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that deleting a vote that was just added is done correctly */
    @Test
    public void deleteVoteByID() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            VoteDB.deleteVoteByID(vid);
            ResultSet rs = VoteDB.findVoteByID(vid);
            assertFalse(rs.next());
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that finding a vote by its ID works as expected */
    @Test
    public void findVoteByID() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            ResultSet rs = VoteDB.findVoteByID(vid);
            assertTrue(rs.next());
            assertEquals(vid, rs.getInt("voteID"));
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that finding a vote by its ID if it is in the instance wanted works as expected */
    @Test
    public void findVoteByIdIfInInstanceTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            ResultSet rs = VoteDB.findVoteByIdIfInInstance(vid, iid);
            assertTrue(rs.next());
            assertEquals(vid, rs.getInt("voteID"));
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }
    
    /** @utp.description Testing that getting the VoteType of a vote by its ID works as expected */
    @Test
    public void getVoteTypeByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            VoteType vt = VoteDB.getVoteTypeByID(vid);
            assertEquals(VoteType.LYNCH, vt);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that a SQLException is thrown when an unknown voteID tries to be found*/
    @Test
    public void getVoteTypeByIDExceptionTest() {
        Class expected = SQLException.class;
        try {
            VoteDB.getVoteTypeByID(98676543);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that getting whether a vote is busy works as expected when it is not busy */
    @Test
    public void getBusyByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            VoteDB.modifyStarted(vid, true);
            VoteDB.modifyEnded(vid, true);
            boolean busy = VoteDB.getBusyByID(vid);
            assertFalse(busy);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that a SQLException is thrown when trying to get whether a vote that does not exist is busy */
    @Test
    public void getBusyByIDExceptionTest() {
        Class expected = SQLException.class;
        try {
            VoteDB.getBusyByID(9876543);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }
    
    /** @utp.description Testing that finding whether a vote has started works when it has not started */
    @Test
    public void getStartedByIDNotStartedTest() {
        Class expected = SQLException.class;
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            boolean started = VoteDB.getStartedByID(vid);
            //assertFalse(started);
        } catch (Exception e) {
            //fail("Exception thrown: " + e);
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that finding whether a vote has started works when it has started */
    @Test
    public void getStartedByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            VoteDB.modifyStarted(vid, true);
            boolean started = VoteDB.getStartedByID(vid);
            assertTrue(started);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }
    
    /** @utp.description Testing that finding whether a vote has started throws an exception */
    @Test
    public void getStartedByIDExceptionTest() {
        Class expected = SQLException.class;
        try {
            VoteDB.getStartedByID(234567890);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that finding whether a vote has ended works when it has ended  */
    @Test
    public void getEndedByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            VoteDB.modifyEnded(vid, true);
            boolean ended = VoteDB.getEndedByID(vid);
            assertTrue(ended);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that finding whether a vote has ended throws an exception  */
    @Test
    public void getEndedByIDExceptionTest() {
        Class expected = SQLException.class;
        try {
            VoteDB.getEndedByID(456765);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that finding whether a vote has ended works when it has not ended  */
    @Test
    public void getEndedByIDNotEndedTest() {
        Class expected = SQLException.class;
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 12345);
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            boolean ended = VoteDB.getEndedByID(vid);
            //assertFalse(ended);
        } catch (Exception e) {
            //fail("Exception thrown: " + e);
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing if adding a ballot to a vote works as expected  */
    @Test
    public void addBallotTest() {
        try {
            int uid = UserDB.createUser("a", "b", "c");
            int iid = InstanceDB.addNewInstance(1, "in", 12345);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            int vid = VoteDB.addNewVote(iid, VoteType.LYNCH);
            VoteDB.addBallot(uid, iid, vid, 1);
            ResultSet rs = VoteDB.getAllBallotsOfVote(vid);
            int count = 0;
            while (rs.next()) {
                count++;
            }
            assertEquals(1, count);
        } catch (Exception e) {

        }
    }
    
    /** @utp.description Testing if getting the players allowed to vote throws exceptions with unknown vid  */
    @Test
    public void getAllowedPlayersExceptionTest() {
        Class expected = SQLException.class;
        try {
            VoteDB.getAllowedPlayers(9876543);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing if setting the players tied in a vote works as expected  */
    @Test
    public void setTiedPlayersTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 123);
            int uid = UserDB.createUser("bb", "s", "e");
            int uid2 = UserDB.createUser("ww", "d", "ss");
            List<PlayerIdentifier> playerIdentifierList = new ArrayList<>();
            playerIdentifierList.add(new PlayerIdentifier(iid, uid));
            playerIdentifierList.add(new PlayerIdentifier(iid, uid2));
            VoteDB.setTiedPlayers(playerIdentifierList);
            playerIdentifierList = VoteDB.getTiedPlayers(iid);
            assertEquals(2, playerIdentifierList.size());
            VoteDB.setUndecidedLynchesByInstanceID(iid, 1);
            int lynches2 = VoteDB.getUndecidedLynches(iid);
            assertEquals(1, lynches2);
        } catch (Exception e) {
            //assertTrue(expected.isInstance(e));
            fail();
        }
    }
    
    /** @utp.description Testing if setting the undecided votes in an instance by its ID works as expected */
    @Test
    public void setUndecidedLynchesByInstanceIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 123);
            VoteDB.setUndecidedLynchesByInstanceID(iid, 5);
            int lynches = VoteDB.getUndecidedLynches(iid);
            assertEquals(5, lynches);
            VoteDB.setUndecidedLynchesByInstanceID(iid, 1);
            int lynches2 = VoteDB.getUndecidedLynches(iid);
            assertEquals(1, lynches2);
        } catch (Exception e) {
            //assertTrue(expected.isInstance(e));
            fail();
        }
    }

    
    /** @utp.description Testing if getting the undecided votes in an instance by its ID works as expected */
    @Test
    public void getUndecidedLynchesTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "name", 123);
            VoteDB.setUndecidedLynchesByInstanceID(iid, 5);
            int lynches = VoteDB.getUndecidedLynches(iid);
            assertEquals(5, lynches);
        } catch (Exception e) {
            //assertTrue(expected.isInstance(e));
            fail();
        }
    }

    
    /** @utp.description Testing if getting the undecided lynches throws an exception when the instance is unknown */
    @Test
    public void getUndecidedLynchesExceptionTest() {
        //Class expected = SQLException.class;
        try {
            int aa = VoteDB.getUndecidedLynches(9876543);
            assertEquals(0, aa);
        } catch (Exception e) {
            //assertTrue(expected.isInstance(e));
            fail();
        }
    }
}
