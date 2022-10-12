package org.lukos.model.rolesystem.roles.mainroles;

import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.PlayerIdentifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link Townsperson}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class TownspersonTest extends GameTest {

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        Townsperson townsperson = new Townsperson();
        CharacterType type = CharacterType.NOT_SHADY;
        Group group = Group.TOWNSPEOPLE;

        assertEquals(type, townsperson.getCharacter(),
                String.format("Townsperson should have %s character type.", type));
        assertEquals(group, townsperson.getGroup(), String.format("Townsperson should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code initializeActions()} function shows the right behaviour. */
    @Test
    public void initializeActionsTest() {
        try {
            (new Townsperson()).initializeActions(new PlayerIdentifier(1, 1));
            assertTrue(true, "initializeActions() didn't thrown an error.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the function {@code performAction()} shows the right behaviour. */
    @Test
    public void performAction() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            (new Townsperson()).performAction(null, null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether the function {@code getInformation()} shows the right behaviour. */
    @Test
    public void getInformationTest() {
        Class<?> expected = UnsupportedOperationException.class;
        try {
            (new Townsperson()).getInformation(null, new PlayerIdentifier(1, 1));
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        Townsperson townsperson = new Townsperson();
        List<Action> actionList = townsperson.getActions();

        assertEquals(0, actionList.size(), "List should be of the same size.");
        assertTrue((new ArrayList<>()).containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(new ArrayList<Action>()), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function shows the right behaviour. */
    @Test
    public void replenishActionTest() {
        try {
            (new Townsperson()).replenishAction(0, new PlayerIdentifier(1, 1));
            assertTrue(true, "replenishAction() didn't thrown an error.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
