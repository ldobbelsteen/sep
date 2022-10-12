package org.lukos.model.actionsystem.actions;

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

import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.*;

/**
 * Action used to change role. The donor player is the player on the player target list. The recipient is the requester
 * of the action. This action gives a new copy the {@code MainRole} of the donor to the recipient. IMPORTANT: this
 * action immediately unlocks the message! (this is used by the graverobber)
 *
 * @author Lucas Gether-Rønning
 * @author Valentijn van den Berg (1457446)
 * @since 26-02-22
 * @since 25-03-22
 */
public class ChangeRole extends Action {

    public ChangeRole() {
        super("ChangeRole");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws GameException, ReflectiveOperationException, SQLException {
        // Extract the player
        Player donorPlayer = getPlayerByUserID(data.data().players().get(0).userID());
        Player recipient = new Player(data.playerIdentifier());

        // Extract new role
        MainRole newRole = donorPlayer.getMainRole();

        // Create new instance of the mainRole of donor and give it to the recipient
        recipient.setMainRole(newRole);

        List<String> fields = new ArrayList<>();
        // Add the donor player's username
        fields.add(getUsernameByUserID(donorPlayer.getPlayerIdentifier().userID()));
        // Add the new role
        fields.add(newRole.getClass().getSimpleName());

        // Add player to Werewolves chat if the chosen role is werewolf
        if (isWolf(donorPlayer.getPlayerIdentifier())) {
            addPlayerToWolvesChat(data.playerIdentifier());
        }

        /* Write the message to the database for Graverobber. */
        int messageId = addMessage(ActionMessages.CHANGED_ROLE_MESSAGE, fields, actionId, data.playerIdentifier());

        /* Write the message for the rest of the players. */
        List<PlayerIdentifier> recipients = getPlayersByInstanceID(data.playerIdentifier().instanceID());
        recipients = recipients.stream()
                .filter(playerIdentifier -> playerIdentifier.userID() != data.playerIdentifier().userID()).toList();
        List<Integer> messageIds =
                addMessages(ActionMessages.CHANGED_ROLE_GLOBAL_MESSAGE, new ArrayList<>(), actionId, recipients);

        messageIds.add(messageId);

        // Mark messages as 'NOT_SEND'
        for (int message : messageIds) {
            unlockMessageByMessageID(message);
        }
    }
}
