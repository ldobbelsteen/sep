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
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.rolesystem.util.GeneralPurposeHelper;
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
 * Test cases for {@link Poisoner}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class PoisonerTest extends GameTest {

    private static final String ITEM = "poison";
    private IInstance instance;
    private Player poisonerPlayer;
    private PlayerIdentifier poisonerPlayerID;
    private Player alivePlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("PoisonerTest", "Sub1"), "User1");
            this.instance = InstanceManager.getInstanceManager().getInstance(testUser.createGame("PoisonerTest", 1));
            this.poisonerPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("PoisonerTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(1);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.alivePlayer.setMainRole(new Werewolf());

            this.poisonerPlayer.setMainRole(new Poisoner());
            this.poisonerPlayerID = this.poisonerPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        Poisoner poisoner = new Poisoner();
        CharacterType type = CharacterType.SHADY;
        Group group = Group.TOWNSPEOPLE;

        assertEquals(type, poisoner.getCharacter(), String.format("Poisoner should have %s character type.", type));
        assertEquals(group, poisoner.getGroup(), String.format("Poisoner should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionsTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.poisonerPlayerID, ITEM);
            (new Poisoner()).initializeActions(this.poisonerPlayerID);
            assertEquals(amountOfItems + 1, ItemDB.amountOfItems(this.poisonerPlayerID, ITEM),
                    "The amount of items of Poisoner should have been increased by 1.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the right action. */
    @Test
    public void performActionTest() {
        try {
            Poisoner poisoner = new Poisoner();
            PreActionDT correctPreAction = new PreActionDT(this.poisonerPlayerID,
                    new ActionEnc(Collections.singletonList(this.alivePlayer.getHouse()), new ArrayList<>()));
            while (ItemDB.amountOfItems(this.poisonerPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.poisonerPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.poisonerPlayerID, ITEM);
            poisoner.performAction(correctPreAction, Action.POISON);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.poisonerPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.poisonerPlayerID.userID());
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
            Poisoner poisoner = new Poisoner();
            PreActionDT correctPreAction = new PreActionDT(this.poisonerPlayerID,
                    new ActionEnc(Collections.singletonList(this.alivePlayer.getHouse()), new ArrayList<>()));

            while (ItemDB.amountOfItems(this.poisonerPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.poisonerPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                poisoner.performAction(correctPreAction, Action.POISON);
                fail("Without poison you should not be able to see poison.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.poisonerPlayerID, ITEM);

            try {
                poisoner.performAction(correctPreAction, Action.HEAL);
                fail("Poisoner can only poison.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = WrongInputException.class;
            PreActionDT wrongPreAction =
                    new PreActionDT(this.poisonerPlayerID, new ActionEnc(new ArrayList<>(), new ArrayList<>()));

            try {
                poisoner.performAction(wrongPreAction, Action.POISON);
                fail("You cannot poison without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            wrongPreAction = new PreActionDT(this.poisonerPlayerID,
                    new ActionEnc(Collections.singletonList(this.alivePlayer.getHouse()),
                            Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));

            try {
                poisoner.performAction(wrongPreAction, Action.POISON);
                fail("You cannot poison without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NotAllowedToPerformActionException.class;
            this.instance.killPlayer(this.poisonerPlayer);

            try {
                poisoner.performAction(correctPreAction, Action.POISON);
                fail("You cannot poison when you are not alive.");
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
     * @utp.description Tests whether the {@code getInformation()} function returns the right information when met with
     * the right requirements.
     */
    @Test
    public void getInformationTest() {
        try {
            while (ItemDB.amountOfItems(this.poisonerPlayerID, ITEM) < 1) { // making sure we have >0 poison
                ItemDB.addPlayerItem(this.poisonerPlayerID, ITEM);
            }

            if (!this.poisonerPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.poisonerPlayer);
            }
            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening

            List<RoleActionInformation> informationList =
                    (new Poisoner()).getInformation(this.instance, this.poisonerPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");
            RoleActionInformation actionInformation = informationList.get(0);
            assertEquals(Action.POISON, actionInformation.action(), "Action should be POISON.");
            assertEquals(GeneralPurposeHelper.getLocationsAsEligible(this.instance), actionInformation.eligible(),
                    "Eligible should be all locations.");
            assertEquals(1, actionInformation.numberOfVotes(), "Number of votes should be 1.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation} function returns an empty list if the right
     * requirements are not met.
     */
    @Test
    public void getEmptyInformationTest() {
        try {
            while (ItemDB.amountOfItems(this.poisonerPlayerID, ITEM) > 0) { // Making sure player has <1 poison
                ItemDB.deletePlayerItem(this.poisonerPlayerID, ITEM);
            }

            if (!this.poisonerPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.poisonerPlayer);
            }

            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening
            assertEquals(new ArrayList<>(), (new Poisoner()).getInformation(this.instance, this.poisonerPlayerID),
                    "When the player has less then 1 poison it should not get any information.");

            while (ItemDB.amountOfItems(this.poisonerPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.poisonerPlayerID, ITEM);
            }

            this.instance.getInstanceState().setPhase(DayPhase.DAY);
            assertEquals(new ArrayList<>(), (new Poisoner()).getInformation(this.instance, this.poisonerPlayerID),
                    "When it is not evening the player should not get any information.");
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.instance.killPlayer(this.poisonerPlayer);
            assertEquals(new ArrayList<>(), (new Poisoner()).getInformation(this.instance, this.poisonerPlayerID),
                    "When the player is deceased it should not get any information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        Poisoner poisoner = new Poisoner();
        List<Action> actionList = poisoner.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.POISON);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.poisonerPlayerID, ITEM);
            (new Poisoner()).replenishAction(1, this.poisonerPlayerID);
            assertEquals(amountOfItems, ItemDB.amountOfItems(this.poisonerPlayerID, ITEM),
                    "Amount of items of Poisoner should still be the same.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
