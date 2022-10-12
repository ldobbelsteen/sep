package org.lukos.database;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.voting.VoteType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.lukos.database.util.ReadingHelper.readPlayerIdentifiers;

/**
 * Class for handling database-operations related to users.
 *
 * @author Lucas Gether-Rønning
 * @since 12-03-22
 */
public class VoteDB {

    /**
     * Adds a new vote to the `instancevotes`-table of the database with the values given as parameters.
     *
     * @param instanceID ID of the instance
     * @param voteType   {@code Instance} for which the vote will take place
     * @return the ID of the newly created vote
     * @throws SQLException Exception thrown when inserting into database fails
     */
    public static int addNewVote(int instanceID, VoteType voteType) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "INSERT INTO InstanceVotes(instanceID, voteType, started, ended) VALUES (?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, instanceID);
        statement.setString(2, voteType.toString());
        statement.setBoolean(3, false);
        statement.setBoolean(4, false);

        DatabaseConnection.getInstance().writeStatement(statement);

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new SQLException("Creating vote failed; no ID was obtained after creating a vote.");
        }
        return generatedKeys.getInt(1);
    }

    /**
     * Returns a {@link ResultSet} containing the {@code Vote}s in the {@code Instance} indicated by {@code
     * instanceID} that are ongoing.
     *
     * @param instanceID the ID for the {@code Instance} for which the ongoing {@code Vote}s should be retrieved
     * @return a {@link ResultSet} containing the ongoing {@code Vote}s in the {@code Instance} indicated by {@code
     * instanceID}
     * @throws SQLException when database-related issues occur
     */
    public static ResultSet retrieveOngoingVotesByInstance(int instanceID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM InstanceVotes WHERE instanceID=? AND started=1 AND ended!=1;");
        statement.setInt(1, instanceID);

        return DatabaseConnection.getInstance().readStatement(statement);
    }

    /**
     * Deletes a vote from the `instancevotes`-table of the database with the given voteID.
     *
     * @param vid VoteID provided to delete from database
     * @throws SQLException Exception thrown when deleting from database fails
     */
    public static void deleteVoteByID(int vid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM InstanceVotes WHERE voteID=?;");
        statement.setInt(1, vid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Method used to find a {@code Vote} in the database by its voteID.
     *
     * @param vid VoteID to find records for in the database
     * @return A ResultSet of the entry in the database with the given voteID
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static ResultSet findVoteByID(int vid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM InstanceVotes WHERE voteID=?;");
        statement.setInt(1, vid);

        return DatabaseConnection.getInstance().readStatement(statement);
    }

    /**
     * Returns a {@link ResultSet} containing a {@code Vote} with ID {@code voteId}, if that {@code Vote} exists and is
     * in the {@code Instance} indicated by {@code instanceId}.
     *
     * @param voteId     ID of the {@code Vote} that should be returned
     * @param instanceId ID of the {@code Instance} in which the {@code Vote} should be
     * @return {@link ResultSet} containing the {@code Vote} with ID {@code voteId}.
     * @throws SQLException when database-related o
     */
    public static ResultSet findVoteByIdIfInInstance(int voteId, int instanceId) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM InstanceVotes WHERE voteID=? AND instanceID=?;");
        statement.setInt(1, voteId);
        statement.setInt(2, instanceId);

        return DatabaseConnection.getInstance().readStatement(statement);
    }

    /**
     * Method to change the state of a vote to be ended or not ended.
     *
     * @param vid   Id of the given vote
     * @param state State to change the vote to
     * @throws SQLException when database-related operation fails
     */
    public static void modifyEnded(int vid, boolean state) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE InstanceVotes SET ended=? WHERE voteID=?;");
        statement.setBoolean(1, state);
        statement.setInt(2, vid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Method to modify whether a vote has started or not.
     *
     * @param vid   id of the vote
     * @param state State to change the vote to
     * @throws SQLException Exception thrown database query fails
     */
    public static void modifyStarted(int vid, boolean state) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE InstanceVotes SET started=? WHERE voteID=?;");
        statement.setBoolean(1, state);
        statement.setInt(2, vid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Getting the vote type of a vote by its id.
     *
     * @param vid id of the vote
     * @return the vote type of the vote
     * @throws SQLException Exception thrown database query fails
     */
    public static VoteType getVoteTypeByID(int vid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT voteType FROM InstanceVotes WHERE voteID=?;");
        statement.setInt(1, vid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (!resultSet.next()) {
            throw new SQLException("That ID is invalid!");
        }
        return VoteType.valueOf(resultSet.getString("voteType"));
    }

    /**
     * Getting whether a vote is still ongoing (busy)
     *
     * @param vid id of the vote
     * @return whether a vote is still busy
     * @throws SQLException Exception thrown query fails
     */
    public static boolean getBusyByID(int vid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT started, ended FROM InstanceVotes WHERE voteID=?;");
        statement.setInt(1, vid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (!resultSet.next()) {
            throw new SQLException("That ID is invalid!");
        }
        return resultSet.getBoolean("started") && !resultSet.getBoolean("ended");
    }

    /**
     * Getting whether a vote has started from its id.
     *
     * @param vid id of the vote
     * @return whether a vote has started
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static boolean getStartedByID(int vid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT started FROM InstanceVotes WHERE voteID=?;");
        statement.setInt(1, vid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (!resultSet.next()) {
            throw new SQLException("That ID is invalid!");
        }
        return resultSet.getBoolean("started");
    }

    /**
     * Getting if a vote has ended by its id.
     *
     * @param vid id of the vote
     * @return whether a vote has ended
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static boolean getEndedByID(int vid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT ended FROM InstanceVotes where voteID=?;");
        statement.setInt(1, vid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (!resultSet.next()) {
            throw new SQLException("That ID is invalid!");
        }
        return resultSet.getBoolean("ended");
    }

    /**
     * Method needed to add a ballot to a particular vote.
     *
     * @param userID     UserID of the voting user
     * @param instanceID InstanceID of the instance the vote takes place
     * @param voteID     ID of the particular vote
     * @param targetID   The target of the vote (player that has been voted for)
     * @throws SQLException Exception thrown when writing to the database fails
     */
    public static void addBallot(int userID, int instanceID, int voteID, int targetID) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("INSERT INTO Ballots(userID, instanceID, voteID, targetID) VALUES (?,?,?,?);");
        statement.setInt(1, userID);
        statement.setInt(2, instanceID);
        statement.setInt(3, voteID);
        statement.setInt(4, targetID);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Provided a voteID, get all ballots of this vote.
     *
     * @param voteID VoteID of vote
     * @return A ResultSet with all ballots of the vote
     * @throws SQLException Exception thrown when reading from database fails
     */
    public static ResultSet getAllBallotsOfVote(int voteID) throws SQLException {
        PreparedStatement statement =
                DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM Ballots WHERE voteID=?;");
        statement.setInt(1, voteID);

        return DatabaseConnection.getInstance().readStatement(statement);
    }

    /**
     * Given a list of players who are allowed to vote, this method generates a json-string of their userIDs and
     * instanceIDs, and inserts it into the database.
     *
     * @param vid            The ID of the {@code Vote} to save allowed players for
     * @param allowedPlayers List of players allowed to vote
     * @throws SQLException exception thrown when writing to the database fails
     */
    public static void saveAllowedPlayers(int vid, Set<PlayerIdentifier> allowedPlayers) throws SQLException {
        JSONArray allowedPlayersJson = new JSONArray();

        for (PlayerIdentifier pid : allowedPlayers) {
            JSONObject json = new JSONObject();
            json.put("userID", pid.userID());
            json.put("instanceID", pid.instanceID());
            allowedPlayersJson.put(json);
        }

        String allowed = allowedPlayersJson.toString();

        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE InstanceVotes SET allowed=? WHERE voteID=?;");
        statement.setString(1, allowed);
        statement.setInt(2, vid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Given a json string of the players that are eligible to vote, this method returns a list of all {@code
     * PlayerIdentifier} objects associated with the players that are eligible to vote.
     *
     * @param vid The voteID of the vote that we are checking eligibility
     * @return A list of all {@code PlayerIdentifier} objects associated with the players able to vote
     * @throws SQLException Exception thrown when reading from the database fails
     */
    public static List<PlayerIdentifier> getAllowedPlayers(int vid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT allowed FROM InstanceVotes WHERE voteID=?;");
        statement.setInt(1, vid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (!resultSet.next()) {
            throw new SQLException("That ID is invalid!");
        }

        String jsonStr = resultSet.getString("allowed");
        // this could need to change, not sure if this takes all objects in properly
        JSONArray json = new JSONArray(jsonStr);
        List<PlayerIdentifier> list = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            int uid = json.getJSONObject(i).getInt("userID");
            int iid = json.getJSONObject(i).getInt("instanceID");
            PlayerIdentifier player = new PlayerIdentifier(iid, uid);
            list.add(player);
        }

        return list;
    }

    public static void deleteTiedPlayers(int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM TiedPlayers WHERE instanceID=?;");

        statement.setInt(1, iid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Adds all players to TiedPlayers that received the same number of votes and the mayor has to decide between.
     *
     * @param tiedPlayers the tied players
     * @throws SQLException Exception thrown if writing to the database fails
     */
    public static void setTiedPlayers(List<PlayerIdentifier> tiedPlayers) throws SQLException {
        // Add a TiedPlayers entry for each tied player.
        for (PlayerIdentifier player : tiedPlayers) {
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                    "INSERT INTO TiedPlayers(instanceID, userID) VALUES (?, ?);"
            );

            statement.setInt(1, player.instanceID());
            statement.setInt(2, player.userID());

            DatabaseConnection.getInstance().writeStatement(statement);
        }
    }

    public static List<PlayerIdentifier> getTiedPlayers(int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT * FROM TiedPlayers where InstanceID=?"
        );

        statement.setInt(1, iid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        return readPlayerIdentifiers(resultSet, iid);
    }

    /**
     * Sets how many undecided lynches there are, i.e. how many players the mayor has to decide to lynch.
     *
     * @param iid the instance id
     * @param undecidedLynches the number of undecided lynches
     * @throws SQLException
     */
    public static void setUndecidedLynchesByInstanceID(int iid, int undecidedLynches) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement(
                        "UPDATE Instance SET undecidedLynches=? WHERE instanceID=? ;"
                );

        statement.setInt(1, undecidedLynches);
        statement.setInt(2, iid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    public static int getUndecidedLynches(int iid) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT undecidedLynches FROM Instance WHERE instanceID=?;");

        statement.setObject(1, iid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }
}
