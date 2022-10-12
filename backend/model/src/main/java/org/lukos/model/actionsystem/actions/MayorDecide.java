package org.lukos.model.actionsystem.actions;

import org.lukos.database.ActionMessagesDB;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.*;

/**
 * Action from the Mayor to decide who dies in a LYNCH vote.
 *
 * @author Martijn van Andel (1251104)
 * @since 05-04-2022
 */
public class MayorDecide extends Action {

    public MayorDecide() {
        super("MayorDecide");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws SQLException, ReflectiveOperationException, GameException {
        addToBeExecutedByInstanceID(data.playerIdentifier().instanceID(), data.data().players());
        setUndecidedLynchesByInstanceID(data.playerIdentifier().instanceID(), 0);

        List<String> fields = new ArrayList<>();
        // Add the chosen player's usernames
        for (PlayerIdentifier player : data.data().players()) {
            fields.add(getUsernameByUserID(player.userID()));
        }
        List<Integer> messageIds =
                ActionMessagesDB.addNewMessage(new ActionMessageDT(ActionMessages.MAYOR_DECIDE_MESSAGE, fields),
                        actionId, getPlayersByInstanceID(data.playerIdentifier().instanceID()));

        // Mark messages as 'NOT_SEND'
        for (int message : messageIds) {
            ActionMessagesDB.unlockMessage(message);
        }
    }

}
