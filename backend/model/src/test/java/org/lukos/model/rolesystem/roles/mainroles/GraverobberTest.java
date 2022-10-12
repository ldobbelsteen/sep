package org.lukos.model.rolesystem.roles.mainroles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.DatabaseConnection;
import org.lukos.database.ItemDB;
import org.lukos.model.GameTest;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.actionsystem.NoSuchActionException;
import org.lukos.model.exceptions.actionsystem.NotAllowedToPerformActionException;
import org.lukos.model.exceptions.actionsystem.WrongInputException;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.*;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link Graverobber}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class GraverobberTest extends GameTest {

    private static final String ITEM = "grave";
    private IInstance instance;
    private Player graverobberPlayer;
    private PlayerIdentifier graverobberPlayerID;
    private Player deadPlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("GraverobberTest", "Sub1"), "User1");
            this.instance = InstanceManager.getInstanceManager().getInstance(testUser.createGame("GraverobberTest", 1));
            this.graverobberPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("GraverobberTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.deadPlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(1);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            MainRole deadPlayerRole = new Werewolf();
            this.deadPlayer.setMainRole(deadPlayerRole);
            this.instance.killPlayer(deadPlayer);

            this.graverobberPlayer.setMainRole(new Graverobber());
            this.graverobberPlayerID = this.graverobberPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        Graverobber graverobber = new Graverobber();
        assertEquals(CharacterType.VAGUE, graverobber.getCharacter(), "Graverobber should have VAGUE character type.");
        assertEquals(Group.NONWINNING, graverobber.getGroup(), "Graverobber should have NONWINNING group.");
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionsTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.graverobberPlayerID, ITEM);
            (new Graverobber()).initializeActions(this.graverobberPlayerID);
            assertEquals(amountOfItems + 1, ItemDB.amountOfItems(this.graverobberPlayerID, ITEM),
                    "The amount of items of graverobber should have been increased by 1.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the right action. */
    @Test
    public void performActionTest() {
        try {
            Graverobber graverobber = new Graverobber();
            PreActionDT correctPreAction = new PreActionDT(this.graverobberPlayerID,
                    new ActionEnc(new ArrayList<>(), Collections.singletonList(this.deadPlayer.getPlayerIdentifier())));
            while (ItemDB.amountOfItems(this.graverobberPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.graverobberPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.graverobberPlayerID, ITEM);
            graverobber.performAction(correctPreAction, Action.ROB_GRAVE);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.graverobberPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.graverobberPlayerID.userID());
            ResultSet resultSet = DatabaseConnection.getInstance().readStatement(preparedStatement);
            assertTrue(resultSet.next(), "There should exist an action.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function will output the right exceptions. */
    @Test
    public void performActionExceptionTest() {
        try {
            Graverobber graverobber = new Graverobber();
            PreActionDT correctPreAction = new PreActionDT(this.graverobberPlayerID,
                    new ActionEnc(new ArrayList<>(), Collections.singletonList(this.deadPlayer.getPlayerIdentifier())));

            while (ItemDB.amountOfItems(this.graverobberPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.graverobberPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                graverobber.performAction(correctPreAction, Action.ROB_GRAVE);
                fail("Without grave items you should not be able to rob graves.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.graverobberPlayerID, ITEM);

            try {
                graverobber.performAction(correctPreAction, Action.HEAL);
                fail("Graverobber can only rob graves.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = WrongInputException.class;
            PreActionDT wrongPreAction =
                    new PreActionDT(this.graverobberPlayerID, new ActionEnc(new ArrayList<>(), new ArrayList<>()));

            try {
                graverobber.performAction(wrongPreAction, Action.ROB_GRAVE);
                fail("You cannot rob graves without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            wrongPreAction = new PreActionDT(this.graverobberPlayerID,
                    new ActionEnc(Collections.singletonList(this.graverobberPlayer.getHouse()), new ArrayList<>()));

            try {
                graverobber.performAction(wrongPreAction, Action.ROB_GRAVE);
                fail("You cannot rob graves without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            this.instance.revivePlayer(this.deadPlayer);
            try {
                graverobber.performAction(correctPreAction, Action.ROB_GRAVE);
                fail("You cannot rob graves if the target does not has a grave.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }
            this.instance.killPlayer(this.deadPlayer);

            expected = NotAllowedToPerformActionException.class;
            this.instance.killPlayer(this.graverobberPlayer);

            try {
                graverobber.performAction(correctPreAction, Action.ROB_GRAVE);
                fail("You cannot rob graves when you are not alive.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation()} function returns the right information when the right
     * requirements are met.
     */
    @Test
    public void getInformationTest1() {
        try {
            if (ItemDB.amountOfItems(this.graverobberPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.graverobberPlayerID, ITEM);
            }

            List<RoleActionInformation> roleActionInformationList =
                    (new Graverobber()).getInformation(this.instance, this.graverobberPlayerID);

            assertEquals(1, roleActionInformationList.size(), "Graverobber information should be size 1.");
            RoleActionInformation information = roleActionInformationList.get(0);
            assertEquals(1, information.numberOfVotes(), "Graverobber should only get 1 vote.");
            assertEquals(Action.ROB_GRAVE, information.action(), "Graverobber should have action ROB_GRAVE.");
            assertEquals(1, information.eligible().size(), "Eligible map should be size 1.");
            assertTrue(information.eligible().containsKey(EligibleType.PLAYER), "Eligible type should only be PLAYER.");
            assertEquals(1, information.eligible().get(EligibleType.PLAYER).size(),
                    "Only 1 player should be in the " + "eligible list");
            assertTrue(information.eligible().get(EligibleType.PLAYER)
                            .contains(this.deadPlayer.getPlayerIdentifier().userID()),
                    "Dead player should be in the eligible list of graverobber.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation()} function returns no information if the right
     * requirements are not met.
     */
    @Test
    public void getInformationTest2() {
        try {
            while (ItemDB.amountOfItems(this.graverobberPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.graverobberPlayerID, ITEM);
            }

            assertEquals(new ArrayList<>(), (new Graverobber()).getInformation(this.instance, this.graverobberPlayerID),
                    "Graverobber information should be empty.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation()} function returns no information if the right
     * requirements are not met.
     */
    @Test
    public void getInformationTest3() {
        try {
            if (ItemDB.amountOfItems(this.graverobberPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.graverobberPlayerID, ITEM);
            }
            this.instance.killPlayer(this.graverobberPlayer);
            assertEquals(new ArrayList<>(), (new Graverobber()).getInformation(this.instance, this.graverobberPlayerID),
                    "Graverobber information should be empty.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        Graverobber graverobber = new Graverobber();
        List<Action> actionList = graverobber.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.ROB_GRAVE);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.graverobberPlayerID, ITEM);
            (new Graverobber()).replenishAction(1, this.graverobberPlayerID);
            assertEquals(amountOfItems, ItemDB.amountOfItems(this.graverobberPlayerID, ITEM),
                    "Amount of items of Graverobber should still be the same.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
