package org.lukos.model.actionsystem.actions;

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
 * @author Valentijn van den Berg (1457446)
 * @since 05-04-22
 */
public class KillMarkedPlayersNight extends Action {

    public KillMarkedPlayersNight() {
        super("KillMarkedPlayers");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws SQLException, ReflectiveOperationException, GameException {
        KillMarkedPlayerExecution.execute(data, actionId, ActionMessages.NIGHT_KILL_BROADCAST_MESSAGE);
    }
}
