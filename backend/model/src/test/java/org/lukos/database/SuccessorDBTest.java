package org.lukos.database;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SuccessorDBTest {

    
    /** @utp.description Testing if modifying or creating a successor works by adding a new Mayor-successor and then trying to add it again where it should then update. */
    @Test
    public void modifyOrCreateSuccessor() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            int uid2 = UserDB.createUser("eer", "fjk", "hjfj");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid2));
            SuccessorDB.modifyOrCreateSuccessor(iid, SuccessorType.MAYOR, new PlayerIdentifier(iid, uid));
            PlayerIdentifier pi = SuccessorDB.getSuccessor(iid, SuccessorType.MAYOR);
            assertEquals(uid, pi.userID());
            SuccessorDB.modifyOrCreateSuccessor(iid, SuccessorType.MAYOR, new PlayerIdentifier(iid, uid2));
            PlayerIdentifier pi2 = SuccessorDB.getSuccessor(iid, SuccessorType.MAYOR);
            assertEquals(uid2, pi2.userID());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing if setting a successor works by adding a new Mayor-successor*/
    @Test
    public void getSuccessors() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            int uid2 = UserDB.createUser("eer", "fjk", "hjfj");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid2));
            SuccessorDB.modifyOrCreateSuccessor(iid, SuccessorType.MAYOR, new PlayerIdentifier(iid, uid));
            SuccessorDB.modifyOrCreateSuccessor(iid, SuccessorType.ALPHA_WOLF, new PlayerIdentifier(iid, uid2));
            Map<SuccessorType, PlayerIdentifier> successors = SuccessorDB.getSuccessors(iid);
            assertEquals(2, successors.size());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing if setting a successor has right exception-behavior when instance does not exist*/
    @Test
    public void getSuccessorExceptionTest() {
        Class expected = NoSuchPlayerException.class;
        try {
            SuccessorDB.modifyOrCreateSuccessor(45678, SuccessorType.MAYOR, new PlayerIdentifier(5678, 123));
            PlayerIdentifier pi = SuccessorDB.getSuccessor(5678, SuccessorType.MAYOR);
        } catch (Exception e){
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing if getting a successor works by adding a new Mayor-successor and getting it*/
    @Test
    public void getSuccessorTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            SuccessorDB.modifyOrCreateSuccessor(iid, SuccessorType.MAYOR, new PlayerIdentifier(iid, uid));
            PlayerIdentifier pi = SuccessorDB.getSuccessor(iid, SuccessorType.MAYOR);
            assertEquals(uid, pi.userID());
            assertEquals(iid, pi.instanceID());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing if an exception is thrown when the successor has been removed */
    @Test
    public void removeSuccessorExceptionTest() {
        Class expected = NoSuchPlayerException.class;
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            SuccessorDB.modifyOrCreateSuccessor(iid, SuccessorType.MAYOR, new PlayerIdentifier(iid, uid));
            SuccessorDB.removeSuccessor(iid, SuccessorType.MAYOR);
            PlayerIdentifier pi = SuccessorDB.getSuccessor(iid, SuccessorType.MAYOR);
        } catch (Exception e){
            assertTrue(expected.isInstance(e));
        }
    }
}
