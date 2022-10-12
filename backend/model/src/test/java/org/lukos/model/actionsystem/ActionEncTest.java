package org.lukos.model.actionsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.actionsystem.actions.ChangeRole;
import org.lukos.model.user.PlayerIdentifier;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for {@link ActionEnc}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 15-04-2022
 */
public class ActionEncTest {

    /** PreActionDT used for testing */
    private ActionEnc actionEnc;

    @BeforeEach
    public void beforeEach() {
        actionEnc = new ActionEnc(new ArrayList<>(Collections.singleton(1)), new ArrayList<>(Collections.singleton(new PlayerIdentifier(1, 1))));
    }

    /**
     * equal test, we compare the object to itself, true should be returned.
     *
     * @utp.description Test whether {@code true} is returned when an object is compared to itself.
     */
    @Test
    public void equalSameTest() {
        assertTrue(actionEnc.equals(actionEnc), "Should return true when comparing the object with itself!");
    }

    /**
     * equal test, we compare the object to a copy of itself, true should be returned.
     *
     * @utp.description Test whether {@code true} is returned when an object is compared to a copy of itself.
     */
    @Test
    public void equalDifferentTest() {
        ActionEnc copy = new ActionEnc(actionEnc.locations(), actionEnc.players());

        assertTrue(actionEnc.equals(copy), "Should return true when comparing the object with a copy of itself!");
    }

    /**
     * equal test, we compare the object to an object with different locations, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object with different {@code locations} parameter.
     */
    @Test
    public void equalNotEqualTimeTest() {
        ActionEnc copy = new ActionEnc(new ArrayList<>(), actionEnc.players());

        assertFalse(actionEnc.equals(copy), "Should return false!");
    }

    /**
     * equal test, we compare the object to an object with different players, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object with different {@code players} parameter.
     */
    @Test
    public void equalNotEqualActionTest() {
        ActionEnc copy = new ActionEnc(actionEnc.locations(), new ArrayList<>());

        assertFalse(actionEnc.equals(copy), "Should return false!");
    }

    /**
     * equal test, we compare the object to an object of a different type, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object of a different type.
     */
    @Test
    public void equalDifferentTypeTest() {
        assertFalse(actionEnc.equals(new ChangeRole()), "Should return false when comparing different types!");
    }

}
