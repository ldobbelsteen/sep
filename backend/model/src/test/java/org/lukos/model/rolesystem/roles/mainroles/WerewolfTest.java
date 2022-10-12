package org.lukos.model.rolesystem.roles.mainroles;

import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.user.PlayerIdentifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link Werewolf}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class WerewolfTest extends GameTest {

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        Werewolf werewolf = new Werewolf();
        CharacterType type = CharacterType.SHADY;
        Group group = Group.WEREWOLVES;

        assertEquals(type, werewolf.getCharacter(), String.format("Werewolf should have %s character type.", type));
        assertEquals(group, werewolf.getGroup(), String.format("Werewolf should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code initializeActions()} function shows the right behaviour. */
    @Test
    public void initializeActionsTest() {
        try {
            (new Werewolf()).initializeActions(new PlayerIdentifier(1, 1));
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
            (new Werewolf()).performAction(null, null);
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /** @utp.description Tests whether the function {@code getInformation()} shows the right behaviour. */
    @Test
    public void getInformationTest() {
        try {
            List<RoleActionInformation> informationList =
                    (new Werewolf()).getInformation(null, new PlayerIdentifier(1, 1));

            assertEquals(0, informationList.size(), "List should be of the same size.");
            assertTrue((new ArrayList<>()).containsAll(informationList), "Actions should be expected.");
            assertTrue(informationList.containsAll(new ArrayList<RoleActionInformation>()),
                    "Actions should hold all expected.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        Werewolf werewolf = new Werewolf();
        List<Action> actionList = werewolf.getActions();

        assertEquals(0, actionList.size(), "List should be of the same size.");
        assertTrue((new ArrayList<>()).containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(new ArrayList<Action>()), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function shows the right behaviour. */
    @Test
    public void replenishActionTest() {
        try {
            (new Werewolf()).replenishAction(0, new PlayerIdentifier(1, 1));
            assertTrue(true, "replenishAction() didn't thrown an error.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
