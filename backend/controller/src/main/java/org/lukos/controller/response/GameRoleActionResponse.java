package org.lukos.controller.response;

import org.lukos.model.rolesystem.RoleActionInformation;

import java.util.List;

/**
 * The response datatype that sends a list with information about all actions of a roles.
 *
 * @author Rick van der Heijden (1461923)
 * @since 04-04-2022
 */
public class GameRoleActionResponse extends SuccessResponse {
    /** The list with information about the actions of the roles of the response. */
    private final List<RoleActionInformation> actions;

    /**
     * Constructor for responses of {@code GameRoleActionResponse}.
     *
     * @param message the message of the response
     * @param actions the information about the actions of the roles of the responses
     */
    public GameRoleActionResponse(String message, List<RoleActionInformation> actions) {
        super(message);
        this.actions = actions;
    }

    /**
     * Returns the information about the actions of the roles of the response.
     *
     * @return the information about the actions
     */
    public List<RoleActionInformation> getActions() {
        return actions;
    }
}
