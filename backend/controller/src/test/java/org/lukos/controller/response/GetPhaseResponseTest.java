package org.lukos.controller.response;

import org.junit.jupiter.api.Test;
import org.lukos.model.instances.DayPhase;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link GetPhaseResponse}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 20-04-2022
 */
public class GetPhaseResponseTest {

    /** @utp.description Tests whether the constructor initializes the variables correctly. */
    @Test
    public void constructorTest() {
        GetPhaseResponse response = new GetPhaseResponse(null);
        assertNull(response.getPhase(), "DayPhase should be null");
        assertNull(response.getMessage(), "Message should be null");
    }

    /** @utp.description Tests whether the function {@code getPhase()} returns the right value. */
    @Test
    public void getPhaseTest() {
        GetPhaseResponse response = new GetPhaseResponse(DayPhase.DAY);
        assertEquals(DayPhase.DAY, response.getPhase(), "DayPhase should be DAY");
        GetPhaseResponse response2 = new GetPhaseResponse(DayPhase.EVENING);
        assertEquals(DayPhase.EVENING, response2.getPhase(), "DayPhase should be EVENING");
        GetPhaseResponse response3 = new GetPhaseResponse(DayPhase.NIGHT);
        assertEquals(DayPhase.NIGHT, response3.getPhase(), "DayPhase should be NIGHT");
    }
}
