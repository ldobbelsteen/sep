package org.lukos.model.actionsystem;

import java.time.Instant;

/**
 * Record for all information regarding the action
 * <p>
 * Holds all action-data
 *
 * @param time              A time the action has been executed
 * @param action            The given action to be performed
 * @param preAction         Additional information regarding the action
 * @author Lucas Gether-Rønning
 * @since 23-02-2022
 */
public record ActionDT(Instant time, Action action, PreActionDT preAction) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof ActionDT otherObject) {
            return  this.time.equals(otherObject.time) &&
                    this.action.getName().equals(otherObject.action.getName()) &&
                    this.preAction.equals(otherObject.preAction);
        }
        return false;
    }
}
