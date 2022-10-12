package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;

import java.time.Instant;

/**
 * Action used to cancel a goodbye(?)
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class CancelGoodbye extends Action {

    public CancelGoodbye() {
        super("CancelGoodbye");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) {

    }
}
