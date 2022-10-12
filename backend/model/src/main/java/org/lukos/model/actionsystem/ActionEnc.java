package org.lukos.model.actionsystem;

import org.lukos.model.user.PlayerIdentifier;

import java.util.List;

/**
 * Record encapsulating action data
 * <p>
 * Holds the data necessary to perform an action
 *
 * @param locations A list of locations affected by the action
 * @param players   A list of players affected by the action
 * @author Lucas Gether-Rønning
 * @since 22-02-2022
 */
public record ActionEnc(List<Integer> locations, List<PlayerIdentifier> players) {

    @Override
    public boolean equals(Object o) {
        if (o instanceof ActionEnc otherObject) {
            return this.locations.equals(otherObject.locations) && this.players.equals(otherObject.players);
        }
        return false;
    }
}
