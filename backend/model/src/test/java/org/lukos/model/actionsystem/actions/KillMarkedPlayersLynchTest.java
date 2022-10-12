package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.ActionMessagesDB;
import org.lukos.database.InstanceDB;
import org.lukos.model.actionsystem.ActionDT;
import org.lukos.model.actionsystem.ActionManager;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.user.PlayerIdentifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link KillMarkedPlayersLynch}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class KillMarkedPlayersLynchTest extends KillMarkedPlayersTest {

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code KillMarkedPlayersLynch} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("KillMarkedPlayersLynch", new KillMarkedPlayersLynch().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * @utp.description Test whether all players are killed correctly and a death message is broadcast.
     */
    @Test
    public void lynchTest() {
        try {
            ActionDT actionDT = new ActionDT(Instant.now(), new KillMarkedPlayersLynch(), preActionDT);

            // Add action to manager
            ActionManager.addAction(actionDT);

            // perform action
            ActionManager.performActions(instanceId);
            ActionMessagesDB.unlockMessages(instanceId);

            // Test the players are dead
            assertFalse(secondPlayer.alive(), "SecondPlayer should have died!");
            assertFalse(thirdPlayer.alive(), "ThirdPlayer should have died!");

            // Test the messages are sent
            List<List<String>> fields = new ArrayList<>();
            fields.add(new ArrayList<>(Collections.singleton(getUsername(secondPlayer))));
            fields.add(new ArrayList<>(Collections.singleton(getUsername(thirdPlayer))));

            // Test messages
            for (PlayerIdentifier pid : InstanceDB.getAlivePlayers(instanceId)) {
                List<Integer> messageIds = ActionMessagesDB.getAllNotSendMessagesForUser(pid);
                // Only one message should have been written
                assertEquals(2, messageIds.size(), "Should only contain two messages!");
                // Test that the content is as expected
                for (int mid: messageIds) {
                    ActionMessageDT message = ActionMessagesDB.getMessage(mid);
                    assertEquals(ActionMessages.LYNCH_KILL_BROADCAST_MESSAGE, message.messageType(), "Type mismatch!");
                    assertTrue(fields.stream().anyMatch(field -> field.equals(message.data())), "Fields mismatch!");
                }
            }
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
}
