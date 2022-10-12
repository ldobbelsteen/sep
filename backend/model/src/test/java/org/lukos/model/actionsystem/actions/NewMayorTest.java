package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.Test;
import org.lukos.database.ActionMessagesDB;
import org.lukos.database.InstanceDB;
import org.lukos.model.actionsystem.*;
import org.lukos.model.rolesystem.jobs.Mayor;
import org.lukos.model.user.PlayerIdentifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link NewMayor}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class NewMayorTest extends ActionTest {

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code NewMayor} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("NewMayor", new NewMayor().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * test all functionality of {@code NewMayor}
     *
     * @utp.description Test whether the {@code Mayor} {@code Job} is correctly added and that the messages are sent out correctly.
     */
    @Test
    public void mayorTest() {
        try {
            // create actionDT
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(player.getPlayerIdentifier())));
            PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);
            ActionDT actionDT = new ActionDT(Instant.now(), new NewMayor(), preActionDT);

            // Add action to manager
            ActionManager.addAction(actionDT);

            // perform action
            ActionManager.performActions(instanceId);
            assertTrue(player.getJobs().get(0) instanceof Mayor, "Player should only be mayor!");

            String username = getUsername(player);
            // Test messages
            for (PlayerIdentifier pid : InstanceDB.getAlivePlayers(instanceId)) {
                List<Integer> messageIds = ActionMessagesDB.getAllNotSendMessagesForUser(pid);
                // Only one message should have been written
                assertEquals(1, messageIds.size(), "Should only contain one message!");
                // Test that the content is as expected
                ActionMessageDT message = ActionMessagesDB.getMessage(messageIds.get(0));
                assertEquals(message.messageType(), ActionMessages.NEW_MAYOR_MESSAGE, "Type mismatch!");
                assertEquals(new ArrayList<>(Collections.singleton(username)), message.data(), "Fields mismatch!");
            }
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
}
