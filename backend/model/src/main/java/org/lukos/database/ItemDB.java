package org.lukos.database;

import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for handling database-operations related to items
 *
 * @author Rick van der Heijden (1461923)
 * @since 05-04-2022
 */
public class ItemDB {


    /**
     * Method used to add an item to a user in an instance (player)
     *
     * @param player The given player
     * @param item   Item to add to player
     * @return the key of the item
     * @throws SQLException Exception thrown when writing to database fails.
     */
    public static int addPlayerItem(PlayerIdentifier player, String item) throws SQLException {
        PreparedStatement statement;
        statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO PlayerItems(userID, instanceID, item) values (?, ?, ?);",
                        Statement.RETURN_GENERATED_KEYS);
        statement.setObject(1, player.userID());
        statement.setObject(2, player.instanceID());
        statement.setString(3, item);

        DatabaseConnection.getInstance().writeStatement(statement);
        ResultSet resultSet = statement.getGeneratedKeys();

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        throw new SQLException("No Item ID was generated. That should not occur...");
    }

    /**
     * Method to delete a certain item from a player.
     *
     * @param player The given player
     * @param item   Item to delete from player
     * @throws SQLException Exception thrown when writing to database fails.
     */
    public static void deletePlayerItem(PlayerIdentifier player, String item) throws SQLException {
        PreparedStatement statement;
        statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM PlayerItems WHERE userID=? AND instanceID=? AND item=? LIMIT 1;");
        statement.setObject(1, player.userID());
        statement.setObject(2, player.instanceID());
        statement.setString(3, item);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Method returning the amount of a certain item that a given player has available.
     *
     * @param player the given player
     * @param item   Item to check if player has available
     * @return The number of items the player has available of a specified item
     * @throws SQLException          Exception thrown when reading from database fails.
     * @throws NoSuchPlayerException Exception thrown when the player is not found in the database
     */
    public static int amountOfItems(PlayerIdentifier player, String item) throws SQLException, NoSuchPlayerException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM PlayerItems WHERE userID=? AND instanceID=? AND item=?;");

        statement.setInt(1, player.userID());
        statement.setInt(2, player.instanceID());
        statement.setString(3, item);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        return count;
    }

}
