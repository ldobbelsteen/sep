package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.*;

/**
 * Action used to return a map of {@code uid} with {@link CharacterType} of all {@link Player} in {@code
 * data.data().players()}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 26-03-22
 */
public class SeeCharacter extends Action {

    public SeeCharacter() {
        super("SeeCharacter");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws SQLException, ReflectiveOperationException, GameException {

        // Figure out the character type of each player and add to the message
        for (PlayerIdentifier playerID : data.data().players()) {
            Player player = getPlayerByUserID(playerID.userID());
            CharacterType characterType = getCharacterTypeByPlayer(player);

            // Write the message to the database
            List<String> fields = new ArrayList<>();
            fields.add(getUsernameByUserID(playerID.userID()));
            fields.add(characterType.name());
            addMessage(ActionMessages.SEE_CHARACTER_MESSAGE, fields, actionId, data.playerIdentifier());
        }
    }
}
