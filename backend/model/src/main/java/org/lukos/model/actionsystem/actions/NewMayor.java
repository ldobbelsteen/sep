package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.jobs.Mayor;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.*;

/**
 * Assigns the Mayor job to a player and informs the town.
 *
 * @author Martijn van Andel (1251104)
 * @since 08-04-2022
 */
public class NewMayor extends Action {

    /**
     * Constructs an {@code Action}
     */
    public NewMayor() {
        super("NewMayor");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws SQLException, ReflectiveOperationException, GameException {
        /* Assign Mayor role. */
        Player player = new Player(data.data().players().get(0));
        addJob(player, new Mayor());

        /* Notify town. */
        List<PlayerIdentifier> recipients = getPlayersByInstanceID(data.playerIdentifier().instanceID());
        List<String> fields = new ArrayList<>();
        fields.add(getUsernameByUserID(player.getPlayerIdentifier().userID()));
        // Add message to the database
        List<Integer> messages = addMessages(ActionMessages.NEW_MAYOR_MESSAGE, fields, actionId, recipients);

        /* Unlock messages so people see them. */
        for (int message : messages) {
            unlockMessageByMessageID(message);
        }
    }
}
