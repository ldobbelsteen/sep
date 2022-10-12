package org.lukos.controller.util;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;
import org.lukos.model.exceptions.user.UserAlreadyExistException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.sql.SQLException;

import static org.lukos.controller.util.UserHelper.getUser;
import static org.lukos.controller.util.UserHelper.userInGame;

/**
 * Helper class with methods that can TODO
 *
 * @author Xander Smeets (1325523)
 * @since 10-03-2022
 */

public class InstanceHelper {
    /** The models {@code InstanceManager}. */
    private static final InstanceManager IM = InstanceManager.getInstanceManager();

    /**
     * TODO: fill Javadoc
     *
     * @return TODO: determine whether this should be static
     */
    public static IInstance getInstance(int iid) throws SQLException, NoSuchInstanceException {
        return IM.getInstance(iid);
    }

    /**
     * TODO: proper Javadoc
     * <p>
     * Does some basic stuff and returns the instance
     *
     * @throws UserAlreadyExistException if the user was created simultaneously with this function call
     *                                                                     FIXME: might need name refactoring
     */
    public static IInstance getInstanceWithPermissionsCheck(int iid, OAuth2User principal)
            throws GameException, SQLException {
        IInstance instance = IM.getInstance(iid);
        User user = getUser(principal);

        if (!userInGame(iid, user)) {
            throw new NullPointerException("User is not in a game!");
        }
        return instance;
    }
}
