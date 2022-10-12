package org.lukos.model.location.states;

import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.location.House;
import org.lukos.model.location.HouseState;

import java.sql.SQLException;

/**
 * This is the {@code Soaked} state for a {@code House}, this means that it can be burn down from now on, which means it
 * will go to the {@code Burned} state. It also can be cleaned again in which it goes back to the {@code Repaired}
 * state.
 * <p>
 * This class is a singleton.
 *
 * @author Rick van der Heijden (1461923)
 * @since 22-02-2022
 */
public class Soaked implements HouseState {

    /** Private constructor to ensure Singleton design. */
    private Soaked() {
    }

    /**
     * Get the instance of {@code Soaked}, as there exist only 1.
     *
     * @return The instance of {@code Soaked}
     */
    public static Soaked getInstance() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * This method will not change anything about the state of the {@code House}, as its already {@code Soaked}.
     *
     * @param house The {@code House} mentioned in the description.
     * @throws SQLException when a database operation fails
     */
    @Override
    public void soak(House house) throws SQLException {
        house.setState(this);
    }

    /**
     * This method will burn down the {@code House}.
     *
     * @param house The {@code House} mentioned in the description.
     * @throws SQLException when a database operation fails
     */
    @Override
    public void burn(House house) throws SQLException {
        house.setState(Burned.getInstance());
    }

    /**
     * Building is not possible in the {@code Soaked} state, as the {@code House} is not burned down.
     *
     * @param house The {@code House} mentioned in the description.
     */
    @Override
    public void build(House house) throws WrongStateMethodException {
        throw new WrongStateMethodException("A house cannot be building while it is soaked.");
    }

    /**
     * This method cleans the {@code House}.
     *
     * @param house The {@code House} mentioned in the description.
     * @throws SQLException when a database operation fails
     */
    @Override
    public void repaired(House house) throws SQLException, WrongStateMethodException {
        house.setState(Cleaned.getInstance());
        house.repaired();
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final Soaked uniqueInstance = new Soaked();
    }
}
