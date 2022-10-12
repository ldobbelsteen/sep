package org.lukos.model.actionsystem.actions.util;

import org.lukos.database.ActionMessagesDB;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.actionsystem.actions.ActionMessageDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.actionsystem.actions.util.InstanceAndUserHelper.getPlayerByUserID;
import static org.lukos.model.actionsystem.actions.util.InstanceAndUserHelper.getUsernameByUserID;

/**
 * @author Valentijn van den Berg (1457446)
 * @since 06-04-2022
 */
public class KillMarkedPlayerExecution {

    public static void execute(PreActionDT data, int actionId, ActionMessages actionMessage)
            throws SQLException, GameException, ReflectiveOperationException {
        // Get the instance
        IInstance instance = InstanceManager.getInstanceManager().getInstance(data.playerIdentifier().instanceID());

        // Get all players in instance
        List<Player> playersInGame = instance.getPlayerList();

        for (PlayerIdentifier player : data.data().players()) {
            List<String> fields = new ArrayList<>();
            fields.add(getUsernameByUserID(player.userID()));

            for (Player recipient : playersInGame) {
                ActionMessagesDB.addNewMessage(new ActionMessageDT(actionMessage, fields), actionId,
                        recipient.getPlayerIdentifier());
            }

            instance.killPlayer(getPlayerByUserID(player.userID()));
        }
    }
}
