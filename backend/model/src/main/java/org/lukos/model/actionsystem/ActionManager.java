package org.lukos.model.actionsystem;

import org.lukos.database.ActionsDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.actionsystem.InvalidActionException;

import java.sql.SQLException;
import java.util.*;

/**
 * Class implementing the action manager
 * <p>
 * An ActionManager has a list of actions and the option to clear, add, and perform actions.
 *
 * @author Lucas Gether-Rønning
 * @author Valentijn van den Berg (1457446)
 * @author Martijn van Andel (1251104)
 * @since 23-02-2022
 */
public class ActionManager {

    /**
     * Adding an action to its list of actions
     *
     * @param action Action to add to list of managed actions
     */
    public static void addAction(ActionDT action) throws SQLException {
        // Deconstruct compAction into its children
        if (action.action() instanceof CompAction compAction) {
            for (ActionDT subAction: compAction.getChildren()) {
                ActionManager.addAction(subAction);
            }
        }
        ActionsDB.addNewAction(action);
    }

    /**
     * Set all actions for an instance to 'COMPLETE'
     *
     * @param instanceId the id of the instance to complete all actions for
     */
    public static void clear(int instanceId) throws SQLException {
        // Get all not completed actions
        List<Integer> actionIds = ActionsDB.getNotExecutedActions(instanceId);
        actionIds.addAll(ActionsDB.getActions(instanceId, "EXECUTED"));

        // Set all actions to complete
        for (int actionId: actionIds) {
            ActionsDB.completeAction(actionId);
        }
    }

    /**
     * Performs all actions that the {@code ActionManager} holds.
     *
     * @param instanceId the instance for which to perform all actions.
     */
    public static void performActions(int instanceId) throws SQLException, ReflectiveOperationException, GameException {
        // Get the ids of not yet executed actions for this instance
        List<Integer> actionIds = ActionsDB.getNotExecutedActions(instanceId);

        // Get the actions from the database
        Map<ActionDT, Integer> actionMap = new HashMap<>();
        for (int actionId: actionIds) {
            actionMap.put(ActionsDB.getActionFromID(actionId), actionId);
        }

        // Sort the list of action based on the time they arrived
        List<ActionDT> actions = actionMap.keySet().stream().toList();

        // If actions is empty we do not have to perform any actions, so we skip
        if (actions != null && !actions.isEmpty()) {
            actions = actions.stream().sorted(new ActionDTComparator()).toList();

            // Execute all actions
            for (ActionDT action: actions) {
                // Execute the action
                action.action().execute(action.preAction(), action.time(), actionMap.get(action));
                // Get the id that corresponds to the actionDT amd update the database
                ActionsDB.executeAction(actionMap.get(action));
            }
        }
    }

    /**
     * Returns the number of actions in the buffer.
     *
     * @param instanceId the instance for which to perform all actions.
     * @return the number of actions currently stored in the buffer.
     */
    public static int actionsInBuffer(int instanceId) throws SQLException {
        return ActionsDB.getNotExecutedActions(instanceId).size();
    }
}
