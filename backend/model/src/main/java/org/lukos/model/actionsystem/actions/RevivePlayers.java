package org.lukos.model.actionsystem.actions;

import org.lukos.database.ActionMessagesDB;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.revivePlayerByInstanceID;
import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.getPlayerByUserID;
import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.getUsernameByUserID;

/**
 * Revive one or more players {@link Action}
 *
 * @author Valentijn van den Berg (1457446)
 * @author Martijn van Andel (1251104)
 * @since 23-03-2022
 */
public class RevivePlayers extends Action {

    public RevivePlayers() {
        super("RevivePlayers");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws SQLException, GameException, ReflectiveOperationException {
        // Revive all the players on the list
        for (PlayerIdentifier playerID : data.data().players()) {
            Player player = getPlayerByUserID(playerID.userID());
            revivePlayerByInstanceID(data.playerIdentifier().instanceID(), player);

            List<String> fields = new ArrayList<>();
            fields.add(getUsernameByUserID(playerID.userID()));

            // Create message for reviver
            ActionMessagesDB.addNewMessage(new ActionMessageDT(ActionMessages.REVIVE_PLAYERS_MESSAGE, fields), actionId,
                    data.playerIdentifier());

            // Create message for revived player
            ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.YOU_HAVE_BEEN_REVIVED_MESSAGE, new ArrayList<>()), actionId,
                    player.getPlayerIdentifier());
        }
        ActionMessagesDB.unlockMessageWithActionId(actionId);
    }
}
