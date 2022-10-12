package org.lukos.model.location.states;

import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.location.House;
import org.lukos.model.location.HouseState;

import java.sql.SQLException;

/**
 * This is the {@code Burned} state for a {@code House}, this means that it has been burned down. Now it will start
 * rebuilding again and when it has been rebuild it will go back to the state {@code Repaired}.
 * <p>
 * This class is a singleton.
 *
 * @author Rick van der Heijden (1461923)
 * @since 22-02-2022
 */
public class Burned implements HouseState {

    /** Private constructor to ensure Singleton design. */
    private Burned() {
    }

    /**
     * Get the instance of {@code Burned}, as there exist only 1.
     *
     * @return The instance of {@code Burned}
     */
    public static Burned getInstance() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * This method will not change anything about the state of the {@code House}, as the {@code House} is already burned
     * down and cannot be soaked until its repaired.
     *
     * @param house The {@code House} mentioned in the description.
     */
    @Override
    public void soak(House house) throws WrongStateMethodException {
        throw new WrongStateMethodException("The house cannot be soaked when it is already burned down.");
    }

    /**
     * This method will not change anything about the state of the {@code House}, as the {@code House} is already in the
     * state {@code Burned}.
     *
     * @param house The {@code House} mentioned in the description.
     */
    @Override
    public void burn(House house) throws WrongStateMethodException {
        throw new WrongStateMethodException("The house cannot be burned when it is already burned down.");
    }

    /**
     * This method will build up the {@code House} again, this will take 2 days.
     *
     * @param house The {@code House} mentioned in the description.
     * @throws SQLException when a database operation fails
     */
    @Override
    public void build(House house) throws SQLException, NoSuchPlayerException {
        house.setStateDay(house.getStateDay() + 1);
        if (house.getStateDay() == 3) {
            repaired(house);
        }
    }

    /**
     * This method will be called when the building phase is over, the {@code House} will then go back to the state
     * {@code Repaired}.
     *
     * @param house The {@code House} mentioned in the description.
     * @throws SQLException when a database operation fails
     */
    @Override
    public void repaired(House house) throws SQLException {
        house.setState(Repaired.getInstance());
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final Burned uniqueInstance = new Burned();
    }
}
