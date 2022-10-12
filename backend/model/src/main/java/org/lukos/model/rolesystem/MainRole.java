package org.lukos.model.rolesystem;

/**
 * Main roles
 * <p>
 * Every player has one of the main roles. They each have a {@code CharacterType} and a (goal){@code Group}.
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
public abstract class MainRole extends Role {

    /**
     * Constructs a {@code MainRole}
     *
     * @param character Charactertype of role
     * @param group     Associated goal group of role
     */
    public MainRole(CharacterType character, Group group) {
        super(character, group);
    }
}

