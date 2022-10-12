package org.lukos.model.actionsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.user.PlayerIdentifier;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for {@link CompAction}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 13-04-2022
 */
public class CompActionTest {

    /** Integer to keep track of executed action */
    private int tracker;

    /** the CompAction */
    CompAction compAction;

    @BeforeEach
    public void beforeEach() {
        tracker = 0;
        compAction = new CompAction("testAction");
    }

    /**
     * Creates and returns an {@code ActionDT} for a {@code PlusAction} for a non-existing {@code Player} and {@code Instance}.
     *
     * @return the generated {@code ActionDT} for a {@code PlusAction}
     */
    private ActionDT createPlusAction() {
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>());
        PreActionDT preActionDT = new PreActionDT(new PlayerIdentifier(1, 1), actionEnc);
        return new ActionDT(Instant.now(), new PlusAction(), preActionDT);
    }

    /**
     * General constructor test.
     *
     * @utp.description Test whether the object was initialed correctly.
     */
    @Test
    public void constructorTest() {
        CompAction consAction = new CompAction("constructorTest");

        // Test whether the object was initialed correctly
        assertEquals("constructorTest", consAction.getName(), "Name was not set correctly");
        assertEquals(new ArrayList<ActionDT>(), consAction.getChildren());
    }

    /**
     * getChildren test, get children when children list is empty.
     *
     * @utp.description Test whether the correct children are returned, when the children list is empty.
     */
    @Test
    public void getChildrenTest() {
        assertEquals(new ArrayList<ActionDT>(), compAction.getChildren());
    }

    /**
     * add test, get children when children's list is populated.
     *
     * @utp.description Test whether all children are correctly added.
     */
    @Test
    public void addTest() {
        List<ActionDT> expected = new ArrayList<>();

        // Create a few actions and add them to the compAction
        for (int i = 0; i < 3; i++) {
            ActionDT action = createPlusAction();
            expected.add(action);
            compAction.add(action);
        }

        // Test whether all children were added.
        assertEquals(expected, compAction.getChildren(), "The list were not equal!");
    }

    /**
     * remove test, try to remove a child when the list is empty.
     *
     * @utp.description Test whether the children list stays the same if the list is empty.
     */
    @Test
    public void removeEmptyTest() {
        // Remove an action that is not in the list.
        compAction.remove(createPlusAction());

        // Test the list is still empty
        assertEquals(new ArrayList<>(), compAction.getChildren(), "The list is not empty!");
    }

    /**
     * remove test, remove child that is part of the compAction.
     *
     * @utp.description Test whether a child is removed correctly if it is present in the children list.
     */
    @Test
    public void removePresentTest() {
        List<ActionDT> expected = new ArrayList<>();

        // Create a few actions and add them to the compAction
        for (int i = 0; i < 3; i++) {
            ActionDT action = createPlusAction();
            expected.add(action);
            compAction.add(action);
        }
        // Test all actions were added correctly
        assertEquals(expected, compAction.getChildren(), "The list were not equal before removing!");

        // Remove an action.
        compAction.remove(expected.remove(0));

        // Test the action was removed correctly
        assertEquals(expected, compAction.getChildren(), "The list were not equal after removing!");
    }

    /**
     * remove test, try to remove a child that is not part of the compAction.
     *
     * @utp.description Test whether the children list stays the same if remove is called with an {@code Action} that is not in the list.
     */
    @Test
    public void removeAbsentTest() {
        List<ActionDT> expected = new ArrayList<>();

        // Create a few actions and add them to the compAction
        for (int i = 0; i < 3; i++) {
            ActionDT action = createPlusAction();
            expected.add(action);
            compAction.add(action);
        }
        // Test all actions were added correctly
        assertEquals(expected, compAction.getChildren(), "The list were not equal before removing!");

        // Remove an action that is not in the list.
        compAction.remove(createPlusAction());

        // Test the action was removed correctly
        assertEquals(expected, compAction.getChildren(), "The list were not equal after removing!");
    }

    /**
     * execute test, children list is empty.
     *
     * @utp.description Test whether no {@code Actions} are executed if the children list is empty.
     */
    @Test
    public void executeEmptyTest() {
        // try to execute the actions
        try {
            compAction.execute(createPlusAction().preAction(), Instant.now(), 1);
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }

        // Test that the action was executed
        assertEquals(0, tracker, "No actions should have been executed!");
    }

    /**
     * execute test, children list has one action.
     *
     * @utp.description Test whether the correct {@code Action} are executed if the children list contains exactly one {@code Action}.
     */
    @Test
    public void executeSingleTest() {
        List<ActionDT> expected = new ArrayList<>();

        // Create one action and add it to the compAction
        ActionDT action = createPlusAction();
        expected.add(action);
        compAction.add(action);

        // Test whether the child was added.
        assertEquals(expected, compAction.getChildren(), "The list were not equal!");

        // Execute the action
        try {
            compAction.execute(createPlusAction().preAction(), Instant.now(), 1);
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }

        // Test that the action was executed
        assertEquals(1, tracker, "Not all or more actions than expected were executed!");
    }

    /**
     * execute test, children list is populated.
     *
     * @utp.description Test whether the correct {@code Actions} are executed if the children list is populated.
     */
    @Test
    public void executePopulatedTest() {
        List<ActionDT> expected = new ArrayList<>();

        // Create a few actions and add them to the compAction
        for (int i = 0; i < 3; i++) {
            ActionDT action = createPlusAction();
            expected.add(action);
            compAction.add(action);
        }

        // Test whether all children were added.
        assertEquals(expected, compAction.getChildren(), "The list were not equal!");

        // Execute all actions
        try {
            compAction.execute(createPlusAction().preAction(), Instant.now(), 1);
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }

        // Test that all actions were executed
        assertEquals(3, tracker, "Not all or more actions than expected were executed!");
    }

    /** Action used for testing */
    private class PlusAction extends Action {

        public PlusAction() {
            super("PlusAction");
        }

        @Override
        public void execute(PreActionDT data, Instant time, int actionId) throws SQLException, ReflectiveOperationException, GameException {
            tracker++;
        }
    }
}
