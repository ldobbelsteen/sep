package org.lukos.model.user.player;

import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.Purpose;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * This is a helper class that helps the {@code Player} to perform its {@code Action}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
class PlayerActionHelper {

    /** Private constructor, as it is a helper class, and it enforces static methods */
    private PlayerActionHelper() {
    }

    /**
     * Method used by the {@code Player} to perform its {@code Action}.
     *
     * @param playerID the {@code PlayerIdentifier} of the {@code Player}
     * @param data     the {@code ActionEnc} of the {@code Action}
     * @param action   the {@code Action} itself
     * @param purposes the {@code Purposes} the {@code Player} has
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    static void performAction(PlayerIdentifier playerID, ActionEnc data, Action action, List<Purpose> purposes)
            throws GameException, SQLException, ReflectiveOperationException {
        boolean performedAction = false;
        for (Purpose purpose : purposes) {
            if (purpose.getActions().contains(action)) {
                purpose.performAction(new PreActionDT(playerID, data), action);
                performedAction = true;
                break;
            }
        }
        if (!performedAction) {
            throw new NoPermissionException("The player does not have the permission to execute that action.");
        }
    }
}
