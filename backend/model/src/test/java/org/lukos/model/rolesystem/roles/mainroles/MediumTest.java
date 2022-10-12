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
 * Test cases for {@link Medium}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class MediumTest extends GameTest {

    private static final String ITEM = "revive";
    private IInstance instance;
    private Player mediumPlayer;
    private PlayerIdentifier mediumPlayerID;
    private Player alivePlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("MediumTest", "Sub1"), "User1");
            this.instance = InstanceManager.getInstanceManager().getInstance(testUser.createGame("MediumTest", 1));
            this.mediumPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("MediumTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(4);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.alivePlayer.setMainRole(new Werewolf());

            this.mediumPlayer.setMainRole(new Medium());
            this.mediumPlayerID = this.mediumPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
    
    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        Medium medium = new Medium();
        CharacterType type = CharacterType.NOT_SHADY;
        Group group = Group.TOWNSPEOPLE;

        assertEquals(type, medium.getCharacter(), String.format("Medium should have %s character type.", type));
        assertEquals(group, medium.getGroup(), String.format("Medium should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.mediumPlayerID, ITEM);
            (new Medium()).initializeActions(this.mediumPlayerID);
            assertEquals(amountOfItems + 1, ItemDB.amountOfItems(this.mediumPlayerID, ITEM),
                    "Amount of items of Medium should have increased by 1.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the right action. */
    @Test
    public void performActionTest() {
        try {
            Medium medium = new Medium();
            this.instance.killPlayer(this.alivePlayer);
            PreActionDT correctPreAction = new PreActionDT(this.mediumPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            while (ItemDB.amountOfItems(this.mediumPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.mediumPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.mediumPlayerID, ITEM);
            medium.performAction(correctPreAction, Action.REVIVE);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.mediumPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.mediumPlayerID.userID());
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
            Medium medium = new Medium();
            PreActionDT correctPreAction = new PreActionDT(this.mediumPlayerID,
                    new ActionEnc(new ArrayList<>(), Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            PreActionDT wrongPreAction = new PreActionDT(this.mediumPlayerID, new ActionEnc(new ArrayList<>(), new ArrayList<>()));

            while (ItemDB.amountOfItems(this.mediumPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.mediumPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                medium.performAction(correctPreAction, Action.REVIVE);
                fail("Without revive items you should not be able to revive.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.mediumPlayerID, ITEM);

            try {
                medium.performAction(correctPreAction, Action.HEAL);
                fail("Medium can only revive.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = WrongInputException.class;

            try {
                medium.performAction(wrongPreAction, Action.REVIVE);
                fail("You cannot revive without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NotAllowedToPerformActionException.class;
            this.instance.killPlayer(this.mediumPlayer);

            try {
                medium.performAction(correctPreAction, Action.REVIVE);
                fail("You cannot revive when you are not alive.");
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
            while (ItemDB.amountOfItems(this.mediumPlayerID, ITEM) < 1) { // making sure we have >0 arrows
                ItemDB.addPlayerItem(this.mediumPlayerID, ITEM);
            }

            if (!this.mediumPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.mediumPlayer);
            }

            List<RoleActionInformation> informationList =
                    (new Medium()).getInformation(this.instance, this.mediumPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");
            RoleActionInformation actionInformation = informationList.get(0);
            assertEquals(Action.REVIVE, actionInformation.action(), "Action should be REVIVE.");
            assertEquals(GeneralPurposeHelper.getDeadPlayersAsEligible(this.instance), actionInformation.eligible(),
                    "Eligible should be all dead players.");
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
            while (ItemDB.amountOfItems(this.mediumPlayerID, ITEM) > 0) { // Making sure player has <1 arrows
                ItemDB.deletePlayerItem(this.mediumPlayerID, ITEM);
            }

            if (!this.mediumPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.mediumPlayer);
            }
            assertEquals(new ArrayList<>(), (new Medium()).getInformation(this.instance, this.mediumPlayerID),
                    "When the player has less then 1 revive it should not get any information.");

            this.instance.killPlayer(this.mediumPlayer);
            assertEquals(new ArrayList<>(), (new Medium()).getInformation(this.instance, this.mediumPlayerID),
                    "When the player has less then 1 revive and is deceased it should not get any information.");

            while (ItemDB.amountOfItems(this.mediumPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.mediumPlayerID, ITEM);
            }
            assertEquals(new ArrayList<>(), (new Medium()).getInformation(this.instance, this.mediumPlayerID),
                    "When the player is deceased it should not get any information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        Medium medium = new Medium();
        List<Action> actionList = medium.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.REVIVE);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.mediumPlayerID, ITEM);
            (new Medium()).replenishAction(1, this.mediumPlayerID);
            assertEquals(amountOfItems, ItemDB.amountOfItems(this.mediumPlayerID, ITEM),
                    "Amount of items of Medium should still be the same.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
