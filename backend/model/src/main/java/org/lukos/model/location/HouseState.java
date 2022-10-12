package org.lukos.model.location;

import org.lukos.model.exceptions.location.WrongStateMethodException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;

import java.sql.SQLException;

/**
 * Interface base class for a state of a {@code House}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 22-02-2022
 */
public interface HouseState {
    /**
     * Method to soak a {@code House}.
     *
     * @param house the {@code House} to soak
     * @throws WrongStateMethodException when the method gets called, but it is in the wrong state and cannot perform
     *                                   this operation
     * @throws SQLException              when a database operation fails
     */
    void soak(House house) throws WrongStateMethodException, SQLException;

    /**
     * Method to burn a {@code House}.
     *
     * @param house the {@code House} to burn
     * @throws WrongStateMethodException when the method gets called, but it is in the wrong state and cannot perform
     *                                   this operation
     * @throws SQLException              when a database operation fails
     */
    void burn(House house) throws WrongStateMethodException, SQLException;

    /**
     * Method to build a {@code House} up.
     *
     * @param house the {@code House} to build
     * @throws WrongStateMethodException when the method gets called, but it is in the wrong state and cannot perform
     *                                   this operation
     * @throws NoSuchPlayerException     when this operation is called on a house that does not have a player
     * @throws SQLException              when a database operation fails
     */
    void build(House house) throws WrongStateMethodException, SQLException, NoSuchPlayerException;

    /**
     * Method to set a {@code House} to repaired.
     *
     * @param house the {@code House} to repair
     * @throws WrongStateMethodException when the method gets called, but it is in the wrong state and cannot perform
     *                                   this operation
     * @throws SQLException              when a database operation fails
     */
    void repaired(House house) throws WrongStateMethodException, SQLException;
}
