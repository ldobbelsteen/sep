package org.lukos.model.location;

import org.lukos.database.HouseDB;
import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;

/**
 * A {@code House} is a special {@code Location}, a {@code Player} owns this location. Furthermore, a {@code House} has
 * a state which is either {@code Soaked}, {@code Burned} or {@code Repaired}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 22-02-2022
 */
public class House extends Location {

    /**
     * Constructs a {@code House} with the starting {@link HouseState} given by {@code state} and linked to a {@code
     * Player} with identifier {@code playerIdentifier}.
     *
     * @param playerIdentifier the {@link PlayerIdentifier} of the {@code Player} to which this {@code House} is linked
     * @param state            the {@code HouseState} of the {@code House}
     * @throws SQLException when a database operation fails
     */
    public House(PlayerIdentifier playerIdentifier, HouseState state) throws SQLException {
        // Initialize a new location. This also generates the ID for this House.
        super(playerIdentifier.instanceID(), true);

        int id = this.getId();

        HouseDB.existOrCreateHouseByID(playerIdentifier, id, state, 0);
    }

    /**
     * Constructs a {@code House} object with a given {@code Location} ID.
     *
     * @param id the ID of the {@code House}
     * @throws SQLException when a database operation fails
     */
    public House(int id) throws SQLException {
        super(id);
    }

    /**
     * Returns the state of the {@code House}.
     *
     * @return the state of the {@code House}
     * @throws SQLException when a database operation fails
     */
    public HouseState getState() throws SQLException {
        return HouseDB.getHouseState(this.getId());
    }

    /**
     * Sets the state of the house.
     *
     * @param state The new state.
     * @throws SQLException when a database operation fails
     */
    public void setState(HouseState state) throws SQLException {
        HouseDB.modifyHouseState(this.getId(), state);
        setStateDay(0);
    }

    /**
     * Returns the day the {@code House} has been in the current {@link HouseState}.
     *
     * @return the day the {@code House} has been in the current {@code HouseState}
     * @throws SQLException          when a database operation fails
     * @throws NoSuchPlayerException when this operation is called on a house that does not have a player
     */
    public int getStateDay() throws SQLException, NoSuchPlayerException {
        return HouseDB.getHouseStateDay(this.getId());
    }


    /**
     * Sets the day the {@code House} has been in the current {@link HouseState}.
     *
     * @param stateDay the day the {@code House} has been in the current {@code HouseState}
     * @throws SQLException when a database operation fails
     */
    public void setStateDay(int stateDay) throws SQLException {
        HouseDB.modifyHouseStateDay(this.getId(), stateDay);
    }

    /**
     * Method to soak a {@code House}.
     *
     * @throws WrongStateMethodException when the method gets called, but it is in the wrong state and cannot perform
     *                                   this operation
     * @throws SQLException              when a database operation fails
     */
    public void soak() throws WrongStateMethodException, SQLException {
        this.getState().soak(this);
    }

    /**
     * Method to burn a {@code House}.
     *
     * @throws WrongStateMethodException when the method gets called, but it is in the wrong state and cannot perform
     *                                   this operation
     * @throws SQLException              when a database operation fails
     */
    public void burn() throws WrongStateMethodException, SQLException {
        this.getState().burn(this);
    }

    /**
     * Method to build a {@code House} up.
     *
     * @throws WrongStateMethodException when the method gets called, but it is in the wrong state and cannot perform
     *                                   this operation
     * @throws NoSuchPlayerException     when this operation is called on a house that does not have a player
     * @throws SQLException              when a database operation fails
     */
    public void build() throws WrongStateMethodException, SQLException, NoSuchPlayerException {
        this.getState().build(this);
    }

    /**
     * Method to set a {@code House} to repaired.
     *
     * @throws WrongStateMethodException when the method gets called, but it is in the wrong state and cannot perform
     *                                   this operation
     * @throws SQLException              when a database operation fails
     */
    public void repaired() throws WrongStateMethodException, SQLException {
        this.getState().repaired(this);
    }
}
