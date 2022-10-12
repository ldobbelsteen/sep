package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;

import java.time.Instant;

/**
 * Action used to cancel actions
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class CancelAction extends Action{

    public CancelAction() {
        super("CancelAction");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) {

    }
}
