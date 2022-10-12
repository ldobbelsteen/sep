package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.Test;
import org.lukos.database.SuccessorDB;
import org.lukos.model.actionsystem.*;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.rolesystem.jobs.Mayor;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link SuccessorAction}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class SuccessorActionTest extends ActionTest {

    /**
     * simple constructor test.
     *
     * @utp.description Test whether the {@code SuccessorAction} is initialized correctly.
     */
    @Test
    public void constructorTest() {
        try {
            new SuccessorAction();
        } catch (Exception e) {
            fail("An exception was thrown during the construction of the SuccessorAction: " + e);
        }
    }

    /**
     * exception test, players list == 0
     *
     * @utp.description Test whether a {@code NoPermissionException} is thrown if the {@code Player} target list is empty.
     */
    @Test
    public void exceptionTest1() {
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>());
        PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);

        try {
            SuccessorAction.execute(preActionDT, SuccessorType.MAYOR);
        } catch (NoPermissionException e) {
            assertNotNull(e.getMessage(), "Message should not be null!");
        } catch (Exception e) {
            fail("Wrong exception was thrown: " + e);
        }
    }

    /**
     * exception test, players list == null
     *
     * @utp.description Test whether a {@code NoPermissionException} is thrown if the {@code Player} target list is null.
     */
    @Test
    public void exceptionTest2() {
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), null);
        PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);

        try {
            SuccessorAction.execute(preActionDT, SuccessorType.MAYOR);
        } catch (NoPermissionException e) {
            assertNotNull(e.getMessage(), "Message should not be null!");
        } catch (Exception e) {
            fail("Wrong exception was thrown: " + e);
        }
    }

    /**
     * exception test, playerIdentifier == null
     *
     * @utp.description Test whether a {@code NoPermissionException} is thrown if the {@code PlayerIdentifier} is null.
     */
    @Test
    public void exceptionTest3() {
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier())));
        PreActionDT preActionDT = new PreActionDT(null, actionEnc);

        try {
            SuccessorAction.execute(preActionDT, SuccessorType.MAYOR);
        } catch (NoPermissionException e) {
            assertNotNull(e.getMessage(), "Message should not be null!");
        } catch (Exception e) {
            fail("Wrong exception was thrown: " + e);
        }
    }

    /**
     * execute test.
     *
     * @utp.description Test whether the successor is set correctly.
     */
    @Test
    public void executeTest() {
        try {
            player.addJob(new Mayor());

            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier())));
            PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);

            SuccessorAction.execute(preActionDT, SuccessorType.MAYOR);

            assertEquals(secondPlayer.getPlayerIdentifier(), SuccessorDB.getSuccessor(instanceId, SuccessorType.MAYOR), "The successor was not set correctly!");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
}
