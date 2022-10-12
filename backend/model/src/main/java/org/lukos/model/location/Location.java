package org.lukos.model.location;

import lombok.Getter;
import org.lukos.database.LocationDB;
import org.lukos.database.PlayerDB;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * This abstract class holds a {@code Location}. {@code Player}s can either visit or leave a {@code Location}. Multiple
 * {@code Player}s can be at 1 {@code Location}, also no {@code Player} can be at an {@code Location}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 22-02-2022
 */
@Getter
public abstract class Location {

    /** Holding the location id. */
    private final int id;

    /**
     * Loads in an existing location with ID {@code locationID}.
     *
     * @param locationID the ID of the {@code Location}
     */
    public Location(int locationID) {
        this.id = locationID;
    }

    /**
     * Generates a new location with a newly generated (auto-incremented) ID linked to the {@code Instance} with ID
     * {@code instanceOrLocationID}, if {@code createNew} is true. Otherwise, we will load in the existing {@code
     * Location} with ID {@code instanceOrLocationID}.
     *
     * @param instanceOrLocationID the ID of either the {@code Instance} or the {@code Location}
     * @param createNew            whether to create a new {@code Location} or load in an existing one
     * @throws SQLException when a database operation fails
     */
    public Location(int instanceOrLocationID, boolean createNew) throws SQLException {
        if (createNew) {
            // the instanceOrLocationID is an instance ID;
            // a new location should be generated with the given instance ID
            this.id = LocationDB.createNewLocation(instanceOrLocationID);
        } else {
            // return a Location with the given instanceOrLocationID as its ID
            this.id = instanceOrLocationID;
        }
    }

    /**
     * Makes a {@code Player} visit this {@code Location}. The {@code player} will be added to the list of {@code
     * player}s at this {@code Location}.
     *
     * @param player The {@code Player} who visits.
     * @throws SQLException when a database operation fails
     */
    public void visitPlayer(Player player) throws SQLException {
        PlayerDB.visitLocation(this.id, player.getPlayerIdentifier());
    }

    /**
     * Returns a list of IDs of {@code Player}s that is currently at this {@code Location}.
     *
     * @return a list of IDs of {@code Player}s
     * @throws SQLException when a database operation fails
     */
    public List<PlayerIdentifier> getPlayersAtLocation() throws SQLException {
        return PlayerDB.getPlayersAtLocation(this.id);
    }
}
