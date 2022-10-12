package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype that gives a list of roles.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class RoleResponse extends SuccessResponse {
    /** The list of roles of the response. */
    private final List<SingleRoleEntry> playerRoles;

    /**
     * Constructor for responses of {@code RoleResponse}.
     *
     * @param playerRoles the list of roles of the response
     */
    public RoleResponse(List<SingleRoleEntry> playerRoles) {
        super(null); // TODO: give message
        this.playerRoles = playerRoles;
    }

    /**
     * Returns the list of roles of the response.
     *
     * @return the list of roles
     */
    public List<SingleRoleEntry> getPlayerRoles() {
        return playerRoles;
    }
}
