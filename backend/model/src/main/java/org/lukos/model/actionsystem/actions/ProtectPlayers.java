package org.lukos.model.actionsystem.actions;

import org.lukos.database.ActionMessagesDB;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.user.UserException;
import org.lukos.model.user.player.Player;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This will protect a list of players from being killed.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 30-03-22
 */
public class ProtectPlayers extends Action {

    public ProtectPlayers() {
        super("ProtectPlayers");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) throws SQLException, UserException {
        // Protect all the players
        for (PlayerIdentifier playerID : data.data().players()) {
            Player player = UserManager.getInstance().getUser(playerID.userID()).getPlayer();
            // Set the player as protected
            player.setProtected(true);

            // Add message to the database
            ActionMessagesDB.addNewMessage(
                    new ActionMessageDT(ActionMessages.PROTECT_PLAYER_MESSAGE,
                        new ArrayList<>( Collections.singleton(
                            // Add the protected player's username to the message
                            new User(player.getPlayerIdentifier().userID()).getUsername()
                    ))),
                    actionId,
                    data.playerIdentifier() );
        }
    }
}
