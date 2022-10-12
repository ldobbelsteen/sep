package org.lukos.controller.util;

import org.lukos.controller.response.SingleRoleEntry;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerHelper {

    /**
     * Checks whether the {@code Player} is already part of the {@code Instance} with ID {@code gid}.
     *
     * @param gid    The {@code Instance} id
     * @param player The {@code Player}
     * @return Whether the user is in the game or not
     */
    public static boolean playerInGame(int gid, Player player) {
        return player != null && player.getPlayerIdentifier().instanceID() == gid;
    }

    // TODO: Check permissions of the user asking for the player's roles

    /**
     * Returns a list with the players of all roles as strings.
     *
     * @param player The {@code Player} of which the roles are
     * @return The list with roles
     */
    public static List<SingleRoleEntry> listPlayerRoles(Player player)
            throws SQLException, ReflectiveOperationException, GameException {
        // Initialize list of roles for containing response
        List<SingleRoleEntry> roles = new ArrayList<>();

        MainRole mainRole = player.getMainRole();
        // store the Class name of the role in the list
        roles.add(new SingleRoleEntry(mainRole.getClass().getSimpleName(), mainRole.getGroup()));

        List<DoubleRole> doubleRoles = player.getDoubleRoles();
        for (DoubleRole role : doubleRoles) {
            roles.add(new SingleRoleEntry(role.getClass().getSimpleName(), role.getGroup()));
        }

        List<Job> jobs = player.getJobs();
        for (Job job : jobs) {
            roles.add(new SingleRoleEntry(job.getClass().getSimpleName(), null));
        }

        return roles;
    }
}
