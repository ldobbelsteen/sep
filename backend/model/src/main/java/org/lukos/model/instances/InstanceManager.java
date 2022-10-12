package org.lukos.model.instances;

import org.lukos.database.InstanceDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a map of instances with their unique identifiers. This is a singleton.
 *
 * @author Martijn van Andel (1251104)
 * @since 21-02-2022
 */
public class InstanceManager {

    /** Default constructor. Initializes {@code instances} */
    private InstanceManager() {
    }

    /** Returns the singleton of uniqueInstance */
    public static InstanceManager getInstanceManager() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * Finds an instance with the given id
     *
     * @param iid ID of the instance to be found
     * @return org.lukos.model.instances.Instance with UUID iid
     * @throws NoSuchInstanceException if no instance with given id is found.
     */
    public IInstance getInstance(int iid) throws NoSuchInstanceException, SQLException {
        ResultSet result = InstanceDB.findInstanceByID(iid);

        if (!result.next()) {
            throw new NoSuchInstanceException("The instance with the given ID does not exist.");
        }

        return new Instance(iid, result.getInt("seed"));
    }

    /**
     * Returns all current instances
     *
     * @return List containing all instances
     */
    public List<IInstance> getInstances() throws SQLException, NoSuchInstanceException {
        List<Integer> instanceIDs = InstanceDB.generateInstanceIDList();
        List<IInstance> instances = new ArrayList<>();
        for (int iid : instanceIDs) {
            instances.add(getInstance(iid));
        }
        return instances;
    }

    /**
     * Creates a new instance, then adds it to {@code instances}
     * FIXME: fix JavaDoc description
     *
     * @param uid UUID of the caller
     * @return iid  UUID of newly created instance
     */
    public int createInstance(int uid, String name, int SEED) throws SQLException {
        return InstanceDB.addNewInstance(uid, name, SEED);
    }

    /**
     * Removes an existing instance from {@code instances}
     *
     * @param uid UUID of the caller
     * @param iid UUID of instance
     * @return iid  UUID of the removed instance
     * @throws NoSuchInstanceException if no instance is found.
     * @throws NoPermissionException   if the caller is not the game master.
     */
    public IInstance removeInstance(int uid, int iid) throws GameException, SQLException {
        IInstance instance = getInstance(iid);
        if (uid != instance.getGameMaster()) {
            throw new NoPermissionException("Caller is not the game master");
        }
        if (!InstanceDB.deleteInstanceByIID(iid)) {
            throw new NoSuchInstanceException("No instance found.");
        }
        return instance;
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final InstanceManager uniqueInstance = new InstanceManager();
    }
}
