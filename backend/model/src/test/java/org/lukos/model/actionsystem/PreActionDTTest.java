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
 * Test cases for {@link PreActionDT}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 15-04-2022
 */
public class PreActionDTTest {

    /** PreActionDT used for testing */
    private PreActionDT preActionDT;

    @BeforeEach
    public void beforeEach() {
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>());
        preActionDT = new PreActionDT(new PlayerIdentifier(1, 1), actionEnc);
    }

    /**
     * equal test, we compare the object to itself, true should be returned.
     *
     * @utp.description Test whether {@code true} is returned when an object is compared to itself.
     */
    @Test
    public void equalSameTest() {
        assertTrue(preActionDT.equals(preActionDT), "Should return true when comparing the object with itself!");
    }

    /**
     * equal test, we compare the object to a copy of itself, true should be returned.
     *
     * @utp.description Test whether {@code true} is returned when an object is compared to a copy of itself.
     */
    @Test
    public void equalDifferentTest() {
        PreActionDT copy = new PreActionDT(preActionDT.playerIdentifier(), preActionDT.data());

        assertTrue(preActionDT.equals(copy), "Should return true when comparing the object with a copy of itself!");
    }

    /**
     * equal test, we compare the object to an object with different playerIdentifier, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object with different {@code playerIdentifier} parameter.
     */
    @Test
    public void equalNotEqualTimeTest() {
        PreActionDT copy = new PreActionDT(new PlayerIdentifier(2, 1), preActionDT.data());

        assertFalse(preActionDT.equals(copy), "Should return false!");
    }

    /**
     * equal test, we compare the object to an object with different actionEnc, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object with different {@code actionEnc} parameter.
     */
    @Test
    public void equalNotEqualActionTest() {
        PreActionDT copy = new PreActionDT(preActionDT.playerIdentifier(), new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(new PlayerIdentifier(2, 2)))));

        assertFalse(preActionDT.equals(copy), "Should return false!");
    }

    /**
     * equal test, we compare the object to an object of a different type, false should be returned.
     *
     * @utp.description Test whether {@code false} is returned when an object is compared to an object of a different type.
     */
    @Test
    public void equalDifferentTypeTest() {
        assertFalse(preActionDT.equals(new ChangeRole()), "Should return false when comparing different types!");
    }
}
