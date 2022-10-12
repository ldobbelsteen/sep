package org.lukos.model.user;

import org.lukos.database.UserDB;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.exceptions.user.UserAlreadyExistException;
import org.lukos.model.exceptions.user.UserException;
import org.lukos.model.rolesystem.roles.MainRoleList;

import java.sql.SQLException;

/**
 * Maintains a map of users with their unique identifiers.
 *
 * @author Rick van der Heijden (1461923)
 * @since 08-03-2022
 */
public class UserManager {

    /** Private constructor to ensure Singleton design. */
    private UserManager() {
    }

    /**
     * Returns the only instance for the {@code UserManager} class.
     *
     * @return The instance of the {@code UserManager} class.
     */
    public static UserManager getInstance() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * Gets an existing {@code User}, if they exist. If the {@code User} does not exist it creates the {@code User}.
     *
     * @param issuerSub The {@code IssuerSub} of the {@code User}
     * @param username  the username of the {@code User}
     * @return The {@code User}
     * @throws UserAlreadyExistException if the {@code User} is already created in the creating phase of this function,
     *                                   but was not created in the getting phase of this function
     * @throws SQLException              when a database operation fails
     */
    public User getAndCreateUser(IssuerSub issuerSub, String username) throws UserException, SQLException {
        int id;
        try {
            id = UserDB.getIfExistUser(issuerSub);
            return new User(id);
        } catch (NoSuchUserException e) {
            return createUser(issuerSub, username);
        }
    }

    /**
     * Gets an existing {@code User}, if they exist. If the {@code User} does not exist it creates the {@code User}.
     *
     * @param issuerSub The {@code IssuerSub} of the {@code User}
     * @param uid       The id of the {@code User}
     * @param username  the username of the {@code User}
     * @return The {@code User}
     * @throws UserAlreadyExistException if the {@code User} is already created in the creating phase of this function,
     *                                   but was not created in the getting phase of this function
     * @throws SQLException              when a database operation fails
     * @throws NoSuchUserException       if the {@code User} can not be created
     */
    public User getAndCreateUser(IssuerSub issuerSub, int uid, String username)
            throws UserAlreadyExistException, SQLException, NoSuchUserException {
        User user;
        if (UserDB.existUser(uid)) {
            user = new User(uid);
        } else {
            user = createUser(issuerSub, uid, username);
        }
        return user;
    }

    /**
     * Gets an existing {@code User}, if they exist. If the {@code User} does not exist it throws an exception.
     *
     * @param uid The id of the user
     * @return The {@code User} with the ID {@code uid}
     * @throws NoSuchUserException if the {@code User} does not exist
     * @throws SQLException        when a database operation fails
     */
    public User getUser(int uid) throws NoSuchUserException, SQLException {
        if (!UserDB.existUser(uid)) {
            throw new NoSuchUserException("There does not exist a user with the id: " + uid + ".");
        }
        return new User(uid);
    }

    /**
     * Gets an existing {@code User}. If the {@code User} does not exist, it throws an exception.
     *
     * @param issuerSub the {@code issuerSub} of the {@code User}
     * @return The {@code User} with the ID {@code id}
     * @throws NoSuchUserException if the {@code User} does not exist
     * @throws SQLException        when a database operation fails
     */
    public User getUser(IssuerSub issuerSub) throws NoSuchUserException, SQLException {
        int id = UserDB.getIfExistUser(issuerSub);
        return new User(id);
    }

    /**
     * Returns whether a {@code User} exist with the given {@code uid}.
     *
     * @param uid The ID of the {@code User}.
     * @return Whether the {@code User} exist.
     * @throws SQLException when a database operation fails
     */
    public boolean existUser(int uid) throws SQLException {
        if (uid == -1) {
            return false;
        }
        return UserDB.existUser(uid);
    }

