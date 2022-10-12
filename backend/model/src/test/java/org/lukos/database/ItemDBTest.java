package org.lukos.database;

import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.voting.VoteType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ItemDBTest extends GameTest {

    
    /** @utp.description Testing the constructor for ItemDB */
    @Test
    public void constructorTest(){
        new ItemDB();
    }

    
    /** @utp.description Testing that adding an item for a player works as expected */
    @Test
    public void addPlayerItemTest() {
        try {
            int uid = UserDB.createUser("iss", "sub", "username");
            int iid = InstanceDB.addNewInstance(1, "newItem", 12345678);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            int item_id = ItemDB.addPlayerItem(new PlayerIdentifier(iid, uid), "TestItem");
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT item FROM PlayerItems WHERE userID=?;");
            queryRead.setInt(1, uid);
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            rs.next();
            assertEquals(rs.getString("item"), "TestItem");
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that adding an item for a player returns an exception when expected */
    @Test
    public void addPlayerItemExceptionTest() {
        Class expected = SQLException.class;
        try {
            int uid = UserDB.createUser("iss", "sub", "username");
            int iid = InstanceDB.addNewInstance(1, "newItem", 12345678);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            int item_id = ItemDB.addPlayerItem(new PlayerIdentifier(iid, uid),
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e));
        }
    }

    
    /** @utp.description Testing that deleting an item from a player works as expected */
    @Test
    public void deletePlayerItemTest() {
        try {
            int uid = UserDB.createUser("iss2", "sub2", "username2");
            int iid = InstanceDB.addNewInstance(1, "newItem2", 12345678);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            int item_id = ItemDB.addPlayerItem(new PlayerIdentifier(iid, uid), "TestItem2");
            ItemDB.deletePlayerItem(new PlayerIdentifier(iid, uid), "TestItem2");
            PreparedStatement queryRead = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "SELECT userID FROM PlayerItems WHERE item=?;");
            queryRead.setString(1, "TestItem2");
            ResultSet rs = DatabaseConnection.getInstance().readStatement(queryRead);
            assertFalse(rs.next());
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    
    /** @utp.description Testing that getting the amount of a certain item for a player works as expected */
    @Test
    public void amountOfItemsTest() {
        try {
            int uid = UserDB.createUser("iss", "sub", "username");
            int iid = InstanceDB.addNewInstance(1, "newItem", 12345678);
            PlayerDB.addNewPlayer(new PlayerIdentifier(iid, uid));
            int item1 = ItemDB.addPlayerItem(new PlayerIdentifier(iid, uid), "TestItem");
            int item2 = ItemDB.addPlayerItem(new PlayerIdentifier(iid, uid), "TestItem");
            int item3 = ItemDB.addPlayerItem(new PlayerIdentifier(iid, uid), "TestItem");
            assertEquals(ItemDB.amountOfItems(new PlayerIdentifier(iid, uid), "TestItem"), 3);
        } catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }
}
