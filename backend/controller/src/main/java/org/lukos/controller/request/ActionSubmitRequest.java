package org.lukos.controller.request;

import org.lukos.model.rolesystem.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * The request datatype for the submitting an action.
 *
 * @author Rick van der Heijden (1461923)
 * @since 04-04-2022
 */
public class ActionSubmitRequest {
    /** The action of the submitted action */
    private Action action;
    /** The ID of the submitted players. */
    private List<Integer> playerIDs;
    /** The ID of the submitted homes. */
    private List<Integer> houseIDs;
    /** The ID of the submitted bridges. */
    private List<Integer> bridgeIDs;

    public ActionSubmitRequest() {
        this.playerIDs = new ArrayList<>();
        this.houseIDs = new ArrayList<>();
        this.bridgeIDs = new ArrayList<>();
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<Integer> getPlayerIDs() {
        return playerIDs;
    }

    public void setPlayerIDs(List<Integer> playerIDs) {
        this.playerIDs = playerIDs;
    }

    public List<Integer> getHouseIDs() {
        return houseIDs;
    }

    public void setHouseIDs(List<Integer> houseIDs) {
        this.houseIDs = houseIDs;
    }

    public List<Integer> getBridgeIDs() {
        return bridgeIDs;
    }

    public void setBridgeIDs(List<Integer> bridgeIDs) {
        this.bridgeIDs = bridgeIDs;
    }
}
