package org.lukos.model.rolesystem;

import lombok.Getter;

/**
 * Represents a role with a certain purpose
 * <p>
 * Each role has a goal-group associated to it, deciding with what group the role can win. Roles extend the class
 * Purpose, specifying associated player and character-type
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
@Getter
public abstract class Role extends Purpose {
    private final Group group; //Goal-group of the role

    /**
     * Constructs a {@code Role}.
     *
     * @param character The character type of a role
     * @param group     Goal group of the given role
     */
    public Role(CharacterType character, Group group) {
        super(character);
        this.group = group;
    }
}
