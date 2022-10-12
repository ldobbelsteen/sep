package org.lukos.model.rolesystem;

import org.junit.jupiter.api.Test;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test cases for {@link Purpose}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class PurposeTest {

    /**
     * @utp.description Tests whether the constructor initializes its variables correctly.
     */
    @Test
    public void constructorTest() {
        for (CharacterType type : CharacterType.values()) {
            Purpose purpose = new PurposeImpl(type);
            assertEquals(type, purpose.getCharacter(),
                    "Purpose did not initialize the character " + type + "correctly.");
        }
        Purpose purpose = new PurposeImpl(null);
        assertNull(purpose.getCharacter(), "Character should be null.");
    }

    /**
     * Helper class that implements a purpose, for testing purposes.
     */
    private static class PurposeImpl extends Purpose {
        /**
         * Constructs a {@code Purpose}.
         *
         * @param character The character type of role/job
         */
        public PurposeImpl(CharacterType character) {
            super(character);
        }

        @Override
        public void initializeActions(PlayerIdentifier player) throws SQLException, GameException {
            throw new UnsupportedOperationException(
                    "This class does not implement this method, this is used for test" + " purposes.");
        }

        @Override
        public void performAction(PreActionDT data, Action action)
                throws GameException, SQLException, ReflectiveOperationException {
            throw new UnsupportedOperationException(
                    "This class does not implement this method, this is used for test" + " purposes.");
        }

        @Override
        public List<RoleActionInformation> getInformation(IInstance instance, PlayerIdentifier playerIdentifier)
                throws SQLException, GameException, ReflectiveOperationException {
            throw new UnsupportedOperationException(
                    "This class does not implement this method, this is used for test" + " purposes.");
        }

        @Override
        public List<Action> getActions() {
            throw new UnsupportedOperationException(
                    "This class does not implement this method, this is used for test" + " purposes.");
        }

        @Override
        public void replenishAction(int gameSpeed, PlayerIdentifier playerIdentifier)
                throws GameException, SQLException {
            throw new UnsupportedOperationException(
                    "This class does not implement this method, this is used for test" + " purposes.");
        }
    }
}
