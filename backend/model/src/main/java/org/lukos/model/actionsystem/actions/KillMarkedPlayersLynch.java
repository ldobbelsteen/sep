package org.lukos.model.actionsystem.actions;

import org.lukos.database.ActionMessagesDB;
import org.lukos.database.ActionsDB;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.actionsystem.actions.util.KillMarkedPlayerExecution;
import org.lukos.model.exceptions.GameException;

import java.sql.SQLException;
import java.time.Instant;

/**
 * Actions used to kill all marked players and add broadcasts to all players.
 *
 * @author Martijn van Andel (1251104)
 * @since 08-04-2022
 */
public class KillMarkedPlayersLynch extends Action {

    public KillMarkedPlayersLynch() {
        super("KillMarkedPlayersLynch");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws SQLException, ReflectiveOperationException, GameException {
        KillMarkedPlayerExecution.execute(data, actionId, ActionMessages.LYNCH_KILL_BROADCAST_MESSAGE);
        ActionMessagesDB.unlockMessageWithActionId(actionId);
    }
}
