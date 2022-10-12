package org.lukos.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.DatabaseConnection;
import org.lukos.database.UserDB;
import org.lukos.model.GameTest;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.exceptions.user.UserAlreadyExistException;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link UserManager}
 *
 * @author Valentijn van den Berg (1457446)
 * @since 21-02-2022
 */
public class UserManagerTest extends GameTest {

    /** UserManager instance */
    private UserManager userManager;

    /** get the UserManager instance */
    @BeforeEach
    public void beforeTests() {
        deleteAllUsers();
        userManager = UserManager.getInstance();
    }

    /** This method will remove all Users from the 'Users' table. */
    private void deleteAllUsers() {
        try {
            // Get all users
            PreparedStatement statement =
                    DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT userID FROM Users;");

            ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

            List<Integer> userList = new ArrayList<>();

            // Extract all users
            while (resultSet.next()) {
                userList.add(resultSet.getInt("userID"));
            }

            // Remove all users
            for (int id : userList) {
                UserDB.deleteUserByUID(id);
            }

        } catch (Exception e) {
            fail("An exception was thrown while removing users: " + e);
        }
    }

    //region Helpers

    /**
     * Helper method that creates a new user.
     *
     * @param issuerSub The issuerSub this user will have.
     * @param username  Username of the user
     */
    private User createUserHelper(IssuerSub issuerSub, String username) {
        try {
            // Create user with ID uid
            User user = userManager.createUser(issuerSub, username);

            // Test whether the user was created correctly
            checkCreatedUser(user.getUid(), issuerSub, username);

            return user;
        } catch (UserAlreadyExistException e) {
            fail("You defied the odds, there was a collision with the UUIDs, run the tests again.");
        } catch (Exception e) {
            fail("An exception was thrown in createUserHelper: " + e);
        }
        return null;
    }

    /**
     * Create new user with random UUID
     *
     * @param issuerSub The issuerSub this user will have
     */
    private User createUserHelper(IssuerSub issuerSub) {
        return createUserHelper(issuerSub, "Username");
    }

    /**
     * Checks if the {@code expectedUser} is the same as the {@code realUser}.
     *
     * @param expectedUser expected return value as {@code User} instance.
     * @param realUser     the User instance returned by the tested method.
     */
    private void compareUsers(User expectedUser, User realUser) {
        try {
            assertNotNull(expectedUser, "expectedUser was null!");
            assertNotNull(realUser, "realUser was null!");
            assertEquals(expectedUser.getUid(), realUser.getUid(), "id mismatch!");
            assertEquals(expectedUser.getIssuer(), realUser.getIssuer(), "Issuer mismatch!");
            assertEquals(expectedUser.getSub(), realUser.getSub(), "Sub mismatch!");
            assertEquals(expectedUser.getUsername(), realUser.getUsername(), "Username mismatch!");
        } catch (Exception e) {
            fail("An exception was thrown in compareUsers: " + e);
        }
    }

    /**
     * Checks if the {@code returnedUser} was returned correctly and with the right values
     *
     * @param userId            The userId
     * @param expectedIssuerSub expected {@code IssuerSub} of this user.
     * @param username          expected username of this user.
     */
    private void checkCreatedUser(int userId, IssuerSub expectedIssuerSub, String username) {
        try {
            User user = userManager.getUser(userId);

            assertNotNull(user, "User was null!");
            assertEquals(expectedIssuerSub.issuer(), user.getIssuer(), "Issuer mismatch!");
            assertEquals(expectedIssuerSub.sub(), user.getSub(), "Sub mismatch!");
            assertEquals(username, user.getUsername(), "Username mismatch!");
        } catch (Exception e) {
            fail("An exception was thrown in checkCreatedUser: " + e);
        }
    }

    //endregion

    /**
     * Tests whether only 1 instance of the user manager exist.
     *
     * @utp.description Test whether there exist exactly one instance of {@code UserManager}.
     */
    @Test
    public void singletonTest() {
        assertEquals(userManager, UserManager.getInstance(), "More than one userManager exist!");
    }

    //region getAndCreateUser

    // ---- With (IssuerSub, username) params ----

