package org.lukos.model.actionsystem.actions;

import org.lukos.database.ActionMessagesDB;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.getPlayerByUserID;
import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.getUsernameByUserID;

/**
 * Action used to return a map of {@code uid} with {@link MainRole} of all {@link Player} in {@code data.players()}.
 *
 * @author Lucas Gether-Rønning
 * @author Valentijn van den Berg (1457446)
 * @since 26-02-22
 * @since 26-03-22
 */
public class SeeRole extends Action {

    public SeeRole() {
        super("SeeRole");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws ReflectiveOperationException, SQLException, GameException {

        for (PlayerIdentifier playerID : data.data().players()) {
            Player player = getPlayerByUserID(playerID.userID());
            // Get mainRole
            List<String> fields = new ArrayList<>();
            fields.add(getUsernameByUserID(playerID.userID()));
            fields.add(player.getMainRole().getClass().getSimpleName());
            // Write the message to the database
            ActionMessagesDB.addNewMessage(new ActionMessageDT(ActionMessages.SEE_ROLE_MESSAGE, fields), actionId,
                    data.playerIdentifier());
        }

    }
}
