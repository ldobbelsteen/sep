package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.Test;
import org.lukos.database.ActionMessagesDB;
import org.lukos.database.InstanceDB;
import org.lukos.model.actionsystem.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link HealPlayer}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class HealPlayerTest extends ActionTest {

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code HealPlayer} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("HealPlayer", new HealPlayer().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * test all functionality of {@code HealPlayer}.
     *
     * @utp.description Test whether the target {@code Player} is unmarked and the messages are sent out correctly.
     */
    @Test
    public void healTest() {
        try {
            // mark secondPlayer
            InstanceDB.modifyExecuted(instanceId, secondPlayer.getPlayerIdentifier(), true);
            assertTrue(InstanceDB.getToBeExecuted(instanceId).contains(secondPlayer.getPlayerIdentifier()), "SecondPlayer should be marked!");

            // create actionDT
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier())));
            PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);
            ActionDT actionDT = new ActionDT(Instant.now(), new HealPlayer(), preActionDT);

            // Add action to manager
            ActionManager.addAction(actionDT);

            // perform action
            ActionManager.performActions(instanceId);
            ActionMessagesDB.unlockMessages(instanceId);

            List<Integer> messageIds = ActionMessagesDB.getAllNotSendMessagesForUser(player.getPlayerIdentifier());
            // Only one message should have been written
            assertEquals(1, messageIds.size(), "Should only contain one message!");

            List<String> fields = new ArrayList<>(Collections.singleton(getUsername(secondPlayer)));

            // Test that the content is as expected
            ActionMessageDT message = ActionMessagesDB.getMessage(messageIds.get(0));
            assertEquals(ActionMessages.HEALED_PLAYER_MESSAGE, message.messageType(), "Type mismatch!");
            assertEquals(fields, message.data(), "Fields mismatch!");

        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
}