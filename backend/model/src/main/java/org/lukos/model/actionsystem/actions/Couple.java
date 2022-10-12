package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;

import java.time.Instant;

/**
 * Action used to assign players to a couple
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class Couple extends Action {

    public Couple() {
        super("Couple");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) {

    }
}
