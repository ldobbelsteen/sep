package org.lukos.database.util;

import org.lukos.model.user.PlayerIdentifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReadingHelper {

    public static List<PlayerIdentifier> readPlayerIdentifiers(ResultSet resultSet, int instanceID) throws
            SQLException {
        List<PlayerIdentifier> players = new ArrayList<>();

        while (resultSet.next()) {
            int userID = resultSet.getInt("userID"); // reading userID from resultSet
            PlayerIdentifier playerIdentifier = new PlayerIdentifier(instanceID, userID);
            players.add(playerIdentifier); // adding username to list
        }

        return players;
    }
}
