package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;

import java.time.Instant;

/**
 * Randomly selected action
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class RandomAction extends Action {

    public RandomAction() {
        super("RandomAction");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) {

    }
}
