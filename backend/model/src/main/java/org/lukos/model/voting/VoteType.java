package org.lukos.model.voting;

/** Enumeration of different voting types */
public enum VoteType {
    /** The type that is used for lynching votes. */
    LYNCH,
    /** The type that is used for mayor votes. */
    MAYOR,
    /** The type that is used for alpha wolf votes. */
    ALPHA_WOLF,
    /** The type that is used for all non-predefined votes. */
    MISC
}
