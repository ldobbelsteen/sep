package org.lukos.model.actionsystem.actions;

import org.lukos.database.util.LocationHelper;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.location.Location;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.lukos.model.actionsystem.actions.util.GeneralActionHelper.*;

/**
 * Kill players {@link Action}. This action will mark to players in the player list and the players at the locations in
 * the location lists for death. This does not kill them yet! TODO continue comment once killing is implemented.
 *
 * @author Valentijn van den Berg (1457446)
 * @author Martijn van Andel (1251104)
 * @since 23-03-2022
 */
public class KillPlayers extends Action {

    /**
     * Constructor
     */
    public KillPlayers() {
        super("KillPlayers");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId)
            throws SQLException, GameException, ReflectiveOperationException {
        // Get protected players
        List<PlayerIdentifier> protectedPlayers = getProtectedPlayersByInstanceID(data.playerIdentifier().instanceID());

        // Get all players from the player target list
        List<PlayerIdentifier> playerList = new ArrayList<>(data.data().players());

        // Add all players at the location targets to the list
        List<PlayerIdentifier> playersAtLocation = new ArrayList<>();
        for (int locationID : data.data().locations()) {
            Location location = LocationHelper.getLocationByID(locationID);
            playersAtLocation.addAll(location.getPlayersAtLocation());
            // Add all players as Player object to the playerList
            playerList.addAll(playersAtLocation);
            playersAtLocation.clear();
        }

        // If the alpha wolf performs this action, all Werewolves should not be marked for death.
        boolean isAlphaWolfKill = isAlphaWolf(data.playerIdentifier());

        // Mark all players on the list for death
        for (PlayerIdentifier player : playerList) {
            if (isAlphaWolfKill && isWolf(player)) {
                // Omit the werewolf, since the alpha wolf is killing.
            } else if (protectedPlayers.stream().noneMatch(p -> p.userID() == player.userID())) {
                // Player is not protected.
                // Mark the player for death
                modifyExecutedByInstanceID(data.playerIdentifier().instanceID(), player, true);
            }
        }
        // TODO send messages
    }
}
