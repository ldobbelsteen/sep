package org.lukos.model.actionsystem;

import java.time.Instant;
import org.lukos.model.exceptions.GameException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for composite actions
 * <p>
 * Extends the abstract class Action, and it is composed of multiple actions - the children of the composite action.
 * CompAction has the ability to add and remove child-actions from itself
 *
 * @author Lucas Gether-Rønning
 * @since 25-02-22
 */
public class CompAction extends Action {

    private final List<ActionDT> children; // child-components of the CompAction

    /**
     * Constructs a {@code CompAction}
     *
     * @param name Name of the composite action
     */
    public CompAction(String name) {
        super(name);
        this.children = new ArrayList<>();
    }

    /**
     * Method used to get the child-actions of a composite action
     *
     * @return The children of the composite action
     */
    public List<ActionDT> getChildren() {
        return children;
    }

    /**
     * Method used to add a child-component c to a composite action
     *
     * @param c Component to add
     */
    public void add(ActionDT c) {
        children.add(c);
    }

    /**
     * Method used to remove a child-component c from a composite action
     *
     * @param c Component to remove
     */
    public void remove(ActionDT c) {
        for (ActionDT child: children) {
            if (child.equals(c)) {
                children.remove(child);
                break;
            }
        }
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) throws ReflectiveOperationException, SQLException, GameException {
        for (ActionDT action : this.children) {
            action.action().execute(data, time, actionId);
        }
    }
}
