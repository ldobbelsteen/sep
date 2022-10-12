package org.lukos.model.config;

import org.lukos.model.rolesystem.CharacterType;

import lombok.Getter;

/** Configuration of the {@code CharacterType} of a role/job */
public enum CharacterConfig {
    WEREWOLF(CharacterType.SHADY),
    WEREWOLF_ELDER(CharacterType.SHADY),
    WEREWOLF_CLEANER(CharacterType.SHADY),
    WEREWOLF_FRAMER(CharacterType.NOT_SHADY),
    ARCHER(CharacterType.SHADY),
    ARSONIST(CharacterType.SHADY),
    CLAIRVOYANT(CharacterType.NOT_SHADY),
    CULT_LEADER(CharacterType.VAGUE),
    EXECUTIONER(CharacterType.SHADY),
    GRAVEROBBER(CharacterType.VAGUE),
    GUARDIAN_ANGEL(CharacterType.NOT_SHADY),
    HEALER(CharacterType.NOT_SHADY),
    HITMAN(CharacterType.SHADY),
    JUDGE(CharacterType.NOT_SHADY),
    MATCHMAKER(CharacterType.NOT_SHADY),
    MEDIUM(CharacterType.NOT_SHADY),
    MUTER(CharacterType.NOT_SHADY),
    POISONER(CharacterType.SHADY),
    PRIVATE_INVESTIGATOR(CharacterType.NOT_SHADY),
    SCEPTIC(CharacterType.NOT_SHADY),
    SCRUTINIZER(CharacterType.NOT_SHADY),
    SCOUT(CharacterType.NOT_SHADY),
    SOMMELIER(CharacterType.NOT_SHADY),
    STALKER(CharacterType.SHADY),
    TOWNSPERSON(CharacterType.NOT_SHADY),
    FOLLOWER(CharacterType.VAGUE),
    JESTER(CharacterType.VAGUE),
    LOVER(CharacterType.UNDEFINED),
    WOLF_CUB(CharacterType.VAGUE),
    ALPHA_WOLF(CharacterType.SHADY),
    MAYOR(CharacterType.NOT_SHADY),
    GATEKEEPER(CharacterType.UNDEFINED),
    BLACKSMITH(CharacterType.SHADY);

    @Getter
    private final CharacterType character; // character-type

    /**
     * Constructs a {@code CharacterConfig} 
     * @param character a character-type
     */
    CharacterConfig(CharacterType character) {
        this.character = character;
    }
}
