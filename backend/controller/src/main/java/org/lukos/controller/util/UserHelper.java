package org.lukos.controller.util;

import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.exceptions.user.UserException;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.sql.SQLException;
import java.util.Objects;

import static org.lukos.controller.util.PlayerHelper.playerInGame;

/**
 * Helper class with methods that can identify and check users. Methods assume that the caller has permission to call
 * said method.
 *
 * @author Rick van der Heijden (1461923)
 * @since 09-03-2022
 */
public class UserHelper {

    /** The models {@code UserManager}. */
    private static final UserManager UM = UserManager.getInstance();

    /**
     * Returns a {@code User} from a {@code OAuth2User}.
     *
     * @param principal The {@code OAuth2User}
     * @return The {@code User}
     * @throws NoSuchUserException  if the {@code User} does not exist
     * @throws NullPointerException if the ID of the {@code OAuth2User} is {@code null}
     */
    public static User getUser(OAuth2User principal) throws SQLException, UserException, NullPointerException {
        String sub = principal.getAttribute("sub");
        // TODO: Check for null
        String issuer = Objects.requireNonNull(principal.getAttribute("iss")).toString(); // iss is a java.net.URL
        String username = principal.getAttribute("name");
        if (sub == null) {
            throw new NullPointerException("User has no id!");
        }
        return getUser(issuer, sub, username);
    }

    // TODO: update javadoc

    /**
     * Returns a {@code User} based on an ID.
     *
     * @return The {@code User}
     */
    public static User getUser(String issuer, String sub, String username) throws UserException, SQLException {
        return UM.getAndCreateUser(new IssuerSub(issuer, sub), username);
    }

    /**
     * Returns a {@code User} based on an ID.
     * <p>
     * //     * @param id The ID
     *
     * @return The {@code User}
     * @throws NoSuchUserException if the {@code User} does not exist
     */
    public static User getUser(int uuid) throws NoSuchUserException, SQLException {
        return UM.getUser(uuid);
    }

    /**
     * Checks whether the {@code User} is already part of the {@code Instance} with ID {@code gid}.
     *
     * @param gid  The {@code Instance} id
     * @param user The {@code User}
     * @return Whether the user is in the game or not
     */
    public static boolean userInGame(int gid, User user) throws SQLException, NoSuchPlayerException {
        return user != null && playerInGame(gid, user.getPlayer());
    }

    public static void setToRemoveUser(IssuerSub issuerSub) throws SQLException {
        UM.setToRemoveUser(issuerSub);
    }
}
