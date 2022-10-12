package org.lukos.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.lukos.model.instances.DayPhase;

/**
 * An entry to hold information about a game.
 *
 * @param dayPhase the current {@link DayPhase} of the game.
 * @param day      the current day of the game.
 * @author Rick van der Heijden (1461923)
 * @since 23-03-2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GameEntry(DayPhase dayPhase, Integer day) {
}