    /**
     * Returns whether a {@code User} exist with the given {@code IssuerSub}.
     *
     * @param issuerSub the {@code issuerSub} of the {@code User}
     * @return Whether the {@code User} exist.
     * @throws SQLException when a database operation fails
     */
    public boolean existUser(IssuerSub issuerSub) throws SQLException {
        try {
            UserDB.getIfExistUser(issuerSub);
            return true;
        } catch (NoSuchUserException e) {
            return false;
        }
    }

    /**
     * Creates a new {@code User} with a random ID.
     *
     * @param issuerSub the {@code issuerSub} of the {@code User}
     * @param username  the username of the {@code User}
     * @return The new {@code user}
     * @throws UserAlreadyExistException if the {@code User} with id {@code uid} already exists
     * @throws SQLException              when a database operation fails
     * @throws NoSuchUserException       if no such {@code User} exist
     */
    public User createUser(IssuerSub issuerSub, String username)
            throws UserAlreadyExistException, SQLException, NoSuchUserException {
        return createUser(issuerSub, -1, username);
    }

    /**
     * Creates a new {@code User} with a specified ID.
     *
     * @param issuerSub the {@code issuerSub} of the {@code User}
     * @param uid       the ID of the {@code User}
     * @param username  the username of the {@code User}
     * @return The new {@code user}
     * @throws UserAlreadyExistException if the {@code User} with id {@code uid} already exists
     * @throws SQLException              when a database operation fails
     * @throws NoSuchUserException       if no such {@code User} exist
     */
    public User createUser(IssuerSub issuerSub, int uid, String username)
            throws UserAlreadyExistException, SQLException, NoSuchUserException {
        return createUser(issuerSub.issuer(), issuerSub.sub(), uid, username);
    }

    /**
     * Creates a new {@code User} with a pre-specified ID.
     *
     * @param issuer   the issuer of the {@code User}
     * @param sub      the issuer of the {@code User}
     * @param uid      The pre-specified ID
     * @param username the username of the {@code User}
     * @return The new {@code user}
     * @throws UserAlreadyExistException if the {@code User} with id {@code uid} already exists
     * @throws NoSuchUserException       if the {@code User} could not be created
     * @throws SQLException              when a database operation fails
     */
    public User createUser(String issuer, String sub, int uid, String username)
            throws UserAlreadyExistException, SQLException, NoSuchUserException {
        if (existUser(uid)) {
            throw new UserAlreadyExistException("The user with id: " + uid + " already exist.");
        }
        int userID;
        try {
            userID = UserDB.createUser(issuer, sub, username);
            /* Add an entry in UserStats for each main role. */
            for (MainRoleList mainroles : MainRoleList.values()) {
                UserDB.addUserRole(userID, 0, 0, mainroles.role.getClass().getSimpleName());
            }
        } catch (SQLException e) {
            throw new UserAlreadyExistException("The user with id: " + uid + " already exist.");
        }
        return new User(userID);
    }

    /**
     * Removes the {@code User} from the users, with IssuerSub {@code issuerSub}.
     *
     * @param issuerSub The {@code IssuerSub} of the {@code User}
     * @return Whether the user that was needed to be removed, existed or not
     * @throws SQLException when a database operation fails
     */
    public boolean removeUser(IssuerSub issuerSub) throws SQLException {
        try {
            UserDB.deleteUserByUID(UserDB.getIfExistUser(issuerSub));
            return true;
        } catch (NoSuchUserException e) {
            //
            return false;
        }
    }

    /**
     * Sets a {@code User} for deletion.
     *
     * @param issuerSub the {@code IssuerSub} of the {@code User}
     * @return whether the {@code User} is set for deletion
     * @throws SQLException when a database operation fails
     */
    public boolean setToRemoveUser(IssuerSub issuerSub) throws SQLException {
        try {
            UserDB.setUserDeletion(UserDB.getIfExistUser(issuerSub), true);
            return true;
        } catch (NoSuchUserException e) {
            //
            return false;
        }
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final UserManager uniqueInstance = new UserManager();
    }
}
