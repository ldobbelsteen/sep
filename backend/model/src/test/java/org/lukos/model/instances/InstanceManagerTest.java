package org.lukos.model.instances;

/**
 * Test cases for {@code InstanceManager}.
 *
 * @author Martijn van Andel (1251104)
 * @since 23-02-2022
 */
public class InstanceManagerTest {
//
//    static InstanceManager im;
//    static int uid;
//    static int iid;
//
//    /**
//     * Before each test, an InstanceManager is initialized and one instance is added.
//     */
//    @BeforeEach
//    public void init() {
//        im = InstanceManager.getInstanceManager();
//        uid = 123;
//        iid = im.createInstance(uid);
//    }
//
//    /**
//     * @utp.description Tests whether and instance can be retrieved after being created.
//     */
//    @Test
//    public void createInstanceTestCorrectUsage() {
//        // Trying to find an existing instance, not expecting any exceptions.
//        try {
//            assertNotNull(im.getInstance(iid),
//                    "getInstance should return Instance with existing ID as input.");
//        } catch (Exception e) {
//            fail("Unexpected exception thrown: "+ e);
//        }
//    }
//
//    /**
//     * @utp.description Tests whether the right exception is thrown when requesting a non-existing instance.
//     */
//    @Test
//    public void createInstanceTestIncorrectUsage() {
//        // Trying to find a non-existing instance, expecting exception.
//        try {
//            im.getInstance(12345678);
//            fail("Should have thrown exception.");
//        } catch (NoSuchInstanceException e) {
//            assertTrue(true, "Expected exception thrown: " + e);
//        } catch (Exception e) {
//            fail("Unexpected exception thrown: "+ e);
//        }
//    }
//
//    /**
//     * @utp.description Tests whether an instance is not removed when called by someone other than the game master.
//     */
//    @Test
//    public void removeInstanceTestNoPermission() {
//        try {
//            im.removeInstance(1234567, iid);
//            fail("Should have thrown exception.");
//        } catch (NoPermissionException e) {
//            assertTrue(true, "Expected exception thrown");
//        } catch (Exception e) {
//            fail("Unexpected exception thrown.");
//        }
//    }
//
//    /**
//     * @utp.description Tests whether an instance is properly removed. Expecting no exceptions.
//     */
//    @Test
//    public void removeInstanceTestCorrectUsage() {
//        try {
//            Instance removedInstance = im.removeInstance(uid, iid);
//
//            // ID of removed instance should match the iid we used to create it.
//            assertEquals( removedInstance.getIid(), iid );
//        } catch (Exception e) {
//            fail("No exception expected.");
//        }
//    }
//
//    /**
//     * @utp.description Tests whether trying to remove a non-existing instance is handled properly.
//     */
//    @Test
//    public void removeNonExistingInstanceTest() {
//        try {
//            im.removeInstance(uid,1234567);
//            fail("Should have thrown exception.");
//        } catch (NoSuchInstanceException e) {
//            assertTrue(true, "Expected exception thrown");
//        } catch (Exception e) {
//            fail("Unexpected exception thrown.");
//        }
//    }
//
//    /**
//     * @utp.description Tests whether all instances are properly returned.
//     */
//    @Test
//    public void getInstancesTest() {
//        int iid2 = im.createInstance(uid);
//        int iid3 = im.createInstance(uid);
//
//        Map<Integer, Instance> instances = im.getInstances();
//
//        assertTrue(instances.containsKey(iid));
//        assertTrue(instances.containsKey(iid2));
//        assertTrue(instances.containsKey(iid3));
//
//        assertFalse(instances.containsKey(1234567));
//    }
}
