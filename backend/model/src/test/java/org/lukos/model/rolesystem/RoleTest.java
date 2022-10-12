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
 * Test cases for {@link Role}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class RoleTest extends PurposeTest {

    /**
     * @utp.description Tests whether the constructor initializes its variables correctly.
     */
    @Test
    public void constructorTest() {
        for (CharacterType type : CharacterType.values()) {
            for (Group group : Group.values()) {
                Role role = new RoleImpl(type, group);
                assertEquals(type, role.getCharacter(),
                        "Role did not initialize the character " + type + " correctly.");
                assertEquals(group, role.getGroup(), "Role did not initialize the group " + group + " correctly.");
            }
        }
        Role role = new RoleImpl(null, null);
        assertNull(role.getGroup(), "Group should be null.");
        assertNull(role.getCharacter(), "Character should be null.");
    }

    /**
     * Helper class that implements a role, for testing purposes.
     */
    private static class RoleImpl extends Role {
        /**
         * Constructs a {@code Role}.
         *
         * @param character The character type of a role
         * @param group     Goal group of the given role
         */
        public RoleImpl(CharacterType character, Group group) {
            super(character, group);
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
