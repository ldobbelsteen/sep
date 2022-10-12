package org.lukos.model.rolesystem.roles;

import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.roles.mainroles.*;

/** 
 * Lists every role in order of assignment
 *
 * @author Martijn van Andel (1251104)
 * @since 10-03-2022
 */
public enum MainRoleList {
    /* WEREWOLVES */
    WEREWOLF(new Werewolf()), 
    WEREWOLF_FRAMER(new WerewolfFramer()),
    WEREWOLF_CLEANER(new WerewolfCleaner()), 
    WEREWOLF_ELDER(new WerewolfElder()),
    /* FRIETSAUS */
    PRIVATE_INVESTIGATOR(new PrivateInvestigator()),
//    SCRUTINIZER(new Scrutinizer()),
    CLAIRVOYANT(new Clairvoyant()),
    /* KETCHUP */
    GUARDIAN_ANGEL(new GuardianAngel()),
    HEALER(new Healer()),
//    MATCHMAKER(new Matchmaker()),
    MEDIUM(new Medium()),
//    STALKER(new Stalker()),
//    SCOUT(new Scout()),
    /* BBQSAUS */
//    EXECUTIONER(new Executioner()),
    ARCHER(new Archer()),
    POISONER(new Poisoner()),
//    JUDGE(new Judge()),
    /* PICALILLYSAUS */
    MUTER(new Muter()),
//    SOMMELIER(new Sommelier()),
    SCEPTIC(new Sceptic()),
    /* CHILISAUS A */
    GRAVEROBBER(new Graverobber()),
//    HITMAN(new Hitman()),
    /* CHILISAUS B */
//    ARSONIST(new Arsonist()),
    CULT_LEADER(new CultLeader());

    public final MainRole role;         // Role corresponding to this enum

    /** Constructor. Assigns role to {@code role} variable */
    MainRoleList(MainRole role) {
        this.role = role;
    }

    /**
     * Returns a role from the given category.
     * @param category  the i'th category of roles, where i = {@code category}
     * @param selectedRole  a value between 0 and 1.
     *                      Acts as a pointer to select a role between the first (0) and last (1) in the category.
     * @return  MainRole in the given category.
     */
    public static MainRole getRole(int category, int selectedRole) {
        return MainRoleList.values()[(selectedRole + MainRoleCategories.getCategoryOffset(category))].role;
    }
}

