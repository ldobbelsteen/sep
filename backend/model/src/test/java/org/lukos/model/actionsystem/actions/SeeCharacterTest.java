package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.Test;
import org.lukos.database.ActionMessagesDB;
import org.lukos.model.actionsystem.*;
import org.lukos.model.rolesystem.roles.mainroles.Graverobber;
import org.lukos.model.rolesystem.roles.mainroles.PrivateInvestigator;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.user.PlayerIdentifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link SeeCharacter}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class SeeCharacterTest extends ActionTest {

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code SeeCharacter} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("SeeCharacter", new SeeCharacter().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * test all functionality of {@code SeeCharacter}
     *
     * @utp.description Test whether the messages are sent out with the correct content.
     */
    @Test
    public void characterTest() {
        try {
            player.setMainRole(new PrivateInvestigator());
            secondPlayer.setMainRole(new Werewolf());
            thirdPlayer.setMainRole(new Graverobber());

            // create actionDT
            ArrayList<PlayerIdentifier> list = new ArrayList<>();
            list.add(player.getPlayerIdentifier());
            list.add(secondPlayer.getPlayerIdentifier());
            list.add(thirdPlayer.getPlayerIdentifier());
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), list);
            PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);
            ActionDT actionDT = new ActionDT(Instant.now(), new SeeCharacter(), preActionDT);

            // Add action to manager
            ActionManager.addAction(actionDT);

            // perform action
            ActionManager.performActions(instanceId);
            ActionMessagesDB.unlockMessages(instanceId);

            // Test that the performer got the messages
            ArrayList<List<String>> fields = new ArrayList<>();
            ArrayList<String> list1 = new ArrayList<>(2);
            list1.add(getUsername(player));
            list1.add(player.getMainRole().getCharacter().name());

            ArrayList<String> list2 = new ArrayList<>(2);
            list2.add(getUsername(secondPlayer));
            list2.add(secondPlayer.getMainRole().getCharacter().name());

            ArrayList<String> list3 = new ArrayList<>(2);
            list3.add(getUsername(thirdPlayer));
            list3.add(thirdPlayer.getMainRole().getCharacter().name());

            fields.add(list1);
            fields.add(list2);
            fields.add(list3);

            List<Integer> messageIds = ActionMessagesDB.getAllNotSendMessagesForUser(player.getPlayerIdentifier());

            assertEquals(3, messageIds.size(), "Should only contain two messages!");
            // Test that the content is as expected
            for (int mid: messageIds) {
                ActionMessageDT message = ActionMessagesDB.getMessage(mid);
                assertEquals(ActionMessages.SEE_CHARACTER_MESSAGE, message.messageType(), "Type mismatch!");
                assertTrue(fields.stream().anyMatch(field -> field.equals(message.data())), "Fields mismatch!");
            }

        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
}
