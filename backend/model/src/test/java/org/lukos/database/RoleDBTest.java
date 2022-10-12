package org.lukos.database;

import org.junit.jupiter.api.Test;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.jobs.Mayor;
import org.lukos.model.rolesystem.roles.doubleroles.Jester;
import org.lukos.model.rolesystem.roles.mainroles.GuardianAngel;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the methods of RoleDB
 *
 * @author Lucas Gether-Rønning
 * @since 19/04/22
 */
public class RoleDBTest {

    
    /** @utp.description Testing the constructor for RoleDB */
    @Test
    public void constructorTest(){
        new RoleDB();
    }

    
    /** @utp.description Testing that setting the main role of a player works by setting the player to be a GuardianAngel */
    @Test
    public void setMainRolePlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.setMainRolePlayerByID(new PlayerIdentifier(iid, uid), new GuardianAngel());
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Roles WHERE userID=? and purposeType=?;");
            queryRead.setInt(1, uid);
            queryRead.setString(2, "mainRole");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            rs.next();
            assertEquals(rs.getString("purpose"), "GuardianAngel");
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that getting the main role of a player works */
    @Test
    public void getMainRolePlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.setMainRolePlayerByID(new PlayerIdentifier(iid, uid), new GuardianAngel());
            MainRole role = RoleDB.getMainRolePlayerByID(new PlayerIdentifier(iid, uid));
            assertEquals(role.getClass(), GuardianAngel.class);
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that getting the double roles of a player works */
    @Test
    public void getDoubleRolesPlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.addDoubleRolePlayerByID(new PlayerIdentifier(iid, uid), new Jester());
            List<DoubleRole> role = RoleDB.getDoubleRolesPlayerByID(new PlayerIdentifier(iid, uid));
            assertEquals(role.get(0).getClass(), Jester.class);
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that getting the jobs of a player works */
    @Test
    public void getJobsPlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.addJobPlayerByID(new PlayerIdentifier(iid, uid), new Mayor());
            List<Job> role = RoleDB.getJobsPlayerByID(new PlayerIdentifier(iid, uid));
            assertEquals(role.get(0).getClass(), Mayor.class);
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that setting the double role of a player works */
    @Test
    public void addDoubleRolePlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.addDoubleRolePlayerByID(new PlayerIdentifier(iid, uid), new Jester());
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Roles WHERE userID=? and purposeType=?;");
            queryRead.setInt(1, uid);
            queryRead.setString(2, "doubleRole");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            rs.next();
            assertEquals(rs.getString("purpose"), "Jester");
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that removing the double role of a player works */
    @Test
    public void removeDoubleRolePlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.addDoubleRolePlayerByID(new PlayerIdentifier(iid, uid), new Jester());
            RoleDB.removeDoubleRolePlayerByID(new PlayerIdentifier(iid, uid), new Jester());
            List<DoubleRole> roles = RoleDB.getDoubleRolesPlayerByID(new PlayerIdentifier(iid, uid));
            assertEquals(0, roles.size());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that setting the job of a player works */
    @Test
    public void addJobPlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.addJobPlayerByID(new PlayerIdentifier(iid, uid), new Mayor());
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Roles WHERE userID=? and purposeType=?;");
            queryRead.setInt(1, uid);
            queryRead.setString(2, "job");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            rs.next();
            assertEquals(rs.getString("purpose"), "Mayor");
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that removing the job of a player works */
    @Test
    public void removeJobPlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.addJobPlayerByID(new PlayerIdentifier(iid, uid), new Mayor());
            RoleDB.removeJobPlayerByID(new PlayerIdentifier(iid, uid), new Mayor());
            List<Job> roles = RoleDB.getJobsPlayerByID(new PlayerIdentifier(iid, uid));
            assertEquals(0, roles.size());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that removing the purpose of a player works in the case of removing a job*/
    @Test
    public void removePurposePlayerByIDTest() {
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.addJobPlayerByID(new PlayerIdentifier(iid, uid), new Mayor());
            RoleDB.removePurposePlayerByID(new PlayerIdentifier(iid, uid), new Mayor());
            List<Job> roles = RoleDB.getJobsPlayerByID(new PlayerIdentifier(iid, uid));
            assertEquals(0, roles.size());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that removing the purpose of a player does not works in the case of removing a main role*/
    @Test
    public void removePurposeMainRolePlayerByIDTest() {
        Class expected = SQLException.class;
        try {
            int iid = InstanceDB.addNewInstance(1, "newPlayer", 222);
            int uid = UserDB.createUser("iss", "suuub", "newPlayer");
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            RoleDB.setMainRolePlayerByID(new PlayerIdentifier(iid, uid), new GuardianAngel());
            RoleDB.removePurposePlayerByID(new PlayerIdentifier(iid, uid), new GuardianAngel());
        } catch (Exception e){
            assertTrue(expected.isInstance(e));
        }
    }
}
