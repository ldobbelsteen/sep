package org.lukos.model.actionsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.actionsystem.actions.ChangeRole;
import org.lukos.model.user.PlayerIdentifier;

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for {@link ActionDTComparator}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 13-04-2022
 */
public class ActionDTComparatorTest {

    /** dummy preActionDT used for ActionDTs. */
    PreActionDT preActionDT;

    /** the actionDTComparator used for testing. */
    ActionDTComparator actionDTComparator;

    @BeforeEach
    public void beforeEach() {
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>());
        preActionDT = new PreActionDT(new PlayerIdentifier(1, 1), actionEnc);
    }

    /**
     * compare test, time1 is greater than time2.
     *
     * @utp.description Test whether a positive integer is returned if time1 is greater than time2.
     */
    @Test
    public void compareGreaterTest() {
        Instant time1 = Instant.now();
        Instant time2 = time1.minusNanos(10);

        ActionDTComparator actionDTComparator = new ActionDTComparator();
        int result = actionDTComparator.compare(new ActionDT(time1, new ChangeRole(), preActionDT),
                                                new ActionDT(time2, new ChangeRole(), preActionDT));

        assertTrue(result > 0, "A positive integer should have been returned!");
    }

    /**
     * compare test, time1 is equal to time2.
     *
     * @utp.description Test whether a zero is returned if time1 is equal to time2.
     */
    @Test
    public void compareEqualTest() {
        Instant time1 = Instant.now();

        ActionDTComparator actionDTComparator = new ActionDTComparator();
        int result = actionDTComparator.compare(new ActionDT(time1, new ChangeRole(), preActionDT),
                                                new ActionDT(time1, new ChangeRole(), preActionDT));

        assertEquals(0, result, "Zero should have been returned!");
    }

    /**
     * compare test, time1 is smaller than time2.
     *
     * @utp.description Test whether a negative integer is returned if time1 is smaller than time2.
     */
    @Test
    public void compareLesserTest() {
        Instant time1 = Instant.now();
        Instant time2 = time1.plusNanos(10);

        ActionDTComparator actionDTComparator = new ActionDTComparator();
        int result = actionDTComparator.compare(new ActionDT(time1, new ChangeRole(), preActionDT),
                                                new ActionDT(time2, new ChangeRole(), preActionDT));

        assertTrue(result < 0, "A negative integer should have been returned!");
    }
}