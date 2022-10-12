package org.lukos.model.rolesystem.util;

import org.lukos.model.actionsystem.*;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.actionsystem.InvalidActionException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

/**
 * This class is a facade for communication with the Action system and classes part of it.
 *
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
abstract class ActionHelper extends GeneralUtilHelper {

    /**
     * Creates a new {@code ActionDT} based on a given {@code Instant}, {@code Action}, and {@code PreActionDT}.
     *
     * @param instant the {@code Instant} used to construct the {@code ActionDT}
     * @param action  the {@code Action} used to construct the {@code ActionDT}
     * @param data    the {@code PreActionDT} used to construct the {@code ActionDT}
     * @return the created {@code ActionDT}
     */
    public static ActionDT createNewActionDT(Instant instant, Action action, PreActionDT data) {
        return new ActionDT(instant, action, data);
    }

    /**
     * Creates a new {@code PreActionDT} based on a given {@code PlayerIdentifier} and {@code ActionEnc}.
     *
     * @param playerID the {@code PlayerIdentifier} used to construct the {@code PreActionDT}
     * @param data     the {@code ActionEnc} used to construct the {@code PreActionDT}
     * @return the created {@code PreActionDT}
     */
    public static PreActionDT createNewPreActionDT(PlayerIdentifier playerID, ActionEnc data) {
        return new PreActionDT(playerID, data);
    }

    /**
     * Creates a new {@code ActionEnc} based on a given {@code List} of {@code Location}s and {@code Player}s.
     *
     * @param locations the {@code List} of {@code Location}s
     * @param players   the {@code List} of {@code Player}s
     * @return the created {@code ActionEnc}
     */
    public static ActionEnc createNewActionEnc(List<Integer> locations, List<PlayerIdentifier> players) {
        return new ActionEnc(locations, players);
    }

    /**
     * Adds a {@code ActionDT} to the {@code ActionManager}.
     *
     * @param instant the {@code Instant} of the {@code ActionDT}
     * @param action  the {@code Action} of the {@code ActionDT}
     * @param data    the {@code PreActionDT} of the {@code ActionDT}
     * @throws InvalidActionException when the {@code Action} is invalid
     * @throws SQLException           when a database operation fails
     */
    public static void addAction(Instant instant, Action action, PreActionDT data)
            throws InvalidActionException, SQLException {
        ActionManager.addAction(createNewActionDT(instant, action, data));
    }

    /**
     * Performs a {@code ActionDT} directly.
     *
     * @param data   the {@code PreActionDT} of the {@code ActionDT}
     * @param action the {@code Action} of the {@code ActionDT}
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void performActionNow(PreActionDT data, Action action)
            throws SQLException, ReflectiveOperationException, GameException {
        ActionDT actionDT = createNewActionDT(Instant.now(), action, data);

        int actionId = addNewAction(actionDT);
        executeAction(actionId);

        actionDT.action().execute(data, actionDT.time(), actionId);
    }
}