    /**
     * getAndCreateUser(issuerSub, username) test, the {@code User} does exist, one user in map, {@code User} should be
     * returned.
     *
     * @utp.description Test whether the correct {@code User} is returned if there is exactly one user active in
     *         the {@code UserManager} instance.
     */
    @Test
    public void getAndCreateUserSingleTest() {
        // Create a user
        IssuerSub issuerSub = new IssuerSub("issuer", "sub");
        User createdUser = createUserHelper(issuerSub);

        // Get the user
        try {
            User user = userManager.getAndCreateUser(issuerSub, "username");

            // Test whether the expected user was returned
            compareUsers(createdUser, user);
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * getAndCreateUser(issuerSub, username) test, the {@code User} does exist, multiple {@code User} in map, the
     * correct user should be returned.
     *
     * @utp.description Test whether the correct {@code User} is returned if there are multiple users active in
     *         the {@code UserManager} instance.
     */
    @Test
    public void getAndCreateUserMultipleTest() {
        // Create four IssuerSubs
        IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
        IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
        IssuerSub issuerSub3 = new IssuerSub("i3", "s3");
        IssuerSub issuerSub4 = new IssuerSub("i4", "s4");

        // Create multiple users
        User createdUser1 = createUserHelper(issuerSub1);
        User createdUser2 = createUserHelper(issuerSub2);
        User createdUser3 = createUserHelper(issuerSub3);
        User createdUser4 = createUserHelper(issuerSub4);

        // Try to get all users
        User user1 = null;
        try {
            user1 = userManager.getAndCreateUser(issuerSub1, "username");
        } catch (Exception e) {
            fail("An exception was thrown during get user1: " + e);
        }
        User user2 = null;
        try {
            user2 = userManager.getAndCreateUser(issuerSub2, "username");
        } catch (Exception e) {
            fail("An exception was thrown during get user2: " + e);
        }
        User user3 = null;
        try {
            user3 = userManager.getAndCreateUser(issuerSub3, "username");
        } catch (Exception e) {
            fail("An exception was thrown during get user3: " + e);
        }
        User user4 = null;
        try {
            user4 = userManager.getAndCreateUser(issuerSub4, "username");
        } catch (Exception e) {
            fail("An exception was thrown during get user4: " + e);
        }

        // Test whether the expected users were returned
        compareUsers(createdUser1, user1);

        compareUsers(createdUser2, user2);

        compareUsers(createdUser3, user3);

        compareUsers(createdUser4, user4);
    }

    /**
     * getAndCreateUser(issuerSub, username) test, the {@code User} does not exist, {@code User} should be created and
     * added to the map.
     *
     * @utp.description Test whether correct {@code User} is returned if the user did not exist in the
     *         {@code UserManager} instance.
     */
    @Test
    public void getAndCreateUserNotExistTest() {
        // Create issuerSub
        IssuerSub issuerSub = new IssuerSub("issuer", "sub");

        // Create the user
        User user = null;
        try {
            user = userManager.getAndCreateUser(issuerSub, "username");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
        // Test that the user was created correctly
        checkCreatedUser(user.getUid(), issuerSub, "username");
    }

    // ---- With (IssuerSub, userId, username) params ----

    /**
     * getAndCreateUser(IssuerSub, userID, username) test, the {@code User} does exist, one user in map, {@code User}
     * should be returned.
     *
     * @utp.description Test whether the correct {@code User} is returned if there is exactly one user active in
     *         the {@code UserManager} instance.
     */
    @Test
    public void getAndCreateUserIDSingleTest() {
        // Create a user
        IssuerSub issuerSub = new IssuerSub("issuer", "sub");
        User createdUser = createUserHelper(issuerSub);

        // Get the user
        User user = null;
        try {
            user = userManager.getAndCreateUser(issuerSub, createdUser.getUid(), "username");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }

        // Test whether the expected user was returned
        compareUsers(createdUser, user);
    }

    /**
     * getAndCreateUser(IssuerSub, userID, username) test, the {@code User} does exist, multiple {@code User} in map,
     * the correct user should be returned.
     *
     * @utp.description Test whether the correct {@code User} is returned if there are multiple users active in
     *         the {@code UserManager} instance.
     */
    @Test
    public void getAndCreateUserIDMultipleTest() {
        // Create four IssuerSubs
        IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
        IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
        IssuerSub issuerSub3 = new IssuerSub("i3", "s3");
        IssuerSub issuerSub4 = new IssuerSub("i4", "s4");

        // Create multiple users
        User createdUser1 = createUserHelper(issuerSub1);
        User createdUser2 = createUserHelper(issuerSub2);
        User createdUser3 = createUserHelper(issuerSub3);
        User createdUser4 = createUserHelper(issuerSub4);

        // Try to get all users
        User user1 = null;
        try {
            user1 = userManager.getAndCreateUser(issuerSub1, createdUser1.getUid(), "username");
        } catch (Exception e) {
            fail("An exception was thrown during get user1: " + e);
        }
        User user2 = null;
        try {
            user2 = userManager.getAndCreateUser(issuerSub2, createdUser2.getUid(), "username");
        } catch (Exception e) {
            fail("An exception was thrown during get user2: " + e);
        }
        User user3 = null;
        try {
            user3 = userManager.getAndCreateUser(issuerSub3, createdUser3.getUid(), "username");
        } catch (Exception e) {
            fail("An exception was thrown during get user3: " + e);
        }
        User user4 = null;
        try {
            user4 = userManager.getAndCreateUser(issuerSub4, createdUser4.getUid(), "username");
        } catch (Exception e) {
            fail("An exception was thrown during get user4: " + e);
        }

        // Test whether the expected users were returned
        compareUsers(createdUser1, user1);

        compareUsers(createdUser2, user2);

        compareUsers(createdUser3, user3);

        compareUsers(createdUser4, user4);
    }

    /**
     * getAndCreateUser(issuerSub, userID, username) test, the {@code User} does not exist, {@code User} should be
     * created and added to the map.
     *
     * @utp.description Test whether correct {@code User} is returned if the user did not exist in the
     *         {@code UserManager} instance.
     */
    @Test
    public void getAndCreateUserNotExistTest2() {
        // Create issuerSub
        IssuerSub issuerSub = new IssuerSub("issuer", "sub");

        // Create the user
        User user = null;
        try {
            user = userManager.getAndCreateUser(issuerSub, 124567, "username");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
        // Test that the user was created correctly
        checkCreatedUser(user.getUid(), issuerSub, "username");
    }

    //endregion
    //region getUser

    // ---- With (userId) param ----

    /**
     * getUser(userId) test, the {@code User} does exist, one {@code User} in map, {@code User} should be returned.
     *
     * @utp.description Test whether the correct {@code User} is returned if there is exactly one user active in
     *         the {@code UserManager} instance.
     */
    @Test
    public void getUserUUIDSingleTest() {
        // Create new user
        IssuerSub issuerSub = new IssuerSub("issuer", "sub");
        User createdUser = createUserHelper(issuerSub);
        int uid = createdUser.getUid();

        // Try to get the user
        User user = null;
        try {
            user = userManager.getUser(uid);
        } catch (NoSuchUserException e) {
            fail("User could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }

        // Test whether the expected user was returned
        compareUsers(createdUser, user);
    }

    /**
     * getUser(userId) test, the {@code User} does exist, multiple {@code User} in map, the correct {@code User} should
     * be returned.
     *
     * @utp.description Test whether the correct {@code User} is returned if there are multiple users active in
     *         the {@code UserManager} instance.
     */
    @Test
    public void getUserUUIDMultipleTest() {
        // Create four IssuerSubs
        IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
        IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
        IssuerSub issuerSub3 = new IssuerSub("i3", "s3");
        IssuerSub issuerSub4 = new IssuerSub("i4", "s4");

        // Create multiple users
        User createdUser1 = createUserHelper(issuerSub1);
        User createdUser2 = createUserHelper(issuerSub2);
        User createdUser3 = createUserHelper(issuerSub3);
        User createdUser4 = createUserHelper(issuerSub4);

        // Get IDs
        int uid1 = createdUser1.getUid();
        int uid2 = createdUser2.getUid();
        int uid3 = createdUser3.getUid();
        int uid4 = createdUser4.getUid();

        // Try to get all users
        User user1 = null;
        User user2 = null;
        User user3 = null;
        User user4 = null;
        try {
            user1 = userManager.getUser(uid1);
        } catch (NoSuchUserException e) {
            fail("User 1 could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown (1): " + e);
        }
        try {
            user2 = userManager.getUser(uid2);
        } catch (NoSuchUserException e) {
            fail("User 2 could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown (2): " + e);
        }
        try {
            user3 = userManager.getUser(uid3);
        } catch (NoSuchUserException e) {
            fail("User 3 could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown (3): " + e);
        }
        try {
            user4 = userManager.getUser(uid4);
        } catch (NoSuchUserException e) {
            fail("User 4 could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown (4): " + e);
        }

        // Test whether the expected users were returned
        compareUsers(createdUser1, user1);

        compareUsers(createdUser2, user2);

        compareUsers(createdUser3, user3);

        compareUsers(createdUser4, user4);
    }

    /**
     * getUser(userId) test, the {@code User} does not exist, {@code NoSuchUserException} should be thrown.
     *
     * @utp.description Test whether an {@code NoSuchUserException} is thrown if
     *         {@code existUser(user) == false}.
     */
    @Test
    public void getUserUUIDExceptionTest() {
        try {
            // Try to find a user that does not exist
            userManager.getUser(123456789);
            fail("A NoSuchUserException should be thrown!");
        } catch (Exception e) {
            Class expected = NoSuchUserException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }

    // ---- With (IssuerSub) param ----

    /**
     * getUser(IssuerSub) test, the {@code User} does exist, one {@code User} in map, {@code User} should be returned.
     *
     * @utp.description Test whether the correct {@code User} is returned if there is exactly one user active in
     *         the {@code UserManager} instance.
     */
    @Test
    public void getUserIssuerSubSingleTest() {
        // Create new user
        IssuerSub issuerSub = new IssuerSub("issuer", "sub");
        User createdUser = createUserHelper(issuerSub);

        // Try to get the user
        User user = null;
        try {
            user = userManager.getUser(issuerSub);
        } catch (NoSuchUserException e) {
            fail("User could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }

        // Test whether the expected user was returned
        compareUsers(createdUser, user);
    }

    /**
     * getUser(IssuerSub) test, the user does exist, multiple user in map, the correct user should be returned.
     *
     * @utp.description Test whether the correct {@code User} is returned if there are multiple users active in
     *         the {@code UserManager} instance.
     */
    @Test
    public void getUserIssuerSubMultipleTest() {
        // Create four IssuerSubs
        IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
        IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
        IssuerSub issuerSub3 = new IssuerSub("i3", "s3");
        IssuerSub issuerSub4 = new IssuerSub("i4", "s4");

        // Create IDs
        int uid1 = 123456789;
        int uid2 = 123456789;
        int uid3 = 123456789;
        int uid4 = 123456789;

        // Create multiple users
        User createdUser1 = createUserHelper(issuerSub1);
        User createdUser2 = createUserHelper(issuerSub2);
        User createdUser3 = createUserHelper(issuerSub3);
        User createdUser4 = createUserHelper(issuerSub4);

        // Try to get all users
        User user1 = null;
        User user2 = null;
        User user3 = null;
        User user4 = null;
        try {
            user1 = userManager.getUser(issuerSub1);
        } catch (NoSuchUserException e) {
            fail("User 1 could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown (1): " + e);
        }
        try {
            user2 = userManager.getUser(issuerSub2);
        } catch (NoSuchUserException e) {
            fail("User 2 could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown (2): " + e);
        }
        try {
            user3 = userManager.getUser(issuerSub3);
        } catch (NoSuchUserException e) {
            fail("User 3 could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown (3): " + e);
        }
        try {
            user4 = userManager.getUser(issuerSub4);
        } catch (NoSuchUserException e) {
            fail("User 4 could not be found!");
        } catch (Exception e) {
            fail("An exception was thrown (4): " + e);
        }

        // Test whether the expected users were returned
        compareUsers(createdUser1, user1);

        compareUsers(createdUser2, user2);

        compareUsers(createdUser3, user3);

        compareUsers(createdUser4, user4);
    }

    /**
     * getUser(IssuerSub) test, the user does not exist, NoSuchUserException should be thrown.
     *
     * @utp.description Test whether an {@code NoSuchUserException} is thrown if
     *         {@code existUser(user) == false}.
     */
    @Test
    public void getUserIssuerSubExceptionTest() {
        try {
            // Try to find a user that does not exist
            userManager.getUser(new IssuerSub("Random", "String"));
            fail("A NoSuchUserException should have thrown!");
        } catch (Exception e) {
            Class expected = NoSuchUserException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }

    //endregion
    //region existUser

    /**
     * existUser test, the user exist, should return true
     *
     * @utp.description Test whether {@code true} is returned if the user does exist in the {@code UserManager}
     *         instance.
     */
    @Test
    public void existUserTest1() {
        try {
            IssuerSub issuerSub = new IssuerSub("iss", "sub");
            // User does not exist
            assertFalse(userManager.existUser(issuerSub),
                    "[IssuerSub] This user does not exist, should have returned false!");
            // Create a user
            int uid = createUserHelper(issuerSub).getUid();
            // User should exist now
            assertTrue(userManager.existUser(uid), "[UUID] This user exist,should have returned true!");
            assertTrue(userManager.existUser(issuerSub), "[IssuerSub] This user exist,should have returned true!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * existUser test, the user does not exist, should return false.
     *
     * @utp.description Test whether {@code false} is returned if there are no active users in the
     *         {@code UserManager} instance.
     */
    @Test
    public void existUserTest2() {
        try {
            assertFalse(userManager.existUser(123456789),
                    "[UUID] This user does not exist, should have returned false!");
            assertFalse(userManager.existUser(new IssuerSub("i", "s")),
                    "[IssuerSub] This user does not exist, should have returned false!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * existUser test, multiple users in map, try a non-existing users.
     *
     * @utp.description Test whether {@code false} is returned if the user does not exist in the
     *         {@code UserManager} instance, when multiple users are active.
     */
    @Test
    public void existUserTest3() {
        try {
            // Create three IssuerSubs
            IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
            IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
            IssuerSub issuerSub3 = new IssuerSub("i3", "s3");

            // Create users
            User user1 = createUserHelper(issuerSub1);
            User user2 = createUserHelper(issuerSub2);
            User user3 = createUserHelper(issuerSub3);

            int uid4 = 123456789;
            IssuerSub issuerSub4 = new IssuerSub("is4", "su4");

            // Try all created user
            assertTrue(userManager.existUser(user1.getUid()),
                    "[UUID] User 1 exist, the method should have returned true!");
            assertTrue(userManager.existUser(user2.getUid()),
                    "[UUID] User 2 exist, the method should have returned true!");
            assertTrue(userManager.existUser(user3.getUid()),
                    "[UUID] User 3 exist, the method should have returned true!");
            assertTrue(userManager.existUser(issuerSub1),
                    "[IssuerSub] User 1 exist, the method should have returned true!");
            assertTrue(userManager.existUser(issuerSub2),
                    "[IssuerSub] User 2 exist, the method should have returned true!");
            assertTrue(userManager.existUser(issuerSub3),
                    "[IssuerSub] User 3 exist, the method should have returned true!");

            // Try non-existing user 4
            assertFalse(userManager.existUser(uid4),
                    "[UUID] User 4 does not exist, the method should have returned false!");
            assertFalse(userManager.existUser(issuerSub4),
                    "[IssuerSub] User 4 does not exist, the method should have returned false!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * existUser test, multiple users in map, find an existing user.
     *
     * @utp.description Test whether {@code true} is returned if the user does exist in the {@code UserManager}
     *         instance, when multiple users are active.
     */
    @Test
    public void existUserTest4() {
        try {
            // Create three IssuerSubs
            IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
            IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
            IssuerSub issuerSub3 = new IssuerSub("i3", "s3");

            // Create users
            User user1 = createUserHelper(issuerSub1);
            User user2 = createUserHelper(issuerSub2);
            User user3 = createUserHelper(issuerSub3);

            IssuerSub issuerSub4 = new IssuerSub("iss4", "sub4");

            // Try all created user
            assertTrue(userManager.existUser(user1.getUid()),
                    "[UUID] User 1 exist, the method should have returned true!");
            assertTrue(userManager.existUser(user2.getUid()),
                    "[UUID] User 2 exist, the method should have returned true!");
            assertTrue(userManager.existUser(user3.getUid()),
                    "[UUID] User 3 exist, the method should have returned true!");
            assertTrue(userManager.existUser(issuerSub1),
                    "[IssuerSub] User 1 exist, the method should have returned true!");
            assertTrue(userManager.existUser(issuerSub2),
                    "[IssuerSub] User 2 exist, the method should have returned true!");
            assertTrue(userManager.existUser(issuerSub3),
                    "[IssuerSub] User 3 exist, the method should have returned true!");

            // Try non-existing user 4
            assertFalse(userManager.existUser(issuerSub4),
                    "[IssuerSub] User 4 does not exist, the method should have returned false!");

            // Create User 4
            User user4 = createUserHelper(issuerSub4);

            // User 4 now exist
            assertTrue(userManager.existUser(user4.getUid()),
                    "[UUID] User 4 exist, the method should have returned true!");
            assertTrue(userManager.existUser(issuerSub4),
                    "[IssuerSub] User 4 exist, the method should have returned true!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    //endregion
    //region createUser

    /**
     * createUser(Issuer, Sub, userId, username) test, user should be in the map after creation and should be a valid
     * user.
     *
     * @utp.description Test whether a user is created correctly when creating a user with
     *         {@code String, String} and {@code UUID} parameters.
     */
    @Test
    public void createUserStrStrUUIDTest() {
        String issuer = "issuer";
        String sub = "sub";
        String username = "username";
        try {
            User user = userManager.createUser(issuer, sub, -1, username);

            // Test
            checkCreatedUser(user.getUid(), new IssuerSub(issuer, sub), username);
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }

    }

    /**
     * createUser(Issuer, Sub, userId, username) test, multiple user in the map, we create another user, this user
     * should be in the map and should be a valid user.
     *
     * @utp.description Test whether a user is created correctly when creating a user with
     *         {@code String, String} and {@code UUID} parameters.
     */
    @Test
    public void createUserStrStrUUIDAlreadyPopulatedTest() {
        // Create three IssuerSubs
        IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
        IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
        IssuerSub issuerSub3 = new IssuerSub("i3", "s3");

        // Create some users to populate the map
        createUserHelper(issuerSub1);
        createUserHelper(issuerSub2);
        createUserHelper(issuerSub3);

        // Create variables for the new user
        String issuer = "issuer";
        String sub = "sub";
        String username = "username";

        // Create new user
        try {
            User user4 = userManager.createUser(issuer, sub, -1, username);

            // Test if the user was created correctly
            checkCreatedUser(user4.getUid(), new IssuerSub(issuer, sub), username);
        } catch (Exception e) {
            fail("An exception was thrown!");
        }
    }

    /**
     * createUser(Issuer, Sub, userId, username) test, user already exist, UserAlreadyExistException should be thrown
     *
     * @utp.description Test whether an UserAlreadyExistException is thrown when a user with {@code uid} already
     *         exist.
     */
    @Test
    public void createUserStrStrUUIDExceptionTest() {
        // Create user with fixed id
        IssuerSub issuerSub = new IssuerSub("issuer", "sub");
        int uid = createUserHelper(issuerSub).getUid();

        try {
            userManager.createUser("issuer", "sub", uid, "un");
            fail("A UserAlreadyExistException should have been thrown!");
        } catch (Exception e) {
            Class expected = UserAlreadyExistException.class;
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null.");
        }
    }

    /**
     * createUser(IssuerSub, username) test, user should be in the map and should be a valid user.
     *
     * @utp.description Test whether a user is created correctly when creating a user with {@code IssuerSub} and
     *         {@code UUID} parameters.
     */
    @Test
    public void createUserIssuerSubUUIDTest() {
        // The helper method does this test completely
        createUserHelper(new IssuerSub("issuer", "sub"), "username");
    }

    /**
     * createUser(IssuerSub, username) test, user should be in the map after creation and should be a valid user.
     *
     * @utp.description Test whether a user is created correctly when creating a user with
     *         {@code String, String} and {@code UUID} parameters.
     */
    @Test
    public void createUserIssuerSubTest() {
        IssuerSub issuerSub = new IssuerSub("create", "user");
        User user = null;
        try {
            user = userManager.createUser(issuerSub, "NoobMaster69");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
        // Test
        checkCreatedUser(user.getUid(), issuerSub, "NoobMaster69");
    }

    /**
     * createUser(id) test, multiple user in the map, we create another user, this user should be in the map and should
     * be a valid user.
     *
     * @utp.description Test whether a user is created correctly when creating a user with
     *         {@code String, String} and {@code UUID} parameters.
     */
    @Test
    public void createUserIssuerSubAlreadyPopulatedTest() {
        // Create three IssuerSubs
        IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
        IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
        IssuerSub issuerSub3 = new IssuerSub("i3", "s3");

        // Create some users to populate the map
        createUserHelper(issuerSub1);
        createUserHelper(issuerSub2);
        createUserHelper(issuerSub3);

        // Create variables for the new user
        IssuerSub issuerSub4 = new IssuerSub("user", "4");
        User user4 = null;

        // Create new user
        try {
            user4 = userManager.createUser(issuerSub4, "NoobMaster69");
        } catch (Exception e) {
            fail("An exception was thrown!");
        }

        // Test if the user was created correctly
        checkCreatedUser(user4.getUid(), issuerSub4, "NoobMaster69");
    }

    //endregion
    //region removeUser

    /**
     * removeUser test, user exists, should return true.
     *
     * @utp.description Test whether a existing {@code User} is removed correctly and whether {@code true} is
     *         returned.
     */
    @Test
    public void removeUserExistTest() {
        try {
            // Create user with fixed uid
            IssuerSub issuerSub = new IssuerSub("issuer", "sub");
            int uid = createUserHelper(issuerSub).getUid();

            // Test to confirm users existence
            assertTrue(userManager.existUser(uid), "User should exist!");

            // Remove user
            boolean result = userManager.removeUser(issuerSub);

            // Test whether the user has been removed correctly
            assertFalse(userManager.existUser(uid), "User should have been removed!");
            assertTrue(result, "Result should be true!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * removeUser test, user does not exist, should return false.
     *
     * @utp.description Test whether {@code false} is returned, when removing a non-existing {@code User}.
     */
    @Test
    public void removeUserNotExistTest() {
        try {
            // Generate new IssuerSub
            IssuerSub issuerSub = new IssuerSub("random", "string");

            // Remove random user
            boolean result = userManager.removeUser(issuerSub);

            // Test whether the user has been removed correctly
            assertFalse(userManager.existUser(issuerSub), "User should still not exist!");
            assertFalse(result, "Result should be false!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * removeUser test, confirm that only the specified user is removed  .
     *
     * @utp.description Test whether only the correct {@code User} is removed and all {@code User} are
     *         untouched.
     */
    @Test
    public void removeUserRightUserTest() {
        try {
            // Create three IssuerSubs
            IssuerSub issuerSub1 = new IssuerSub("i1", "s1");
            IssuerSub issuerSub2 = new IssuerSub("i2", "s2");
            IssuerSub issuerSub3 = new IssuerSub("i3", "s3");

            // Create three users
            int uid1 = createUserHelper(issuerSub1).getUid();
            int uid2 = createUserHelper(issuerSub2).getUid();
            int uid3 = createUserHelper(issuerSub3).getUid();

            // Make sure these users exist
            assertTrue(userManager.existUser(uid1), "User 1 should exist!");
            assertTrue(userManager.existUser(uid2), "User 2 should exist!");
            assertTrue(userManager.existUser(uid3), "User 3 should exist!");

            // Remove user 1
            boolean result = userManager.removeUser(issuerSub1);

            // Test that only user 1 is removed
            assertTrue(result, "Result should have been true!");
            assertFalse(userManager.existUser(uid1), "User 1 should have been removed!");
            assertTrue(userManager.existUser(uid2), "User 2 should exist!");
            assertTrue(userManager.existUser(uid3), "User 3 should exist!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * setToRemoveUser test, user exists, should return true.
     *
     * @utp.description Test whether a existing {@code User} is removed correctly and whether {@code true} is
     *         returned.
     */
    @Test
    public void setToRemoveUserExistTest() {
        try {
            // Create user with fixed uid
            IssuerSub issuerSub = new IssuerSub("issuer", "sub");
            int uid = createUserHelper(issuerSub).getUid();

            // Test to confirm users existence
            assertTrue(userManager.existUser(uid), "User should exist!");

            // Remove user
            boolean result = userManager.setToRemoveUser(issuerSub);

            // Test whether the user has been removed correctly
            assertTrue(result, "Result should be true!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * setToRemoveUser test, user does not exist, should return false.
     *
     * @utp.description Test whether {@code false} is returned, when removing a non-existing {@code User}.
     */
    @Test
    public void setToRemoveUserNotExistTest() {
        try {
            // Generate new IssuerSub
            IssuerSub issuerSub = new IssuerSub("random", "string");

            // Remove random user
            boolean result = userManager.setToRemoveUser(issuerSub);

            // Test whether the user has been removed correctly
            assertFalse(result, "Result should be false!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    //endregion
}
