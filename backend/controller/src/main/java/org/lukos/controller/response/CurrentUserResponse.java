package org.lukos.controller.response;

import java.util.ArrayList;
import java.util.List;

/**
 * The response datatype for giving information about the current user.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class CurrentUserResponse extends SuccessResponse {
    /** The issuer of the request. */
    private final String issuer;
    /** The sub of the user. This is a unique ID for the user provided by the authentication provider. */
    private final String sub;
    /** The ID of the user. */
    private final int id;
    /** The IDs of the games the user is in. */
    private final List<Integer> gameId;
    /** The name of the user. */
    private final String name;

    /**
     * Constructor for responses of {@code CurrentUserResponse}.
     *
     * @param issuer The issuer of the request
     * @param sub    The sub of the user
     * @param userId The ID of the user
     * @param gameId The IDs of the games the user is in
     * @param name   The name of the user
     */
    public CurrentUserResponse(String issuer, String sub, int userId, int gameId, String name) {
        super(null); // TODO: give message
        this.issuer = issuer;
        this.sub = sub;
        this.id = userId;
        this.name = name;

        // return a list of game IDs
        // for this implementation, contains only one game ID
        List<Integer> gameIdList = new ArrayList<>();
        gameIdList.add(gameId);
        this.gameId = gameIdList;
    }

    /**
     * Constructor for responses of {@code CurrentUserResponse}.
     *
     * @param issuer The issuer of the request
     * @param sub    The sub of the user
     * @param userId The ID of the user
     * @param name   The name of the user
     */
    public CurrentUserResponse(String issuer, String sub, int userId, String name) {
        super(null); // TODO: give message
        this.issuer = issuer;
        this.sub = sub;
        this.id = userId;
        this.gameId = new ArrayList<>(); // no games joined yet; return empty list
        this.name = name;
    }

    /**
     * Returns the issuer of the request.
     *
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Returns the sub of the user. This is a unique ID for the user provided by the authentication provider.
     *
     * @return the sub of the user
     */
    public String getSub() {
        return sub;
    }

    /**
     * Returns the ID of the user.
     *
     * @return the ID of the user
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the gameIDs of the games where the user is in.
     *
     * @return the gameIDs of the games the user is in
     */
    public List<Integer> getGameId() {
        return gameId;
    }

    /**
     * Returns the name of the user.
     *
     * @return the name of the user
     */
    public String getName() {
        return this.name;
    }
}
