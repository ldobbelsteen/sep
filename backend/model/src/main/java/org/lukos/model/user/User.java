package org.lukos.model.user;

import lombok.Getter;
import org.lukos.database.PlayerDB;
import org.lukos.database.UserDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.instances.GameAlreadyStartedException;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;
import org.lukos.model.exceptions.user.AlreadyInGameException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;

/**
 * The user class contains some basic information about a single user.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 21-02-2022
 */
@Getter
public class User {

    /** Unique identifier for this user */
    private final int uid; //Read only

//    /** Google account ID for user */
//    private final String sub;
//    private final String issuer;
//
//    /** This users username */
//    @Setter
//    private String username;
//
//    /** Player object associated with this user */
//    private Player player;

    //** Time and date that this user last logged on */
    //private LocalDateTime lastLogin;

    //** Time and date that this user last logged out */
    //private LocalDateTime lastLogout;

//    /**
//     * Constructor for {@code User} with a random {@code id} and default {@code username}.
//     *
//     * @param issuerSub issuerSub record for Google id
//     * @throws SQLException
//     */
//    User(IssuerSub issuerSub) throws SQLException {
//        this(issuerSub.issuer(), issuerSub.sub(), UUID.randomUUID());
//    }
//
//    /**
//     * Constructor for {@code User} with a random {@code id} and default {@code username}.
//     *
//     * @param issuer the issuer as String
//     * @param sub the sub as String
//     * @throws SQLException
//     */
//    User(String issuer, String sub) throws SQLException {
//        this(issuer, sub, UUID.randomUUID());
//    }
//
//    /**
//     * Constructor for {@code User} with a pre-specified {@code id} and default {@code username}.
//     *
//     * @param issuer the issuer as String
//     * @param sub the sub as String
//     * @param id Pre-set user ID of this user
//     * @throws SQLException
//     */
//    User(String issuer, String sub, UUID id) throws SQLException {
//        this(issuer, sub, id, "Username");
//    }

    /**
     * Constructor for {@code User} with a pre-specified {@code id}.
     * <p>
     * <p>
     * //     * @param issuer the issuer as String //     * @param sub the sub as String
     *
     * @param uid Pre-set user ID of this user //     * @param username Pre-set the username of this user
     */
    public User(int uid) {
        this.uid = uid;
//        try {
//            UserDB.addNewUser(uid, issuer, sub, username);
//        } catch (SQLException ignore) {}
//         // this method doesnt check if the user exists, but an error will be thrown if the userID is already in
//         the database
    }

    public String getSub() throws SQLException {
        return UserDB.findUserByID(this.uid).getString("sub");
    }

    public String getIssuer() throws SQLException {
        return UserDB.findUserByID(this.uid).getString("issuer");
    }

    public String getUsername() throws SQLException {
        return UserDB.findUserByID(this.uid).getString("username");
    }

    public void setUsername(String username) throws SQLException {
        UserDB.setUsernameByID(this.uid, username);
    }

    public Player getPlayer() throws SQLException, NoSuchPlayerException {
        return new Player(PlayerDB.getPlayerFromUserByID(this.uid));
    }

    private void setPlayer(Player player) throws SQLException {
        //UserDB.setPlayerByID(this.uid, player);
        PlayerDB.addNewPlayer(player.getPlayerIdentifier());
    }

    /**
     * Creates a new game instance with this user as game master.
     *
     * @param name The name of the game instance
     * @param SEED the seed used for all the randomness in the game
     * @return the ID of the newly created {@code Instance}
     * @throws AlreadyInGameException  if this user is already in a game
     * @throws NoSuchInstanceException if the created game is not found
     */
    public int createGame(String name, int SEED) throws GameException, SQLException {
        try {
            // Check whether player is already in a game;
            // if so, return an AlreadyInGameException,
            // since joining 2 games is forbidden.
            getPlayer();
            throw new AlreadyInGameException("This user (id: " + this.uid + ") is already in a game!");
        } catch (NoSuchPlayerException e) {
            // Is expected
        }
        //checkIfInGame();
        // Assert this user is not in a game

        // Get the instance manager
        InstanceManager im = InstanceManager.getInstanceManager();

        // Create a new game
        int iid = im.createInstance(uid, name, SEED);

        // Join the game
        this.joinGame(iid);

        // return the instance ID of the current game
        return iid;
    }

    /**
     * This user will join a game with id gameId
     *
     * @param gameId the id of the game the user joins
     * @throws AlreadyInGameException  if this user is already in a game
     * @throws NoSuchInstanceException if the game with {@code gameId} is not found
     */
    public void joinGame(int gameId) throws GameException, SQLException {
        try {
            getPlayer();
            throw new AlreadyInGameException("This user (id: " + this.uid + ") is already in a game!");
        } catch (NoSuchPlayerException e) {
            // Is expected
        }
        //checkIfInGame();
        // Assert this user is not in a game

        // Get the instance manager
        InstanceManager im = InstanceManager.getInstanceManager();

        // Get the game instance
        IInstance game = im.getInstance(gameId);

        // Create a player
        setPlayer(new Player(gameId, uid));
//
//        // Add the player to the game
//        game.addPlayer(getPlayer());
    }

    /**
     * If the {@code instance} has not started, removes this user form the game. Otherwise, it throws an exception.
     *
     * @param gameId the instance to leave
     * @throws SQLException  something goes wrong while querying then database
     * @throws GameException if the instance has already started
     */
    public void leaveGame(int gameId) throws SQLException, GameException {
        // TODO: Check to see whether player is actually in the game
        IInstance instance = InstanceManager.getInstanceManager().getInstance(gameId);
        if (instance.isStarted()) {
            throw new GameAlreadyStartedException("The game has already started");
        }

        instance.removePlayer(this.getPlayer());
    }

}
