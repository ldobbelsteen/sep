package org.lukos.model.location.states;

import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.location.House;
import org.lukos.model.location.HouseState;

import java.sql.SQLException;

/**
 * This is the {@code Repaired} state for a {@code House}, this means that it can be soaked, which means it will go to
 * the {@code Soaked} state.
 * <p>
 * This class is a singleton.
 *
 * @author Rick van der Heijden (1461923)
 * @since 22-02-2022
 */
public class Repaired implements HouseState {

    /** Private constructor to ensure Singleton design. */
    private Repaired() {
    }

    /**
     * Get the instance of {@code Repaired}, as there exist only 1.
     *
     * @return The instance of {@code Repaired}
     */
    public static Repaired getInstance() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * This method will change the state of the {@code House} to {@code Soaked}.
     *
     * @param house The {@code House} mentioned in the description.
     * @throws SQLException when a database operation fails
     */
    @Override
    public void soak(House house) throws SQLException {
        house.setState(Soaked.getInstance());
    }

    /**
     * This method will not change anything about the state of the {@code House}, as the {@code House} is not soaked and
     * thus it cannot go to state {@code Burned}.
     *
     * @param house The {@code House} mentioned in the description.
     */
    @Override
    public void burn(House house) throws WrongStateMethodException {
        throw new WrongStateMethodException("House cannot be burned while it is not soaked.");
    }

    /**
     * This method will not change anything about the state of the {@code House}, as the {@code House} is already in the
     * state {@code Repaired}.
     *
     * @param house The {@code House} mentioned in the description.
     */
    @Override
    public void build(House house) throws WrongStateMethodException {
        throw new WrongStateMethodException("House cannot be build while it is already intact.");
    }

    /**
     * This method will not change anything about the state of the {@code House}, as the {@code House} is already in the
     * state {@code Repaired}.
     *
     * @param house The {@code House} mentioned in the description.
     * @throws SQLException when a database operation fails
     */
    @Override
    public void repaired(House house) throws SQLException {
        house.setState(this);
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final Repaired uniqueInstance = new Repaired();
    }
}
