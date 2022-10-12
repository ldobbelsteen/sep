package org.lukos.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.lukos.model.rolesystem.Group;

/**
 * An entry that holds information about a single {@code Group}.
 *
 * @param group the {@link Group} of the {@code Role}.
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GroupEntry(int id, Group group) {
}
