package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;

import java.time.Instant;

/**
 * Action used to keep track of buildings drenched by arsonist
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class DrenchedBuildings extends Action {

    public DrenchedBuildings() {
        super("DrenchedBuildings");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) {

    }
}
