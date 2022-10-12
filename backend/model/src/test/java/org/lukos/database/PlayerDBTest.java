package org.lukos.database;

import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.notes.Deathnote;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the methods of PlayerDB
 *
 * @author Lucas Gether-Rønning
 * @since 13/04/22
 */
public class PlayerDBTest extends GameTest {

    
    /** @utp.description Testing the constructor for PlayerDB */
    @Test
    public void constructorTest(){
        new PlayerDB();
    }

    
    /** @utp.description Testing that getting the deathnote of a player works correctly */
    @Test
    public void getDeathNotePlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            Deathnote deathnote = PlayerDB.getDeathNotePlayerByID(new PlayerIdentifier(iid, uid));
            assertNull(deathnote.getContent());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that getting the deathnote of a player throws exception with unknown user and instance */
    @Test
    public void getDeathNotePlayerByIDExceptionTest() {
        Class expected = SQLException.class;
        try {
            PlayerDB.addNewPlayer(new PlayerIdentifier(99999, 34726332));
            Deathnote deathnote = PlayerDB.getDeathNotePlayerByID(new PlayerIdentifier(99999, 34726332));
        } catch (Exception e){
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that getting the deathnote of a player throws NoSuchPlayerException with unknown player */
    @Test
    public void getDeathNotePlayerByIDNoSuchPlayerExceptionTest() {
        Class expected = NoSuchPlayerException.class;
        try {
            int uid = UserDB.createUser("s", "w", "w");
            int iid = InstanceDB.addNewInstance(1, "wwwwe", 1345);
            //PlayerDB.addNewPlayer(new PlayerIdentifier(99999, 34726332));
            Deathnote deathnote = PlayerDB.getDeathNotePlayerByID(new PlayerIdentifier(iid, uid));
        } catch (Exception e){
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that setting the deathnote of a player works correctly */
    @Test
    public void setDeathNotePlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            PlayerDB.setDeathNotePlayerByID(new PlayerIdentifier(iid, uid), new Deathnote("new deathnote", true));
            Deathnote deathnote = PlayerDB.getDeathNotePlayerByID(new PlayerIdentifier(iid, uid));
            assertEquals("new deathnote", deathnote.getContent());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that a player is added properly by their playeridentifier */
    @Test
    public void addNewPlayerTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Players WHERE userID=?;");
            queryRead.setInt(1, uid);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            rs.next();
            int iid_test = rs.getInt("instanceID");
            assertEquals(iid, iid_test);
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that a player is deleted properly by their ID*/
    @Test
    public void deletePlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            PlayerDB.deletePlayerByID(uid, iid);
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Players WHERE userID=?;");
            queryRead.setInt(1, uid);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            assertFalse(rs.next());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that a player is fetched properly by their id */
    @Test
    public void getPlayerFromUserByID() {
        try {
            int iid = InstanceDB.addNewInstance(1, "getPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "getPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            PlayerIdentifier pi = PlayerDB.getPlayerFromUserByID(uid);
            assertEquals(uid, pi.userID());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that visiting a location works as expected */
    @Test
    public void visitLocation() {
        try {
            int iid = InstanceDB.addNewInstance(1, "getPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "getPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            int lid = LocationDB.createNewLocation(iid);
            PlayerDB.visitLocation(lid, new PlayerIdentifier(iid, uid));
            List<PlayerIdentifier> players = PlayerDB.getPlayersAtLocation(lid);
            assertEquals(uid, players.get(0).userID());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that getting all players at a given location works */
    @Test
    public void getPlayersAtLocation() {
        try {
            int uid1 = UserDB.createUser("iss1", "suuub1", "getPlayer1");
            int uid2 = UserDB.createUser("iss2", "suuub2", "getPlayer2");
            int uid3 = UserDB.createUser("iss3", "suuub3", "getPlayer3");
            int iid = InstanceDB.addNewInstance(1, "getPlayer2", 222);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid2));
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid3));
            int lid = LocationDB.createNewLocation(iid);
            PlayerDB.visitLocation(lid, new PlayerIdentifier(iid, uid1));
            PlayerDB.visitLocation(lid, new PlayerIdentifier(iid, uid2));
            PlayerDB.visitLocation(lid, new PlayerIdentifier(iid, uid3));
            List<PlayerIdentifier> players = PlayerDB.getPlayersAtLocation(lid);
            assertEquals(uid1, players.get(0).userID());
            //assertEquals(uid2, players.get(1).userID());
            assertEquals(3, players.size());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing if getting the id of the player owning a house gives the right player */
    @Test
    public void getOwnerByHouseID() {
        try {
            int iid = InstanceDB.addNewInstance(1, "getPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "getPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            int lid = LocationDB.createNewLocation(iid);
            HouseDB.setHousePlayerByID(new PlayerIdentifier(iid, uid), lid);
            int owner = PlayerDB.getOwnerByHouseID(lid);
            assertEquals(uid, owner);
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing if it works to get all protected players of an instance */
    @Test
    public void getProtectedPlayers() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            PlayerDB.updateProtected(new PlayerIdentifier(iid, uid), true);
            List<PlayerIdentifier> protectedPlayers = PlayerDB.getProtectedPlayers(iid);
            assertEquals(uid, protectedPlayers.get(0).userID());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing if it works to set the protected-status of a player to true and back */
    @Test
    public void updateProtected() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            PlayerDB.updateProtected(new PlayerIdentifier(iid, uid), true);
            List<PlayerIdentifier> protectedPlayers = PlayerDB.getProtectedPlayers(iid);
            assertEquals(uid, protectedPlayers.get(0).userID());
            PlayerDB.updateProtected(new PlayerIdentifier(iid, uid), false);
            List<PlayerIdentifier> protectedPlayers2 = PlayerDB.getProtectedPlayers(iid);
            assertEquals(0, protectedPlayers2.size());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }
}
