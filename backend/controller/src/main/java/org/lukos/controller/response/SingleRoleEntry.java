package org.lukos.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.lukos.model.rolesystem.Group;

/**
 * An entry that holds information about a single {@code Role}.
 *
 * @param name  the name of the {@code Role}.
 * @param group the {@link Group} of the {@code Role}.
 * @author Rick van der Heijden (1461923)
 * @since 23-03-2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SingleRoleEntry(String name, Group group) {
}
