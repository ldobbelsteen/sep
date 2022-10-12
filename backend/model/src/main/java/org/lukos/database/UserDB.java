package org.lukos.database;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.user.IssuerSub;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class for handling database-operations related to users.
 *
 * @author Lucas Gether-Rønning
 * @since 12-03-22
 */
public class UserDB {

    /**
     * Adds a new user to the `users`-table of the database with the values given as parameters.
     *
     * @param issuer   the one calling this method
     * @param sub      sub of the user
     * @param username username of the user
     * @return the id of the user that was just created
     * @throws SQLException        Exception thrown when inserting into database fails
     * @throws NoSuchUserException Exception thrown when there is no such user in the database
     */
    public static int createUser(String issuer, String sub, String username) throws SQLException, NoSuchUserException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "INSERT INTO Users(sub, issuer, username, wins, losses, last_login, last_logout, playtime, toBeDeleted) values (?," +
                        " ?, ?, ?, ?, ?, ?, ?, ?);");
        statement.setString(1, sub);
        statement.setString(2, issuer);
        statement.setString(3, username);
        statement.setInt(4, 0);
        statement.setInt(5, 0);
        statement.setDate(6, new java.sql.Date(new java.util.Date().getTime()));
        statement.setDate(7, new java.sql.Date(new java.util.Date().getTime()));
        statement.setInt(8, 0);
        statement.setBoolean(9, false);

        DatabaseConnection.getInstance().writeStatement(statement);
        // Retrieve user with this issuer & sub
        return getIfExistUser(new IssuerSub(issuer, sub));
    }

    /**
     * Getting the ID of a user from their issuerSub if they exist.
     *
     * @param issuerSub IssuerSub of the player
     * @return the ID of the user
     * @throws SQLException        Exception thrown when reading from the database fails
     * @throws NoSuchUserException Exception thrown when there is no such user in the database
     */
    public static int getIfExistUser(IssuerSub issuerSub) throws SQLException, NoSuchUserException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT * FROM Users WHERE issuer=? and sub=?;");
        statement.setString(1, issuerSub.issuer());
        statement.setString(2, issuerSub.sub());

        ResultSet rs = DatabaseConnection.getInstance().readStatement(statement);
        if (rs.next()) {
            return rs.getInt("userID");
        }

        // throws an Exception if the user does not exist.
        throw new NoSuchUserException("User does not exist.");
    }

    /**
     * Find out if a user exists in the database.
     *
     * @param uid ID of the user
     * @return Whether the user exists or not
     * @throws SQLException Exception thrown when reading from the database fails
     */
    public static boolean existUser(int uid) throws SQLException {
        PreparedStatement statement =
                DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM Users WHERE userID=?;");
        statement.setInt(1, uid);

        ResultSet rs = DatabaseConnection.getInstance().readStatement(statement);
        return rs.next();
    }

    /**
     * Deletes a user from the `users`-table of the database with the given {@code uid}.
     *
     * @param uid ID of the {@code User} provided
     * @throws SQLException Exception thrown when deleting from database fails
     */
    public static void deleteUserByUID(int uid) throws SQLException {
        PreparedStatement statement =
                DatabaseConnection.getInstance().getConnect().prepareStatement("DELETE FROM Users WHERE userID=?;");
        statement.setInt(1, uid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Method used to find a {@code User} in the database by its userID.
     *
     * @param uid UserID to find records for in the database
     * @return ResultSet generated from the entry in the database with the given userID
     * @throws SQLException Exception thrown when reading expected query from database fails
     */
    public static ResultSet findUserByID(int uid) throws SQLException {
        PreparedStatement statement =
                DatabaseConnection.getInstance().getConnect().prepareStatement("SELECT * FROM Users WHERE userID=?;");
        statement.setInt(1, uid);

        ResultSet resultSet = DatabaseConnection.getInstance().readStatement(statement);
        if (!resultSet.next()) {
            throw new SQLException("The ID is invalid!");
        }
        return resultSet;
    }

    /**
     * Method to add an entry to the UserRoles-table.
     *
     * @param uid         UserID of user to add role-statistics for
     * @param gamesPlayed Games user has played with given role
     * @param wins        Wins user has had with the given role
     * @param mainRole    MainRole to add to the table for this user
     * @throws SQLException Exception thrown when writing expected query fails
     */
    public static void addUserRole(int uid, int gamesPlayed, int wins, String mainRole) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement(
                        "INSERT INTO UserStats(userID, gamesPlayed, wins, purpose) VALUES (?, ?, ?, ?);"
                );
        statement.setInt(1, uid);
        statement.setInt(2, gamesPlayed);
        statement.setInt(3, wins);
        statement.setString(4, mainRole);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Setting the username of a user.
     *
     * @param uid      ID of the user
     * @param username Username to give the player
     * @throws SQLException Exception thrown if writing to the database fails
     */
    public static void setUsernameByID(int uid, String username) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("UPDATE Users SET username=? WHERE userID=?;");
        statement.setString(1, username);
        statement.setInt(2, uid);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * GDPR-method to extract all data associated with a userID from the database
     *
     * @param uid UserID of the user requesting data
     * @return a file (csv) containing the data requested by the user
     * @throws SQLException Exception thrown when reading from the database fails
     * @throws IOException  Exception thrown when i/o fails in filewriting
     */
    public static File getAllUserInfo(int uid) throws SQLException, IOException {
        //from https://idineshkrishnan.com/convert-resultset-to-csv-in-java/
        // creating the csv format
        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator("\n");

        // setting the file-name to user_{userID}_data_gdpr.csv
        final String FILE_NAME = "user_" + uid + "_data_gdpr.csv";

        // creating the file object
        File file = new File(FILE_NAME);

        // creating file writer object
        FileWriter fw = new FileWriter(file);

        // creating the csv printer object
        //https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVPrinter.html
        CSVPrinter printer = new CSVPrinter(fw, format);

        PreparedStatement userStatement;
        userStatement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT `Users`.*, `UserStats`.* FROM `Users` LEFT JOIN `UserStats` ON `Users`.`userID` = `UserStats`" +
                        ".`userID` WHERE Users.userID=?;");
        userStatement.setInt(1, uid);

        PreparedStatement playerStatement;
        playerStatement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT `Players`.*, `Roles`.*, `Ballots`.*, `PlayerItems`.* FROM `Players` LEFT JOIN `Ballots` ON " +
                        "`Players`.`userID` = `Ballots`.`userID` LEFT JOIN `PlayerItems` ON `Players`.`userID` = " +
                        "`PlayerItems`.`userID` LEFT JOIN `Roles` ON `Players`.`userID` = `Roles`.`userID` WHERE " +
                        "Players.userID=?;");
        playerStatement.setInt(1, uid);

        PreparedStatement actionStatement;
        actionStatement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT `Actions`.*, `ActionLogs`.* FROM `Actions` LEFT JOIN `ActionLogs` ON `Actions`.`actionID` = " +
                        "`ActionLogs`.`actionID` WHERE Actions.userID = ?;");
        actionStatement.setInt(1, uid);

        PreparedStatement chatStatement;
        chatStatement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "SELECT `ChatMembers`.*, `ChatMessages`.* FROM `ChatMembers` LEFT JOIN `ChatMessages` ON " +
                        "`ChatMembers`.`userID` = `ChatMessages`.`userID` WHERE ChatMembers.userID=?;");
        chatStatement.setInt(1, uid);

        // printing the result in 'CSV' file
        printer.printRecord("userID", "sub", "issuer", "username", "wins", "losses", "last_login", "last_logout", "playtime", "userID", "purpose", "gamesPlayed", "wins");
        printer.printRecords(DatabaseConnection.getInstance().readStatement(userStatement));
        printer.printRecord("instanceID","userID", "houseID", "houseState", "houseStateDay", "alive", "currentLocation", "coupleUID", "coupleIID", "toBeExecuted", "muted", "deathNote", "deathnoteIsChangeable", "isProtected", "userID", "instanceID", "purposeType", "purpose", "userID", "instanceID", "voteID", "targetID", "itemID", "userID", "instanceID", "item");
        printer.printRecords(DatabaseConnection.getInstance().readStatement(playerStatement));
        printer.printRecord("actionID", "instanceID", "userID", "time", "name", "status", "targetType", "messageID", "actionID", "receiverID", "status", "messageType");
        printer.printRecords(DatabaseConnection.getInstance().readStatement(actionStatement));
        printer.printRecord("userID", "chatID", "writeAccess", "messageID", "chatID", "timeSent", "userID", "message");
        printer.printRecords(DatabaseConnection.getInstance().readStatement(chatStatement));

        fw.close(); // closing the file-writer
        printer.close(); // closing the csv-printer
        return file; // returning the created file
    }

    // TODO: add javadoc
    public static void deleteUsersAfterInstanceEnd(List<Integer> users) throws SQLException {
        for (int userID: users) {
            deleteUsersAfterInstanceEnd(userID);
        }
    }

    /**
     * Deletes all users with 'true' in their 'toBeDeleted'-column in the database
     *
     * @throws SQLException exception thrown when writing to the database fails
     */
    private static void deleteUsersAfterInstanceEnd(int userID) throws SQLException {
        // this might cause issues if there are multiple active instances, as
        // all users that request deletion will be deleted once any instance is over.
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("DELETE FROM Users WHERE toBeDeleted='1' AND userID=?;");
        statement.setInt(1, userID);

        DatabaseConnection.getInstance().writeStatement(statement);
    }

    /**
     * Sets the column `toBeDeleted` to the specified value for a specified user in the database
     *
     * @throws SQLException exception thrown when writing to the database fails
     */
    public static void setUserDeletion(int uid, boolean toBeDeleted) throws SQLException {
        PreparedStatement statement1 = DatabaseConnection.getInstance().getConnect()
                .prepareStatement("SELECT instanceID FROM Players WHERE userID=?;");
        statement1.setInt(1, uid);
        ResultSet rs = DatabaseConnection.getInstance().readStatement(statement1);
        if (rs.next()) { // the user is currently in a game
            PreparedStatement statement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("UPDATE Users SET toBeDeleted=? WHERE userID=?;");
            statement.setBoolean(1, toBeDeleted);
            statement.setInt(2, uid);

            DatabaseConnection.getInstance().writeStatement(statement);
        } else if (toBeDeleted){ // the user is not in a game
            deleteUserByUID(uid); //
        }
    }

    /**
     * Updates the user statistics after a game has concluded.
     *
     * @param uid user ID of a player that just finished a game.
     * @param mainRole the MainRole that the {@code uid} had.
     * @param hasWon dictates whether this user has won or not.
     * @throws SQLException
     */
    public static void incrementGamesPlayedByUserID(int uid, String mainRole, boolean hasWon) throws SQLException {
        int win = hasWon ? 1 : 0;
        PreparedStatement statement = DatabaseConnection.getInstance().getConnect().prepareStatement(
                "UPDATE UserStats SET gamesPlayed=gamesPlayed+1, wins=wins+? WHERE userID=? AND purpose=?;"
        );
        statement.setInt(1, win);
        statement.setInt(2, uid);
        statement.setString(3, mainRole);

        DatabaseConnection.getInstance().writeStatement(statement);
    }
}
