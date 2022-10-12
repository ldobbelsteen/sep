package org.lukos.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.location.Bridge;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.lukos.database.LocationDB.*;

/**
 * Test cases for {@link LocationDB}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class LocationDBTest extends GameTest {

    private static final String BRIDGE_NAME = "LocationDBTest bridge";
    private IInstance instance;
    private Bridge bridge;

    @BeforeEach
    public void setUp() {
        try {
            InstanceManager manager = InstanceManager.getInstanceManager();
            this.instance = manager.getInstance(manager.createInstance(1, "LocationDBTest", 1));
            this.bridge = new Bridge(this.instance.getIid(), BRIDGE_NAME);
        } catch (SQLException | NoSuchInstanceException e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /** @utp.description Testing the constructor for LocationDB */
    @Test
    public void constructorTest() {
        try {
            new LocationDB();
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Test whether {@code true} is returned when the {@code Bridge} exist.
     */
    @Test
    public void bridgeExistsTest() {
        try {
            assertTrue(bridgeExists(this.bridge.getId()), "Bridge should exist");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code loadOrCreateBridge()} creates a bridge if it does not exist.
     */
    @Test
    public void loadOrCreateBridgeTest1() {
        try {
            int id = createNewLocation(this.instance.getIid());
            assertFalse(bridgeExists(id), "Bridge should not exist.");
            loadOrCreateBridge(this.instance.getIid(), id, "LocationDBTest bridge");
            assertTrue(bridgeExists(id), "Bridge should exist.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code loadOrCreateBridge()} loads a bridge if it does exist.
     */
    @Test
    public void loadOrCreateBridgeTest2() {
        try {
            assertTrue(bridgeExists(this.bridge.getId()), "Bridge should exist.");
            loadOrCreateBridge(this.instance.getIid(), this.bridge.getId(), "LocationDBTest bridge");
            assertTrue(bridgeExists(this.bridge.getId()), "Bridge should exist.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /** @utp.description Tests whether the function {@code getNameBridgeByID()} returns the correct name. */
    @Test
    public void getNameBridgeByIDTest() {
        try {
            assertEquals(BRIDGE_NAME, getNameBridgeByID(this.bridge.getId()));
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code getNameBridgeByID()} throws an exception when giving a non-existing ID.
     */
    @Test
    public void getNameBridgeByIDExceptionTest() {
        Class<?> expected = SQLException.class;
        try {
            getNameBridgeByID(666);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether the function {@code getInstanceIDBridgeByID()} returns the instance ID. */
    @Test
    public void getInstanceIDBridgeByIDTest() {
        try {
            assertEquals(this.instance.getIid(), getInstanceIDBridgeByID(this.bridge.getId()));
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code getInstanceIDBridgeByID()} throws an exception when giving a non-existing ID.
     */
    @Test
    public void getInstanceIDBridgeByIDExceptionTest() {
        Class<?> expected = SQLException.class;
        try {
            getInstanceIDBridgeByID(666);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether the function {@code getBridgesByInstance()} retrieves the right bridges. */
    @Test
    public void getBridgesByInstanceTest() {
        try {
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT bridgeID FROM Bridge WHERE instanceID=?;");
            statement.setObject(1, this.instance.getIid());
            ResultSet rs = DatabaseConnection.getInstance().readStatement(statement);

            List<Integer> bridges = new ArrayList<>();
            while (rs.next()) {
                bridges.add(rs.getInt("bridgeID"));
            }
            List<Integer> retrievedBridges = getBridgesByInstance(this.instance.getIid());
            assertEquals(bridges.size(), retrievedBridges.size(), "Size should be equal.");
            assertTrue(bridges.containsAll(retrievedBridges), "bridges should contain retrievedBridges.");
            assertTrue(retrievedBridges.containsAll(bridges), "retrievedBridges should contain bridges.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code removeBridgeByInstance()} actually removes the bridge.
     */
    @Test
    public void removeBridgeByInstanceTest() {
        try {
            assertTrue(bridgeExists(this.bridge.getId()), "Bridge should exist.");
            removeBridgeByInstance(this.instance.getIid(), this.bridge.getId());
            assertFalse(bridgeExists(this.bridge.getId()), "Bridge should not exist.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code removeBridgeByInstance()} actually removes the bridge.
     */
    @Test
    public void getLocationsFromInstanceIDTest() {
        try {
            List<Integer> locations = this.instance.getBridges();
            List<Integer> retrievedLocations = getLocationsFromInstanceID(this.instance.getIid());
            assertEquals(locations.size(), retrievedLocations.size(), "Size should be equal.");
            assertTrue(locations.containsAll(retrievedLocations), "locations should contain retrievedLocations.");
            assertTrue(retrievedLocations.containsAll(locations), "retrievedLocations should contain locations.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /** @utp.description Tests whether the function {@code createNewLocation()} creates a new location. */
    @Test
    public void createNewLocationTest() {
        try {
            PreparedStatement statement =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM Location");
            ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
            int lastID = -1;
            while (resultSet.next()) {
                lastID = resultSet.getInt("LocationID");
            }

            int newID = lastID + 1;
            assertFalse(existLocation(newID), "Location should not exist.");
            createNewLocation(this.instance.getIid());
            assertTrue(existLocation(newID), "Location should exist.");
        } catch (Exception e) {
            fail("Unexpected exception thrown : " + e);
        }
    }

    /**
     * @utp.description Tests whether the function {@code createNewLocation()} throws an exception when giving a non-existing ID.
     */
    @Test
    public void createNewLocationExceptionTest() {
        Class<?> expected = SQLException.class;
        try {
            createNewLocation(this.instance.getIid() + 1);
            fail("Should have thrown an exception.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * Returns whether the {@code Location} with ID {@code id} exists.
     *
     * @param id the ID of the {@code Location}
     * @return whether the {@code Location} with ID {@code id} exists
     * @throws SQLException when a database operation fails
     */
    private boolean existLocation(int id) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT locationID FROM Location WHERE locationID = ?;");
        statement.setInt(1, id);
        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        return resultSet.next();
    }
}
