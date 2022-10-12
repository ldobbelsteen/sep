package org.lukos.controller.response;

import org.lukos.model.rolesystem.Group;

/**
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
public record PlayerEntry(SimplePlayerEntry simplePlayerEntry, Group group) {
}
