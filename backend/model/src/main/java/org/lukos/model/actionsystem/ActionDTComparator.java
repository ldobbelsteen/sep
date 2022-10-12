package org.lukos.model.actionsystem;

import java.time.Instant;
import java.util.Comparator;

/**
 * Comparator which compares {@link Instant} objects in the given {@link ActionDT}s.
 */
public class ActionDTComparator implements Comparator<ActionDT> {
    /**
     * Compares its two arguments for order.
     * Returns a negative integer, zero, or a positive integer as
     * the first argument is less than, equal to, or greater than the second.
     * @param o1 object 1
     * @param o2 object 2
     * @return whether object 1 is greater than object 2 (-1 implies 1 s.t. 2; 0 implies 01 eq. 02; 1 implies o1 g.t. o2)
     */
    @Override
    public int compare(ActionDT o1, ActionDT o2) {
        return o1.time().compareTo(o2.time());
    }
}
