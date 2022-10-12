package org.lukos.model.actionsystem.actions;

import org.lukos.database.util.LocationHelper;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.location.LocationException;
import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.location.House;

import java.sql.SQLException;
import java.time.Instant;

/**
 * Action used for arsonist to clean a house. This house is assumed to be {@code data.location().get(0)}.
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 */
public class CleanHouse extends Action {

    public CleanHouse() {
        super("DrenchClean");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) throws SQLException, LocationException {
        House house = (House) LocationHelper.getLocationByID(data.data().locations().get(0));
        try {
            house.repaired();
        } catch (WrongStateMethodException e) {
            e.printStackTrace();
        }
    }
}
