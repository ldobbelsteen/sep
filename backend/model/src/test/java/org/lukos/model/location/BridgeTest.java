package org.lukos.model.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.InstanceDB;
import org.lukos.model.exceptions.location.BridgeDoesNotExistException;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.instances.IInstance;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test cases for {@code Bridge}.
 *
 * @author Rick van der Heijden (1461923)
 * @author Martijn van Andel (1251104)
 * @since 22-02-2022
 */
public class BridgeTest extends LocationTest {

    /** The name used for the test {@link Bridge}. */
    private static final String name = "Test-Bridge";
    /** Stores the ID of the {@code Instance} that is used to link the bridges to */
    private static int instanceID = -1;
    private int instances = 0;

    /**
     * Sets up the {@link IInstance} necessary to link a {@link Bridge} to.
     */
    @BeforeEach
    public void setUpBridge() {
        try {
            createInstance();
        } catch (SQLException e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    @Override
    protected Location createNewInstance() {
        try {
            createInstance();
            String bridgeName = name + instances;
            instances++;
            return new Bridge(instanceID, bridgeName);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
        return null;
    }

    /**
     * Creates an instance to use during testing.
     *
     * @throws SQLException when a database operation fails.
     */
    private static void createInstance() throws SQLException {
        if (instanceID == -1 || !InstanceDB.findInstanceByID(instanceID).next()) {
            instanceID = InstanceManager.getInstanceManager().createInstance(1, "BridgeTest", 1);
        }
    }

    /**
     * Tests the constructor
     *
     * @utp.description Tests whether a bridge that is constructed by an instance has the correct name.
     */
    @Test
    public void testBridgeConstructor() {
        try {
            assertEquals(name + 0, ((Bridge) instance).getName());
            String bridgeName = name + instances;
            Bridge bridge = (Bridge) createNewInstance();
            assertEquals(bridgeName, bridge.getName());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests the constructor
     *
     * @utp.description Tests whether assigning a name to a bridge assigns it correctly.
     */
    @Test
    public void testBridgeConstructor1() {
        try {
            String name = "Test";
            String name1 = "Sint-vlaflip oversteekbrug";
            String name2 = "De heen en weerwolfbrug";

            assertEquals(name, (new Bridge(instanceID, name)).getName());
            assertEquals(name1, (new Bridge(instanceID, name1)).getName());
            assertEquals(name2, (new Bridge(instanceID, name2)).getName());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests the constructor
     *
     * @utp.description Tests whether it returns the name of an existing bridge correctly.
     */
    @Test
    public void testBridgeConstructor2() {
        try {
            int bridgeID = instance.getId();

            assertEquals(name + 0, (new Bridge(bridgeID)).getName());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the constructor throws an exception when asked for a non-existing bridge.
     */
    @Test
    public void testBridgeConstructException() {
        Class<?> expected = BridgeDoesNotExistException.class;
        try {
            new Bridge(666);
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * @utp.description Tests whether the players at location list is not {@code null} on initialization.
     */
    @Override @Test
    public void testConstructor() {
        super.testConstructor();
    }

    /**
     * @utp.description Tests whether all constructors for existing locations do not return a list of players at
     * location that is {@code null}.
     */
    @Override @Test
    public void testConstructor1() {
        super.testConstructor1();
    }

    /**
     * @utp.description Tests whether IDs are not equal to each other when creating multiple locations.
     */
    @Override @Test
    public void testConstructorIDEquality() {
        super.testConstructorIDEquality();
    }

    /**
     * @utp.description Tests the visitPlayer() function, whether a player actually visits that location.
     */
    @Override @Test
    public void testVisitPlayer() {
        super.testVisitPlayer();
    }

    /**
     * @utp.description Tests the visitPlayer() function, whether multiple players can visit a location.
     */
    @Override @Test
    public void testVisitPlayer1() {
        super.testVisitPlayer1();
    }

    /**
     * @utp.description Tests whether a player leaves his previous location once it visits a new location.
     */
    @Override @Test
    public void testLeavePlayer() {
        super.testLeavePlayer();
    }

    /**
     * @utp.description Tests whether if multiple players visit a new location they also leave their old location.
     */
    @Override @Test
    public void testLeavePlayer1() {
        super.testLeavePlayer1();
    }
}
