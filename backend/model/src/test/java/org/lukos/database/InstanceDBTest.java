package org.lukos.database;

import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Method to test methods in InstanceDB
 *
 * @author Lucas Gether-Rønning
 * @since 13/04/22
 */
public class InstanceDBTest extends GameTest {

    
    /** @utp.description Testing the constructor for InstanceDB */
    @Test
    public void constructorTest(){
        new InstanceDB();
    }

    
    /**  @utp.description Testing if it works to add a new instance to the database */
    @Test
    public void addNewInstanceTest() {
        try {
            PreparedStatement queryRead2 = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Instance;");
            ResultSet rs2 = DatabaseConnection.getInstance().readStatement(queryRead2);
            int instanceID = -1;
            if (!rs2.next()) { //resultset empty
                instanceID = InstanceDB.addNewInstance(1, "testGame", 12345);
            }
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Instance WHERE instanceID=?;");
            queryRead.setInt(1, instanceID);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            assertTrue(rs.next()); //there is an element in the database with the newly created instance
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }

    }

    
    /**  @utp.description Testing if the exception works when trying to add a new instance to the database with a name that is longer than the database allows. */
    @Test
    public void addNewInstanceExceptionTest() {
        Class expected = SQLException.class;
        try {
            int iid = InstanceDB.addNewInstance(1, "namenamenamenamenamenamenamenamenamename", 123); //too long of a game-name, should throw exception
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));

        }

    }

    
    /**  @utp.description Testing if it works to delete an instance in the database when knowing its ID */
    @Test
    public void deleteInstanceByIIDTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "deleteGame", 123);
            InstanceDB.deleteInstanceByIID(instanceID);
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Instance WHERE instanceID=?;");
            queryRead.setInt(1, instanceID);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            assertFalse(rs.next()); //resultset should be empty after deletion
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /**  @utp.description Testing if the method to generate a list of all active instances works */
    @Test
    public void generateInstanceIDListTest() {
        try {
            int instanceID1 = InstanceDB.addNewInstance(2, "listGame1", 123987);
            int instanceID2 = InstanceDB.addNewInstance(3, "listGame2", 123987);
            List<Integer> instances = InstanceDB.generateInstanceIDList();
            assertEquals(instances.size(), 2);
            assertTrue(instances.contains(instanceID1));
            assertTrue(instances.contains(instanceID2));
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /**  @utp.description Testing whether an instance can be found by its ID */
    @Test
    public void findInstanceByIDTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "findGame", 123987);
            ResultSet rs = InstanceDB.findInstanceByID(instanceID);
            assertTrue(rs.next()); //resultset should not be empty
            assertEquals(rs.getString("name"), "findGame");
            assertEquals(rs.getInt("SEED"), 123987);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /**  @utp.description Testing that modifying the game-day of an instance works */
    @Test
    public void modifyDayTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyDayGame", 2345);
            InstanceDB.modifyDay(instanceID, 69);
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Instance WHERE instanceID=?;");
            queryRead.setInt(1, instanceID);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            rs.next();
            assertEquals(rs.getInt("day"), 69);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /**  @utp.description Testing that modifying the game-dayphase of an instance works */
    @Test
    public void modifyPhaseTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            InstanceDB.modifyPhase(instanceID, DayPhase.NIGHT);
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Instance WHERE instanceID=?;");
            queryRead.setInt(1, instanceID);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            rs.next();
            String out = rs.getString("dayPhase");
            System.out.println(out);
            assertEquals(out, "NIGHT");
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that all alive players of an instance are fetched in getAlivePlayers */
    @Test
    public void getAlivePlayersTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            int userID2 = UserDB.createUser("iss2", "aliveSub2", "alive2");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID2));
            List<PlayerIdentifier> alive = InstanceDB.getAlivePlayers(instanceID);
            assertEquals(alive.get(0).userID(), userID1);
            assertEquals(alive.get(1).userID(), userID2);
            assertEquals(alive.get(0).instanceID(), instanceID);
            assertEquals(alive.get(1).instanceID(), instanceID);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that isAlivePlayers correctly returns true when a given player is alive */
    @Test
    public void isAlivePlayerTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            boolean alive = InstanceDB.isAlivePlayer(new PlayerIdentifier(instanceID, userID1));
            assertTrue(alive);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that it works to set a player to be alive again after they have been killed */
    @Test
    public void setAlivePlayerTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.killPlayer(new PlayerIdentifier(instanceID, userID1));
            List<PlayerIdentifier> setAlive = new ArrayList<>();
            setAlive.add(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.setAlivePlayer(instanceID, setAlive);
            boolean alive = InstanceDB.isAlivePlayer(new PlayerIdentifier(instanceID, userID1));
            assertTrue(alive);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that the exception thrown with mismatching instanceID and players instanceID is right */
    @Test
    public void setAlivePlayerExceptionTest() {
        Class expected = NoSuchPlayerException.class;
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.killPlayer(new PlayerIdentifier(instanceID, userID1));
            List<PlayerIdentifier> setAlive = new ArrayList<>();
            setAlive.add(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.setAlivePlayer(instanceID+5, setAlive);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that it properly works to kill a player, that they do not appear as alive anymore */
    @Test
    public void killPlayerTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.killPlayer(new PlayerIdentifier(instanceID, userID1));
            boolean alive = InstanceDB.isAlivePlayer(new PlayerIdentifier(instanceID, userID1));
            assertFalse(alive);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that it works to revive a player after they have been killed */
    @Test
    public void revivePlayerTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.killPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.revivePlayer(new PlayerIdentifier(instanceID, userID1));
            boolean alive = InstanceDB.isAlivePlayer(new PlayerIdentifier(instanceID, userID1));
            assertTrue(alive);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that getDeadPlayers properly retrieve all dead players in an instance */
    @Test
    public void getDeadPlayersTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.killPlayer(new PlayerIdentifier(instanceID, userID1));
            List<PlayerIdentifier> deadPlayers = InstanceDB.getDeadPlayers(instanceID);
            assertEquals(deadPlayers.get(0).userID(), userID1);
            assertEquals(deadPlayers.get(0).instanceID(), instanceID);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that the name of an instance can be fetched correctly */
    @Test
    public void getGameNameByInstanceIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(2, "gameNameInstance", 12345678);
            String gamename = InstanceDB.getGameNameByInstanceID(iid);
            assertEquals("gameNameInstance", gamename);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that the name of an instance can be fetched correctly */
    @Test
    public void getGameNameByInstanceIDExceptionTest() {
        Class expected = SQLException.class;
        try {
            String gamename = InstanceDB.getGameNameByInstanceID(9999);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing if getting the game-master of an instance is done correctly */
    @Test
    public void getGameMasterTest() {
        try {
            int iid = InstanceDB.addNewInstance(8, "gameMasterInstance", 12345678);
            int gm_id = InstanceDB.getGameMaster(iid);
            assertEquals(8, gm_id);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing if getting the game-master of an invalid instance is done correctly */
    @Test
    public void getGameMasterExceptionTest() {
        Class expected = SQLException.class;
        try {
            int gm_id = InstanceDB.getGameMaster(999999);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing if it works like expected to get executed players */
    @Test
    public void getToBeExecutedTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.modifyExecuted(instanceID, new PlayerIdentifier(instanceID, userID1), true);
            List<PlayerIdentifier> toBeExecuted = InstanceDB.getToBeExecuted(instanceID);
            assertEquals(toBeExecuted.get(0).userID(), userID1);
            assertEquals(toBeExecuted.get(0).instanceID(), instanceID);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing if it works like expected to set a player to be executed */
    @Test
    public void modifyExecutedTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "modifyPhaseGame", 23456);
            int userID1 = UserDB.createUser("iss", "aliveSub", "alive");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.modifyExecuted(instanceID, new PlayerIdentifier(instanceID, userID1), true);
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT toBeExecuted FROM Players WHERE instanceID=? AND userID=?;");
            queryRead.setInt(1, instanceID);
            queryRead.setInt(2, userID1);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            rs.next();
            assertTrue(rs.getBoolean("toBeExecuted"));
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that it works as expected to get all players from a given instance */
    @Test
    public void getPlayersTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "getPlayersGame", 23456);
            int userID1 = UserDB.createUser("iss", "playerSub", "player");
            int userID2 = UserDB.createUser("iss2", "playerSub2", "player2");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID2));
            List<PlayerIdentifier> players = InstanceDB.getPlayers(instanceID);
            assertEquals(players.get(0).userID(), userID1);
            assertEquals(players.get(1).userID(), userID2);
            assertEquals(players.get(0).instanceID(), instanceID);
            assertEquals(players.get(1).instanceID(), instanceID);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that the gamemaster for an instance can be set correctly */
    @Test
    public void setGameMasterByInstanceTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "setGameMasterInstance", 12345678);
            InstanceDB.setGameMasterByInstance(iid, 2);
            int gm_id = InstanceDB.getGameMaster(iid);
            assertEquals(2, gm_id);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that it initializing the instancestate sets all players of an instance to be alive */
    @Test
    public void initializeInstanceStateTest() {
        try {
            int instanceID = InstanceDB.addNewInstance(1, "getPlayersGame", 23456);
            int userID1 = UserDB.createUser("iss", "playerSub", "player");
            int userID2 = UserDB.createUser("iss2", "playerSub2", "player2");
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID1));
            PlayerDB.addNewPlayer(new PlayerIdentifier(instanceID, userID2));
            InstanceDB.killPlayer(new PlayerIdentifier(instanceID, userID1));
            InstanceDB.killPlayer(new PlayerIdentifier(instanceID, userID2));
            InstanceDB.initializeInstanceState(instanceID);
            List<PlayerIdentifier> players = InstanceDB.getAlivePlayers(instanceID);
            assertEquals(2, players.size());
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }
}
