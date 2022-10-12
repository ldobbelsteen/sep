package org.lukos.model.instances.util;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.instances.NotEnoughRolesException;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.roles.MainRoleCategories;
import org.lukos.model.rolesystem.roles.MainRoleList;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * This is a helper class for assigning roles.
 *
 * @author Martijn van Andel (1251104)
 * @author Rick van der Heijden (1461923)
 * @since 09-04-2022
 */
abstract class RoleDivisionHelper extends RoleDivisionReader {

    /**
     * After starting a game, this method assigns roles based on a config table.
     * <p>
     * Based on the number of players, this function will read the corresponding role division as declared in the
     * configuration file. Then, for each group, it will randomly select the specified amount of roles from that group,
     * and add it to the list of 'roles to be distributed'. Then, each player is assigned one random role from this
     * list.
     *
     * @param SEED    the seed of the game
     * @param players a list of {@code Player}s of the game
     * @throws GameException                when an exception occurs in the game-logic
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    public static void assignRoles(long SEED, ArrayList<Player> players)
            throws GameException, SQLException, ReflectiveOperationException {
        // TODO: cleanup
        Random random = new Random(SEED);
        int NROF_PLAYERS = players.size();

        ArrayList<MainRole> roles = new ArrayList<>();          // List of roles that will be distributed

        /* Find the correct role division based on the NROF_PLAYERS */
        int[] roleDivision = readDivision(NROF_PLAYERS);

        boolean subgroupsChilisaus = false;
        int jesters = 0;
        for (int column = 0; column < roleDivision.length; column++) {
            int playersInCategory = roleDivision[column];
            switch (column) {
                case 9 -> subgroupsChilisaus = playersInCategory == 0;
                case 10 -> {
                }
                case 11 -> {
                    if (!subgroupsChilisaus) {
                        continue;
                    }
                }
                case 12 -> {
                    continue;
                }
                default -> {
                    if (playersInCategory == 0) {
                        continue;
                    }
                }
            }
            /* Determine the number of unique roles in the current category. */
            int ROLES_IN_CATEGORY = MainRoleCategories.values()[column].groupSize;

            /* Create a list of pointers for each unique role in this category and shuffle them. */
            ArrayList<Integer> pointers =
                    new ArrayList<>(IntStream.range(0, MainRoleCategories.values()[column].groupSize).boxed().toList());
            Collections.shuffle(pointers, random);

            /*
             * Add roles to the role list.
             * If all unique roles have been distributed and there are still players without a role,
             * reshuffle the pointers, and continue adding roles until ROLES_IN_CATEGORY == playersInCategory.
             */
            int remainingUniqueRoles = ROLES_IN_CATEGORY;
            while (playersInCategory > 0) {
                if (remainingUniqueRoles == 0) {
                    Collections.shuffle(pointers, random);
                    remainingUniqueRoles = ROLES_IN_CATEGORY;
                }
                roles.add(rolesLUT(column, pointers.get(remainingUniqueRoles - 1)));
                remainingUniqueRoles--;
                playersInCategory--;
            }
        }

        /* Check if there are as many roles as players. If this is the case, the config file is faulty. */
        if (roles.size() != players.size()) {
            throw new NotEnoughRolesException("Not enough roles to be distributed. Perhaps check the config file.");
        }

        /* Create ArrayList of roles to be distributed and randomly assign players to it. */
        for (Player p : players) {
            int REMAINING_ROLES = roles.size();
            MainRole mainRole;
            int randomIndex;
            if (REMAINING_ROLES != 1) {
                randomIndex = random.nextInt(REMAINING_ROLES);
            } else {
                randomIndex = 0;
            }
            mainRole = roles.remove(randomIndex);

            p.setMainRole(mainRole);
            /* Initialize action items. */
            p.getMainRole().initializeActions(p.getPlayerIdentifier());
        }
    }

    /**
     * Given a role category, this method returns the n'th role in that category, where n = {@code selectedRole}.
     *
     * @param category     the category we are picking a role from. Examples are 'Frietsaus', 'BBQsaus' etc.
     * @param selectedRole the specific role we are looking for.
     * @return the requested role.
     */
    public static MainRole rolesLUT(int category, int selectedRole) {
        return MainRoleList.getRole(category, selectedRole);
    }
}
