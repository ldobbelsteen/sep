package org.lukos.controller.response;

import org.lukos.model.actionsystem.actions.ActionMessageDT;

import java.util.List;

/**
 * The response datatype that sends a list with all results of actions.
 *
 * @author Marco Pleket (1295713)
 * @since 05-04-2022
 */
public class ActionResultResponse extends SuccessResponse {
    /** The list with messages for all the performed actions */
    private final List<ActionMessageDT> results;

    /**
     * Constructor for responses of {@code GameRoleActionResponse}.
     *
     * @param message the message of the response
     * @param results the information about all the results of the user's actions
     */
    public ActionResultResponse(String message, List<ActionMessageDT> results) {
        super(message);
        this.results = results;
    }

    /**
     * Returns the list with messages for all the performed actions
     *
     * @return the information about the results of the actions
     */
    public List<ActionMessageDT> getActions() {
        return results;
    }
}
