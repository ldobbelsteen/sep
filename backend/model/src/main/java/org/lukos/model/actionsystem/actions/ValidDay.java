package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;

import java.time.Instant;

/**
 * Action used to check validity of day(?)
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class ValidDay extends Action {

    public ValidDay() {
        super("ValidDay");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) {

    }
}
