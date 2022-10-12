package org.lukos.database.util;

import org.lukos.database.HouseDB;
import org.lukos.database.LocationDB;
import org.lukos.model.exceptions.location.BridgeDoesNotExistException;
import org.lukos.model.exceptions.location.LocationException;
import org.lukos.model.exceptions.location.NoSuchLocationException;
import org.lukos.model.location.Bridge;
import org.lukos.model.location.House;
import org.lukos.model.location.Location;

import java.sql.SQLException;

/**
 * @author Rick van der Heijden (1461923)
 * @since 06-04-2022
 */
public class LocationHelper {

    /** Private constructor, as it is a helper class, and it enforces static methods */
    private LocationHelper() {
    }

    /**
     * Returns a {@code Location} by its ID.
     *
     * @param locationID the ID of the {@code Location}
     * @return a {@code Location} by its ID
     * @throws SQLException      when a database failure occurs
     * @throws LocationException when there exist no {@code Location} with the given ID
     */
    public static Location getLocationByID(int locationID) throws SQLException, LocationException {
        if (HouseDB.existHouseByID(locationID)) {
            return new House(locationID);
        } else if (LocationDB.bridgeExists(locationID)) {
            return new Bridge(locationID);
        }
        throw new NoSuchLocationException("That Location does not exist!");
    }
}
