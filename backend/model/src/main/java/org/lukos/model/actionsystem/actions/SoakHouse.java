package org.lukos.model.actionsystem.actions;

import org.lukos.database.util.LocationHelper;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.location.LocationException;
import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.location.House;
import org.lukos.model.location.Location;

import java.sql.SQLException;
import java.time.Instant;

/**
 * Soaks multiple houses.
 * Expects the houses, that are to be soaked, to be in {@code data.location()}. It ignores {@code data.players()}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 26-03-22
 */
public class SoakHouse extends Action {

    public SoakHouse() {
        super("SoakHouse");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) throws SQLException, LocationException {
        // Go through all locations
        for (int locationID : data.data().locations()) {
            Location location = LocationHelper.getLocationByID(locationID);
            // If the location is a bridge, we ignore it
            if (location instanceof House) {
                // If it is a house, we soak it
                ((House) location).soak();
            }
        }
    }
}
