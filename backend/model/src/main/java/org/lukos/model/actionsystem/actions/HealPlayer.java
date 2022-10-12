package org.lukos.model.actionsystem.actions;

import org.lukos.database.ActionMessagesDB;
import org.lukos.database.InstanceDB;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Will heal the player at {@code data.data().players().get(0)}. Used by the {@code Healer}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 30-03-22
 */
public class HealPlayer extends Action {

    public HealPlayer() {
        super("HealPlayer");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) throws SQLException {
        PlayerIdentifier playerToRevive = data.data().players().get(0);

        InstanceDB.modifyExecuted(data.playerIdentifier().instanceID(), playerToRevive, false);

        List<String> fields = new ArrayList<>(Collections.singleton(new User(playerToRevive.userID()).getUsername()));

        // Add message to healer in the database
        ActionMessagesDB.addNewMessage(new ActionMessageDT(ActionMessages.HEALED_PLAYER_MESSAGE, fields), actionId,
                data.playerIdentifier());
    }
}
