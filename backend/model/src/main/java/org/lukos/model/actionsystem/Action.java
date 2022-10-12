package org.lukos.model.actionsystem;

import lombok.Getter;
import org.lukos.model.exceptions.GameException;

import java.sql.SQLException;
import java.time.Instant;

/**
 * Abstract class for actions
 * <p>
 * Overarching class containing the abstract method for executing the specific action, and its name
 *
 * @author Lucas Gether-Rønning
 * @since 23-02-2022
 */
public abstract class Action {
    @Getter
    private final String name; // name of the action

    /**
     * Constructs an {@code Action}
     *
     * @param name The name of the action
     */
    public Action(String name) {
        this.name = name;
    }

    /**
     * Abstract method for executing an action.
     *
     * @param data Data used for executing the action
     * @param time the time the action was created (can be used for messaging system)
     * @param actionId the id of this action
     * @throws SQLException when there is a database failure
     * @throws GameException when something with the gameObjects goes wrong (e.g. instance or player)
     * @throws ReflectiveOperationException when something goes wrong
     */
    public abstract void execute(PreActionDT data, Instant time, int actionId)
            throws SQLException, ReflectiveOperationException, GameException;

}
