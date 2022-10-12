package org.lukos.model.config;

import org.lukos.model.rolesystem.Group;

import lombok.Getter;

/** Configuration of the {@code Group} of all roles */
public enum GroupConfig {
    WEREWOLF(Group.WEREWOLVES),
    WEREWOLF_ELDER(Group.WEREWOLVES),
    WEREWOLF_CLEANER(Group.WEREWOLVES),
    WEREWOLF_FRAMER(Group.WEREWOLVES),
    ARCHER(Group.TOWNSPEOPLE),
    ARSONIST(Group.ARSONIST),
    CLAIRVOYANT(Group.TOWNSPEOPLE),
    CULT_LEADER(Group.CULT),
    EXECUTIONER(Group.TOWNSPEOPLE),
    GRAVEROBBER(Group.NONWINNING),
    GUARDIAN_ANGEL(Group.TOWNSPEOPLE),
    HEALER(Group.TOWNSPEOPLE),
    HITMAN(Group.HITMAN),
    JUDGE(Group.TOWNSPEOPLE),
    MATCHMAKER(Group.TOWNSPEOPLE),
    MEDIUM(Group.TOWNSPEOPLE),
    MUTER(Group.TOWNSPEOPLE),
    POISONER(Group.TOWNSPEOPLE),
    PRIVATE_INVESTIGATOR(Group.TOWNSPEOPLE),
    SCEPTIC(Group.TOWNSPEOPLE),
    SCRUTINIZER(Group.TOWNSPEOPLE),
    SOMMELIER(Group.TOWNSPEOPLE),
    STALKER(Group.TOWNSPEOPLE),
    SCOUT(Group.TOWNSPEOPLE),
    TOWNSPERSON(Group.TOWNSPEOPLE),
    FOLLOWER(Group.CULT),
    JESTER(Group.JESTER),
    LOVER(Group.LOVERS),
    WOLF_CUB(Group.NONWINNING);
    
    @Getter
    private final Group group; // goal-group
    
    /**
     * Constructs a {@code GroupConfig}
     * @param group a goal-group
     */
    GroupConfig(Group group) {
        this.group = group;
    }
}
