package org.lukos.database;

import java.sql.*;

/**
 * Singleton-class used for connecting to the database, and executing database-operations
 *
 * @author Lucas Gether-Rønning
 * @since 11-03-22
 */
public class DatabaseConnection {

    // We use MYSQL_HOSTNAME instead of MYSQL_HOST to prevent issues in Docker with setting the MYSQL_HOST variables
    // also see https://hub.docker.com/_/mysql
    private static final String CONNECTION_STRING =
            "jdbc:mysql://" + System.getenv("MYSQL_HOSTNAME") + ":" + System.getenv("MYSQL_PORT") + "/" +
                    System.getenv("MYSQL_DATABASE");
    private static final String MYSQL_USER = System.getenv("MYSQL_USER");
    private static final String MYSQL_PASSWORD = System.getenv("MYSQL_PASSWORD");

    private Connection connect; // connection to the database, initialized in constructor

    // Private constructor to ensure Singleton design.
    private DatabaseConnection() {
        try {
            connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the instance of {@code DatabaseConnection}, as there exist only 1.
     *
     * @return The instance of {@code DatabaseConnection}
     */
    public static DatabaseConnection getInstance() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * Getter for connection to db.
     *
     * @return connection to db
     */
    public Connection getConnect() throws SQLException {
        if (this.connect.isClosed()) {
            try {
                connect();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new SQLException("Could not establish connection to the database.");
            }
        }
        return this.connect;
    }

    /** Method used to set up database-connection */
    public void connect() throws InterruptedException {
        try {
            connect = DriverManager.getConnection(CONNECTION_STRING, MYSQL_USER,
                    MYSQL_PASSWORD);  // setting up connection
        } catch (Exception e) {
            e.printStackTrace();
            Thread.sleep(1);
            connect();
        } finally {
            if (this.connect == null) {
                Thread.sleep(1);
                connect();
            }
        }
    }

    /**
     * TODO: update JavaDoc
     * Shared execution method to execute a SQL-query reading from the database (INSERT, UPDATE, DELETE)
     *
     * @param query PreparedStatement containing prefilled SQL-query
     * @return A ResultSet returned from the read statement
     */
    public ResultSet readStatement(PreparedStatement query) throws SQLException {
        ResultSet resultSet = query.executeQuery();

        return resultSet;
    }
    /**
     * Shared execution method to execute a SQL-query writing to the database (INSERT, UPDATE, DELETE)
     *
     * @param query PreparedStatement containing prefilled SQL-query
     */
    public void writeStatement(PreparedStatement query) {
        try {
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final DatabaseConnection uniqueInstance = new DatabaseConnection();
    }
}
