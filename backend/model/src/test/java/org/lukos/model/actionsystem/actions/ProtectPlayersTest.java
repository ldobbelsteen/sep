package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.Test;
import org.lukos.database.ActionMessagesDB;
import org.lukos.database.PlayerDB;
import org.lukos.model.actionsystem.*;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link ProtectPlayers}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class ProtectPlayersTest extends ActionTest {

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code ProtectPlayers} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("ProtectPlayers", new ProtectPlayers().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * compare the action messages
     *
     * @param playerIdentifier the player whose messages to check
     * @param fields the expected data
     * @throws SQLException database error
     */
    private void compareMessages(PlayerIdentifier playerIdentifier, List<List<String>> fields) throws SQLException {
        List<Integer> messageIds = ActionMessagesDB.getAllNotSendMessagesForUser(playerIdentifier);
        // Only one message should have been written
        assertEquals(2, messageIds.size(), "Should only contain two messages!");
        // Test that the content is as expected
        for (int mid: messageIds) {
            ActionMessageDT message = ActionMessagesDB.getMessage(mid);
            assertEquals(ActionMessages.PROTECT_PLAYER_MESSAGE, message.messageType(), "Type mismatch!");
            assertTrue(fields.stream().anyMatch(field -> field.equals(message.data())), "Fields mismatch!");
        }
    }

    /**
     * test all functionality of {@code ProtectPlayers}
     *
     * @utp.description Test whether the target {@code players} are correctly set and that the messages are sent out correctly.
     */
    @Test
    public void protectTest() {
        try {
            // create actionDT
            ArrayList<PlayerIdentifier> list = new ArrayList<>();
            list.add(player.getPlayerIdentifier());
            list.add(secondPlayer.getPlayerIdentifier());
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), list);
            PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);
            ActionDT actionDT = new ActionDT(Instant.now(), new ProtectPlayers(), preActionDT);

            // Add action to manager
            ActionManager.addAction(actionDT);

            // perform action
            ActionManager.performActions(instanceId);
            ActionMessagesDB.unlockMessages(instanceId);

            List<PlayerIdentifier> protectedPlayers = PlayerDB.getProtectedPlayers(instanceId);

            // Test that the players are protected
            assertTrue(protectedPlayers.contains(player.getPlayerIdentifier()), "Player should be protected!");
            assertTrue(protectedPlayers.contains(secondPlayer.getPlayerIdentifier()), "secondPlayer should be protected!");

            // Test that the performer got the messages
            ArrayList<List<String>> finalList = new ArrayList<>();
            finalList.add(new ArrayList<>(Collections.singleton(getUsername(player))));
            finalList.add(new ArrayList<>(Collections.singleton(getUsername(secondPlayer))));

            compareMessages(player.getPlayerIdentifier(), finalList);

        } catch (Exception e) {
            fail("An exception was thrown in beforeEach: " + e);
        }
    }
}
