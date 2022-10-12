package org.lukos.model.rolesystem.roles;

/**
 * Enumeration with the categories for role assignment, including the number of roles per category.
 *
 * @author Martijn van Andel (1251104)
 * @since 12-03-2022
 */
public enum MainRoleCategories {
    WEREWOLF(1),
    WEREWOLF_FRAMER(1),
    WEREWOLF_CLEANER(1),
    WEREWOLF_ELDER(1),
    FRIETSAUS(2),
    KETCHUP(3),
    BBQSAUS(2),
    PICALILLYSAUS(1),
    SCEPTIC(1),
    CHILISAUS_TOT(2),
    CHILISAUS_A(1),
    CHILISAUS_B(1);

    public final int groupSize;             // Number of roles in this category

    /**
     * Constructor. Assigns group size to {@code groupSize}
     */
    MainRoleCategories(int groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * Sums up the sizes of preceding categories (see usages)
     *
     * @param category category to calculate the offset for
     * @return sum of preceding category sizes
     */
    public static int getCategoryOffset(int category) {
        int sum = 0;
        int remainingIterations = category;
        for (MainRoleCategories c : MainRoleCategories.values()) {
            if (remainingIterations == 0) {
                break;
            }
            sum += c.groupSize;
            remainingIterations--;
        }

        if (category == 10 || category == 11) {
            sum -= MainRoleCategories.CHILISAUS_TOT.groupSize;
        }
        return sum;
    }
}
