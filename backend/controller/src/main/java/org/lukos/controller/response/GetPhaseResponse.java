package org.lukos.controller.response;

import org.lukos.model.instances.DayPhase;

/**
 * The response datatype for giving the current phase.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @since 11-03-2022
 */
public class GetPhaseResponse extends SuccessResponse {
    /** The phase of the response. */
    private final DayPhase phase;

    /**
     * Constructor for responses of {@code GetPhaseResponse}.
     *
     * @param phase The phase in the response
     */
    public GetPhaseResponse(DayPhase phase) {
        super(null); // TODO: give message
        this.phase = phase;
    }

    /**
     * Returns the {@code DayPhase} of this response.
     *
     * @return the {@code DayPhase} of this response
     */
    public DayPhase getPhase() {
        return phase;
    }
}
