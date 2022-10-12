package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.Test;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.user.PlayerIdentifier;

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for {@link ValidDay}.
 * This class is not used in the current version of the program, so the test are very simple.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class ValidDayTest {

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code ValidDay} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("ValidDay", new ValidDay().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /** @utp.description Tests whether {@code execute()} behaves as intended. */
    @Test
    public void executeTest() {
        try {
            (new ValidDay()).execute(new PreActionDT(new PlayerIdentifier(1, 1),
                    new ActionEnc(new ArrayList<>(), new ArrayList<>())), Instant.now(), 0);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
