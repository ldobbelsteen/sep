package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;

import java.time.Instant;

/**
 * Action used for people at a location(?)
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class PeopleLocation extends Action {

    public PeopleLocation() {
        super("PeopleLocation");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) {

    }
}
