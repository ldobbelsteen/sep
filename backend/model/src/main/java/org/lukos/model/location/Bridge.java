package org.lukos.model.location;

import org.lukos.database.LocationDB;
import org.lukos.model.exceptions.location.BridgeDoesNotExistException;

import java.sql.SQLException;

/**
 * A {@code Bridge} is a special {@code Location}, no one owns this location. A {@code Bridge} also has a name.
 *
 * @author Rick van der Heijden (1461923)
 * @since 22-02-2022
 */
public class Bridge extends Location {

    /** The ID of the {@code Bridge} */
    private final int iid;

    /**
     * Creates a {@code Bridge} object for an existing {@code Bridge} with ID {@code id}.
     *
     * @param id the ID of the {@code Bridge}
     * @throws SQLException when a database operation fails
     * @throws BridgeDoesNotExistException if there exist no bridge with ID {@code id}
     */
    public Bridge(int id) throws SQLException, BridgeDoesNotExistException {
        super(id);
        if (!LocationDB.bridgeExists(id)) {
            throw new BridgeDoesNotExistException("Tried to create a bridge object for a bridge that does not exist, " +
                    "use the creation constructor!");
        }
        this.iid = LocationDB.getInstanceIDBridgeByID(id);
    }

    /**
     * Creates a {@code Bridge} object and loads in into the database with name {@code name} and linked to the instance
     * with ID {@code iid}.
     *
     * @param iid  the ID of the instance the {@code Bridge} is linked to
     * @param name the name of the {@code Bridge}
     * @throws SQLException when a database operation fails
     */
    public Bridge(int iid, String name) throws SQLException {
        // generate new Location with ID
        super(iid, true);
        this.iid = iid;

        // retrieve the new location's ID
        int locationID = this.getId();

        // store the bridge in the database
        LocationDB.loadOrCreateBridge(this.iid, locationID, name);
    }

    /**
     * Returns the name of the {@code Bridge}.
     *
     * @return the name of the {@code Bridge}
     * @throws SQLException when a database operation fails
     */
    public String getName() throws SQLException {
        return LocationDB.getNameBridgeByID(this.getId());
    }
}
