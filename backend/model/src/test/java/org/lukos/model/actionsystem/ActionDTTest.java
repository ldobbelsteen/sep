package org.lukos.model.actionsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.actionsystem.actions.ChangeRole;
import org.lukos.model.actionsystem.actions.RevivePlayers;
import org.lukos.model.user.PlayerIdentifier;

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for {@link ActionDT}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 15-04-2022
 */
public class ActionDTTest {

    /** ActionDT used for testing */
    private ActionDT actionDT;

    @BeforeEach
    public void beforeEach() {
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>());
        PreActionDT preActionDT = new PreActionDT(new PlayerIdentifier(1, 1), actionEnc);
        actionDT = new ActionDT(Instant.now(), new ChangeRole(), preActionDT);
    }

    /**
     * equal test, we compare the object to itself, true should be returned.
     *
     * @utp.description Test whether {@code true} is returned when an object is compared to itself.
     */
    @Test
    public void equalSameTest() {
        assertTrue(actionDT.equals(actionDT), "Should return true when comparing the object with itself!");
    }

    /**
     * equal test, we compare the object to a copy of itself, true should be returned.
     *
     * @utp.description Test whether {@code true} is returned when an object is compared to a copy of itself.
     */
    @Test
    public void equalDifferentTest() {
        ActionDT copy = new ActionDT(actionDT.time(), actionDT.action(), actionDT.preAction());

        assertTrue(actionDT.equals(copy), "Should return true when comparing the object with a copy of itself!");
    }

    /**
     * equal test, we compare the object to an object with different time, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object with different {@code time} parameter.
     */
    @Test
    public void equalNotEqualTimeTest() {
        ActionDT copy = new ActionDT(Instant.now(), actionDT.action(), actionDT.preAction());

        assertFalse(actionDT.equals(copy), "Should return false!");
    }

    /**
     * equal test, we compare the object to an object with different action, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object with different {@code action} parameter.
     */
    @Test
    public void equalNotEqualActionTest() {
        ActionDT copy = new ActionDT(actionDT.time(), new RevivePlayers(), actionDT.preAction());

        assertFalse(actionDT.equals(copy), "Should return false!");
    }

    /**
     * equal test, we compare the object to an object with different preAction, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object with different {@code preAction} parameter.
     */
    @Test
    public void equalNotEqualPreActionTest() {
        ActionDT copy = new ActionDT(actionDT.time(), actionDT.action(), new PreActionDT(new PlayerIdentifier(2, 1), new ActionEnc(new ArrayList<>(), new ArrayList<>())));

        assertFalse(actionDT.equals(copy), "Should return false!");
    }

    /**
     * equal test, we compare the object to an object of a different type, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object of a different type.
     */
    @Test
    public void equalDifferentTypeTest() {
        assertFalse(actionDT.equals(new ChangeRole()), "Should return false when comparing different types!");
    }
}
