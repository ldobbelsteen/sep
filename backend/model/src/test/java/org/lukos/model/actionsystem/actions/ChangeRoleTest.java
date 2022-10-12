package org.lukos.model.actionsystem.actions;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.actionsystem.ActionDT;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.ActionManager;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.rolesystem.roles.mainroles.Graverobber;
import org.lukos.model.rolesystem.roles.mainroles.Poisoner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link ChangeRole}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 15-04-2022
 */
public class ChangeRoleTest extends ActionTest {


    private ActionDT actionDT;

    @BeforeEach
    public void beforeEach() {
        assertNotNull(player, "Player is null!");
        assertNotNull(secondPlayer, "secondPlayer is null!");

        // Second player as target
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier())));
        PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);
        actionDT = new ActionDT(Instant.now(), new ChangeRole(), preActionDT);

        try {
            player.setMainRole(new Graverobber());
        } catch (Exception e) {
            fail("An exception was thrown in beforeEach: " + e);
        }
    }

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code ChangeRole} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("ChangeRole", new ChangeRole().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * Try steal a different role.
     *
     * @utp.description Test whether the new {@code Role} is correctly applied to the initiator.
     */
    @Test
    public void stealRole() {
        try {
            // Set main role of secondPlayer
            secondPlayer.setMainRole(new Poisoner());

            // Execute the action
            ActionManager.addAction(actionDT);
            ActionManager.performActions(instanceId);

            // Test
            assertEquals(Poisoner.class, player.getMainRole().getClass(), "Player should have Poisoner role!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

    /**
     * Try steal a GraveRobber role.
     *
     * @utp.description Test whether the new {@code GraveRobber} {@code Role]} is correctly applied to the initiator.
     */
    @Test
    public void stealGraveRobber() {
        try {
            // Set main role of secondPlayer
            secondPlayer.setMainRole(new Graverobber());

            // Execute the action
            ActionManager.addAction(actionDT);
            ActionManager.performActions(instanceId);

            // Test
            assertEquals(Graverobber.class, player.getMainRole().getClass(), "Player should have Poisoner role!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }

}