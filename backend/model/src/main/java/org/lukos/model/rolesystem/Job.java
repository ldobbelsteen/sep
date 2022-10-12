package org.lukos.model.rolesystem;

/**
 * Represents a job with a certain purpose
 * <p>
 * A player can get a job with certain abilities under some conditions, for example by a vote (Mayor and AlphaWolf).
 * Each job has a {@code CharacterType}.
 *
 * @author Lucas Gether-Rønning
 * @since 21-02-2022
 */
public abstract class Job extends Purpose {

    /**
     * Constructs a {@code Job}
     *
     * @param character Charactertype of role
     */
    public Job(CharacterType character) {
        super(character);
    }
}
