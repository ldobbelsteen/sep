package org.lukos.controller.response;

import java.util.List;

/**
 * The response datatype for sending all the ids that are eligible to be killed by the alpha wolf.
 *
 * @author Rick van der Heijden (1461923)
 * @since 03-04-2022
 */
public class AlphaWolfKillEligibleResponse extends SuccessResponse {
    /** The ids eligible to be killed by the Alpha wolf. */
    private final List<IdEntry> eligible;

    /**
     * Constructor for responses of {@code AlphaWolfKillEligibleResponse}.
     *
     * @param eligible The id eligible to be killed by the Alpha Wolf
     */
    public AlphaWolfKillEligibleResponse(List<IdEntry> eligible) {
        super(null); // TODO: give message
        this.eligible = eligible;
    }

    /**
     * Returns a list of eligible players to be killed by the Alpha Wolf.
     *
     * @return a list of eligible players
     */
    public List<IdEntry> getEligible() {
        return eligible;
    }
}
