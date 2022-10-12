package org.lukos.model.actionsystem.actions;

import org.lukos.database.SuccessorDB;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;

import java.sql.SQLException;

/**
 * This is the action used to assign a successor for a specific successor type. This is not extending the class {@code
 * Action}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 04-04-2022
 */
public class SuccessorAction {

    /**
     * This method executes the action to assign a successor for a specific successor type.
     *
     * @param data          the data needed to assign the successor
     * @param successorType the successor type
     * @throws GameException if there occurs an exception in the game logic
     * @throws SQLException  if there occurs an exception in the database
     */
    public static void execute(PreActionDT data, SuccessorType successorType) throws GameException, SQLException {
        if (data.playerIdentifier() == null || data.data().players() == null || data.data().players().size() != 1) {
            throw new NoPermissionException("Wrong input for this action");
        }
        IInstance instance = InstanceManager.getInstanceManager().getInstance(data.playerIdentifier().instanceID());
        SuccessorDB.modifyOrCreateSuccessor(instance.getIid(), successorType, data.data().players().get(0));
    }
}
