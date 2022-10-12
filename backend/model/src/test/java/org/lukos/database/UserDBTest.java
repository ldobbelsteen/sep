package org.lukos.database;

import org.junit.jupiter.api.Test;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the methods of UserDB
 *
 * @author Lucas Gether-Rønning
 * @since 08/04/22
 */
public class UserDBTest {

    
    /** @utp.description Testing the constructor for UserDB */
    @Test
    public void constructorTest(){
        new UserDB();
    }

    
    /** @utp.description Test to see if a user is inserted into the database correctly */
    @Test
    public void createUserTest() {
        try {
            UserDB.createUser("issuer", "sub", "testingUsername");
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT userID FROM Users WHERE username=?;");
            queryRead.setString(1, "testingUsername"); // setting username checked to the one just added
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            assertTrue(rs.next()); // test passes if resultset is not empty
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Test to see if getIfExistUser works when the user already exists */
    @Test
    public void getIfExistUserExistsTest() {
        try {
            // creating a user with testing values for issuer and sub
            int userID = UserDB.createUser("issuerTest", "subTest", "getIfExistUserExistsTestUsername");
            // calling getIfExistUser with the same values for issuer and sub as at creation above
            int userIDtest = UserDB.getIfExistUser(new IssuerSub("issuerTest", "subTest"));
            // since user already exists, getIfExistUser should return the same ID as createUser
            assertEquals(userID, userIDtest);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Test to see if getIfExistUser works when the user does not already exist (exception thrown) */
    @Test
    public void getIfExistUserNotExistsTest() throws SQLException, NoSuchUserException {
        Class expected = NoSuchUserException.class;
        try {
            int userIDtest = UserDB.getIfExistUser(new IssuerSub("issuerNONEXISTANT", "subNONEXISTANT"));
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Test to check if existUser finds a user we know exists */
    @Test
    public void existUserTest() {
        try {
            UserDB.createUser("iss1", "sub1", "existUsername");
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT userID FROM Users WHERE username=?;");
            queryRead.setString(1, "existUsername");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            int userID = -1;
            if (rs.next()) {
                userID = rs.getInt("userID");
            }
            assertTrue(UserDB.existUser(userID));
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Test to check if existUser finds a user we know exists */
    @Test
    public void notExistUserTest() {
        try {
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT userID FROM Users WHERE userID=?;");
            queryRead.setInt(1, 9999);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            assertFalse(rs.next()); // resultset should be empty
            assertFalse(UserDB.existUser(9999));
        } catch (SQLException e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Test to see if a user is deleted properly */
    @Test
    public void deleteUserByUIDTest() {
        try {
            UserDB.createUser("iss2", "sub2", "deleteUsername");
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT userID FROM Users WHERE username=?;");
            queryRead.setString(1, "deleteUsername");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            int userID = -1;
            if (rs.next()) {
                userID = rs.getInt("userID");
            }
            UserDB.deleteUserByUID(userID);
            PreparedStatement queryRead2 = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT userID FROM Users WHERE userID=?;");
            queryRead2.setInt(1, userID);
            ResultSet rs2 = DatabaseConnection.getInstance().readStatement(queryRead2);
            assertFalse(rs2.next()); //resultset should be empty
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Test to see if the right user is found by findUserByID */
    @Test
    public void findUserByIDTest() {
        try {
            int userID = UserDB.createUser("iss3", "sub3", "findUsername");
            assertEquals(UserDB.findUserByID(userID).getString("username"), "findUsername");
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Test to see if the right user is found by findUserByID */
    @Test
    public void findUserByIDNoUserTest() {
        Class expected = SQLException.class;
        try {
            int userID = UserDB.createUser("iss0", "sub0", "findUsername2");
            UserDB.findUserByID(userID+10);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Test to check that UserStats are added to db correctly */
    @Test
    public void addUserRoleTest() {
        try {
            int userID = UserDB.createUser("iss4", "sub4", "userRoleUsername");
            UserDB.addUserRole(userID, 1, 1,"Werewolf");
            PreparedStatement queryRead2 = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM UserStats WHERE userID=?;");
            queryRead2.setInt(1, userID);
            ResultSet rs2 = DatabaseConnection.getInstance().readStatement(queryRead2);
            assertTrue(rs2.next());
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that a username is set correctly for a given id */
    @Test
    public void setUsernameByIDTest() {
        try {
            int userID = UserDB.createUser("iss5", "sub5", "usernameUsername");
            UserDB.setUsernameByID(userID, "newlyChangedUsernameHere");
            assertEquals(UserDB.findUserByID(userID).getString("username"), "newlyChangedUsernameHere");
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that a file is in fact created from getAllUserInfo (should probably be tested further) */
    @Test
    public void getAllUserInfoTest() {
        try {
            int userID = UserDB.createUser("iss6", "sub6", "infoUsername");
            File file = UserDB.getAllUserInfo(userID);
            assertNotNull(file);
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that the method takes a list of user and deletes them when called */
    @Test
    public void deleteUsersAfterInstanceEndTest() {
        try {
            int uid = UserDB.createUser("i", "s", "u");
            List<Integer> delete = new ArrayList<>();
            delete.add(uid);
            UserDB.deleteUsersAfterInstanceEnd(delete);
            ResultSet rs = UserDB.findUserByID(uid);
            assertFalse(rs.next());
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }

    }

    
    /** @utp.description Testing that setting a user to be deleted works */
    @Test
    public void setUserDeletionTest() {
        try {
            int userID = UserDB.createUser("isss7", "suub7", "setDeletionUsername");
            int iid = InstanceDB.addNewInstance(userID, "name1", 123);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID));
            UserDB.setUserDeletion(userID, true);
            ResultSet rs2 = UserDB.findUserByID(userID);
            if (rs2.next()) {
                assertTrue(rs2.getBoolean("toBeDeleted"));
            }
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that setting a user to be deleted deletes the user immediately if they are not in a game */
    @Test
    public void setUserDeletionNotInGameTest() {
        try {
            int userID = UserDB.createUser("iss77", "sub7", "setDeletionUsername");
            int iid = InstanceDB.addNewInstance(userID, "name2", 123);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID));
            UserDB.setUserDeletion(userID, true);
            ResultSet rs2 = UserDB.findUserByID(userID);
            assertFalse(rs2.next());
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that setting a user to be deleted as false works */
    @Test
    public void setUserDeletionFalseTest() {
        try {
            int userID = UserDB.createUser("isws7", "sub7", "setDeletionUsername");
            int iid = InstanceDB.addNewInstance(userID, "name3", 123);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, userID));
            UserDB.setUserDeletion(userID, false);
            ResultSet rs2 = UserDB.findUserByID(userID);
            if (rs2.next()) {
                assertFalse(rs2.getBoolean("toBeDeleted"));
            }
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that setting a user to be deleted as false works */
    @Test
    public void setUserDeletionFalseNotInGameTest() {
        try {
            int userID = UserDB.createUser("isws799", "sub799", "setDeletionUsername99");
            UserDB.setUserDeletion(userID, false);
            ResultSet rs2 = UserDB.findUserByID(userID);
            if (rs2.next()) {
                assertFalse(rs2.getBoolean("toBeDeleted"));
            }
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that it works to increment a users games played by their ID */
    @Test
    public void incrementGamesPlayedByUserIDTest() {
        try {
            int userID = UserDB.createUser("iss8", "sub8", "incrementStatsUsername");
            UserDB.incrementGamesPlayedByUserID(userID, "role", true);
            PreparedStatement queryRead2 = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM UserStats WHERE userID=? and purpose=?;");
            queryRead2.setInt(1, userID);
            queryRead2.setString(2, "role");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead2);
            if (rs.next()) {
                int gamesPlayed = rs.getInt("gamesPlayed");
                int wins = rs.getInt("wins");
                assertEquals(gamesPlayed, 1); //games played should be 1 as this is a new user
                assertEquals(1, wins);
            }
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that it works properly when this method is called and a user has not won the game they were in. */
    @Test
    public void dontIncrementGamesPlayedByUserIDTest() {
        try {
            int userID = UserDB.createUser("iss9", "sub9", "dontIncrementStatsUsername");
            UserDB.incrementGamesPlayedByUserID(userID, "role1", false);
            PreparedStatement queryRead2 = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM UserStats WHERE userID=? and purpose=?;");
            queryRead2.setInt(1, userID);
            queryRead2.setString(2, "role1");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead2);
            if (rs.next()) {
                int gamesPlayed = rs.getInt("gamesPlayed");
                int wins = rs.getInt("wins");
                assertEquals(1, gamesPlayed); //games played should be 1 as this is a new user
                assertEquals(0, wins);
            }
        } catch (Exception e){
            fail("Exception thrown: " + e);
        }
    }
}
