package org.lukos.model;

import org.junit.jupiter.api.BeforeEach;
import org.lukos.database.InstanceDB;
import org.lukos.database.UserDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * General super testing class that cleans up the database before every test case.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-04-2022
 */
public abstract class GameTest {

    /**
     * Cleans up the database before every test case.
     */
    @BeforeEach
    public void cleanUpDatabase() {
        try {
            for (int gameId : InstanceDB.generateInstanceIDList()) {
                IInstance instance = InstanceManager.getInstanceManager().getInstance(gameId);
                for (Player player : instance.getPlayerList()) {
                    UserDB.deleteUserByUID(player.getPlayerIdentifier().userID());
                }
                InstanceDB.deleteInstanceByIID(gameId);
            }
        } catch (SQLException | GameException e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }
}
