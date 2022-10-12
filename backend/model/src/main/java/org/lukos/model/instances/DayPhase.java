package org.lukos.model.instances;

/** Enumeration for different stages of an in-game day */
public enum DayPhase {
    NIGHT, MORNING, DAY, VOTE, EXECUTION, EVENING;

    // Local variable that stores each value
    private static final DayPhase[] values = values();

    /**
     * A call to this method shifts the enumerator to the next value.
     *
     * @return next ordinal phase
     */
    public DayPhase next()
    {
        return values[(this.ordinal()+1) % values.length];
    }

    /**
     * A call to this method shifts the enumerator to the previous value.
     *
     * @return previous ordinal phase
     */
    public DayPhase previous()
    {
        return values[((this.ordinal()-1) + values.length) % values.length];
    }
}