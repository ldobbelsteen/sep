package org.lukos.database;

import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.user.NoSuchUserException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the methods of DatabaseConnection
 *
 * @author Lucas Gether-Rønning
 * @since 08/04/22
 */
public class DatabaseConnectionTest extends GameTest {

    /** @utp.description Makeshift test for what happens when thread is interrupted when making db connection. */
    @Test
    public void constructorExceptionTest() {
        Class expected = InterruptedException.class;
        try{
            Thread.currentThread().interrupt();
            DatabaseConnection.getInstance();
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }
    
    /** @utp.description Testing if getting the instance of DatabaseConnection works. */
    @Test
    public void getInstanceTest() {
        // call twice, they have to be equal because of singleton-design
        assertEquals(DatabaseConnection.getInstance(), DatabaseConnection.getInstance());
    }

    
    /** @utp.description Testing if getting the connection of DatabaseConnection works. */
    @Test
    public void getConnectTest() {
        try {
            // without being initialized, getConnect() should retrieve null
            assertNotNull(DatabaseConnection.getInstance().getConnect());
        } catch (Exception e) {
            fail("Unexpected exception thrown: "+ e);
        }
    }

    
    /** @utp.description Testing if getting the connection of DatabaseConnection works after the connection is closed. */
    @Test
    public void getConnectClosedTest() {
        try {
            DatabaseConnection.getInstance().getConnect().close(); //closing the connection
            assertNotNull(DatabaseConnection.getInstance().getConnect()); // checking that the connection is re-established
        } catch (Exception e) {
            fail("Unexpected exception thrown: "+ e);
        }
    }

    
    /** @utp.description Testing if getting the connection of DatabaseConnection works after the connection is closed and the thread is interrupted. */
    @Test
    public void getConnectClosedExceptionTest() {
        Class expected = InterruptedException.class;
        try {
            DatabaseConnection.getInstance().getConnect().close(); //closing the connection
            Thread.currentThread().interrupt();
            DatabaseConnection.getInstance().getConnect(); // checking if the connection is re-established
            // after interruption, should not get this far in the test
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing if setting the connection works. */
    @Test
    public void connectTest() {
        try {
            DatabaseConnection.getInstance().connect();
            assertNotNull(DatabaseConnection.getInstance().getConnect());
        } catch (Exception e) {
            fail("Unexpected exception thrown: "+ e);
        }
    }

    
    /** @utp.description Testing that the exception for connect in the case of interruption works. */
    @Test
    public void connectExceptionTest() {
        Class expected = InterruptedException.class;
        try {
            Thread.currentThread().interrupt();
            DatabaseConnection.getInstance().connect();
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }
//
//    
//    /** @utp.description Testing that the exception is thrown when connection empty at finally-block */
//    public void connectNullTest() {
//        Class expected = InterruptedException.class;
//        try {
//            Thread.currentThread().interrupt();
//            DatabaseConnection.getInstance().connect();
//        } catch (Exception e) {
//            assertTrue(expected.isInstance(e));
//        }
//    }

    
    /** @utp.description Test to see if the method can take in a preparedstatement and generate a resultset. */
    @Test
    public void readStatementTest() {
        try {
            PreparedStatement query = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Users;");
            ResultSet resultSet = DatabaseConnection.getInstance().readStatement(query);
            assertNotNull(resultSet);
        } catch (SQLException e){
            fail("SQLException thrown: " + e);
        }
    }

    
    /** @utp.description Test to see exception-behavior in readStatement. */
    @Test
    public void readStatementExceptionTest() {
        Class expected = SQLException.class;
        try {
            PreparedStatement query = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM NotATable;");
            DatabaseConnection.getInstance().readStatement(query);
            fail("Should have thrown an error.");
        } catch (SQLException e){
            assertTrue(expected.isInstance(e));
        }
    }


    
    /** @utp.description Test to see if the method can take in a preparedstatement and execute a query. */
    @Test
    public void writeStatementTest() {
        try {
            PreparedStatement queryWrite = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "INSERT INTO Users(sub, issuer, username, wins, losses, last_login, last_logout, playtime, toBeDeleted) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            queryWrite.setString(1, "sub");
            queryWrite.setString(2, "issuer");
            queryWrite.setString(3, "username");
            queryWrite.setInt(4, 100);
            queryWrite.setInt(5, 100);
            queryWrite.setDate(6, new java.sql.Date(new java.util.Date().getTime()));
            queryWrite.setDate(7, new java.sql.Date(new java.util.Date().getTime()));
            queryWrite.setInt(8, 0);
            queryWrite.setInt(9, 0);
            DatabaseConnection.getInstance().writeStatement(queryWrite);
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Users;");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            assertTrue(rs.next()); // test passes if resultset is not empty
        } catch (SQLException e) {
            fail("SQLException thrown: " + e);
        }

    }

    
    /** @utp.description Test to see if the method can take in a preparedstatement and execute a query - exception. */
    @Test
    public void writeStatementExceptionTest() {
        Class expected = SQLException.class;
        try {
            PreparedStatement queryWrite = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "INSERT INTO Users(sub, issuer, username, wins, losses, huh) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?);");
            queryWrite.setString(1, "sub");
            queryWrite.setString(2, "issuer");
            queryWrite.setString(3, "username");
            queryWrite.setInt(4, 100);
            queryWrite.setInt(5, 100);
            queryWrite.setDate(6, new java.sql.Date(new java.util.Date().getTime()));
            DatabaseConnection.getInstance().writeStatement(queryWrite);
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT * FROM Users;");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            assertTrue(rs.next()); // test passes if resultset is not empty
        } catch (SQLException e) {
            assertTrue(expected.isInstance(e));
        }

    }
}
