package org.lukos.model.location.states;

import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.location.House;
import org.lukos.model.location.HouseState;

import java.sql.SQLException;

/**
 * This is the {@code Cleaned} state for a {@code House}, this means that it can go back to its {@code Repaired} state
 * again.
 * <p>
 * This class is a singleton.
 *
 * @author Rick van der Heijden (1461923)
 * @since 07-04-2022
 */
public class Cleaned implements HouseState {

    /** Private constructor to ensure Singleton design. */
    private Cleaned() {
    }

    /**
     * Get the instance of {@code Cleaned}, as there exist only 1.
     *
     * @return The instance of {@code Cleaned}
     */
    public static Cleaned getInstance() {
        return Cleaned.SingletonHelper.uniqueInstance;
    }

    /**
     * This method will not change anything about the state of the {@code House}, as the {@code House} is just cleaned
     * and first needs to go into the {@code Repaired} state before it can be {@code Soaked} again.
     *
     * @param house The {@code House} mentioned in the description.
     */
    @Override
    public void soak(House house) throws WrongStateMethodException {
        throw new WrongStateMethodException("The house cannot be soaked when it is in the cleaned state, it must be " +
                "repaired before it can be soaked again.");
    }

    /**
     * This method will not change anything about the state of the {@code House}, as the {@code House} is not
     * {@code Soaked}.
     *
     * @param house The {@code House} mentioned in the description.
     */
    @Override
    public void burn(House house) throws WrongStateMethodException {
        throw new WrongStateMethodException("A cleaned house cannot burn down.");
    }

    /**
     * The method will not change anything about the state of the {@code House}, as the house is still standing and thus
     * there is nothing to build up again.
     *
     * @param house The {@code House} mentioned in the description.
     */
    @Override
    public void build(House house) throws SQLException, NoSuchPlayerException, WrongStateMethodException {
        throw new WrongStateMethodException("A cleaned house is not broke, hence it cannot be build.");
    }

    /**
     * This method will be called to put the {@code House} back in the {@code Repaired} state, as it has been cleaned
     * and is fully restored in its original state ({@code Repaired}).¶
     *
     * @param house The {@code House} mentioned in the description.
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
        private static final Cleaned uniqueInstance = new Cleaned();
    }
}
