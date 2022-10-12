package org.lukos.model.rolesystem;

/**
 * Double roles
 * <p>
 * A {@code Player} can have additional roles in addition to its {@code MainRole}, which is called a {@code DoubleRole}.
 * They each have a {@code CharacterType} and a (goal) {@code Group} that is deciding before that of a players
 * {@code MainRole}.
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
public abstract class DoubleRole extends Role {

    /**
     * Constructs a {@code DoubleRole}
     *
     * @param character Charactertype of role
     * @param group     Associated goal group of role
     */
    public DoubleRole(CharacterType character, Group group) {
        super(character, group);
    }
}
