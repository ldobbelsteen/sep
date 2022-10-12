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
 * Test cases for {@link Healer}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class HealerTest extends GameTest {

    private static final String ITEM = "medkit";
    private IInstance instance;
    private Player healerPlayer;
    private PlayerIdentifier healerPlayerID;
    private Player alivePlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("HealerTest", "Sub1"), "User1");
            this.instance = InstanceManager.getInstanceManager().getInstance(testUser.createGame("HealerTest", 1));
            this.healerPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("HealerTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(1);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            MainRole alivePlayerRole = new Werewolf();
            this.alivePlayer.setMainRole(alivePlayerRole);

            this.healerPlayer.setMainRole(new Healer());
            this.healerPlayerID = this.healerPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        Healer healer = new Healer();
        CharacterType type = CharacterType.NOT_SHADY;
        Group group = Group.TOWNSPEOPLE;

        assertEquals(type, healer.getCharacter(), String.format("Healer should have %s character type.", type));
        assertEquals(group, healer.getGroup(), String.format("Healer should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionsTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.healerPlayerID, ITEM);
            (new Healer()).initializeActions(this.healerPlayerID);
            assertEquals(amountOfItems + 1, ItemDB.amountOfItems(this.healerPlayerID, ITEM),
                    "The amount of items of healer should have been increased by 1.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the right action. */
    @Test
    public void performActionTest() {
        try {
            Healer healer = new Healer();
            PreActionDT correctPreAction = new PreActionDT(this.healerPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            while (ItemDB.amountOfItems(this.healerPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.healerPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.healerPlayerID, ITEM);
            healer.performAction(correctPreAction, Action.HEAL);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.healerPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.healerPlayerID.userID());
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
            Healer healer = new Healer();
            PreActionDT correctPreAction = new PreActionDT(this.healerPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            PreActionDT wrongPreAction =
                    new PreActionDT(this.healerPlayerID, new ActionEnc(new ArrayList<>(), new ArrayList<>()));

            while (ItemDB.amountOfItems(this.healerPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.healerPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                healer.performAction(correctPreAction, Action.HEAL);
                fail("Without medkits you should not be able to heal.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.healerPlayerID, ITEM);

            try {
                healer.performAction(correctPreAction, Action.SHOOT);
                fail("Healer can only heal.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = WrongInputException.class;

            try {
                healer.performAction(wrongPreAction, Action.HEAL);
                fail("You cannot heal without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NotAllowedToPerformActionException.class;
            this.instance.killPlayer(this.healerPlayer);

            try {
                healer.performAction(correctPreAction, Action.HEAL);
                fail("You cannot heal when you are not alive.");
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
            while (ItemDB.amountOfItems(this.healerPlayerID, ITEM) < 1) { // making sure we have >0 medkit
                ItemDB.addPlayerItem(this.healerPlayerID, ITEM);
            }

            if (!this.healerPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.healerPlayer);
            }
            this.instance.getInstanceState().setPhase(DayPhase.MORNING); // Making sure it is evening

            List<RoleActionInformation> informationList =
                    (new Healer()).getInformation(this.instance, this.healerPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");
            RoleActionInformation actionInformation = informationList.get(0);
            assertEquals(Action.HEAL, actionInformation.action(), "Action should be HEAL.");
            assertEquals(GeneralPurposeHelper.getToBeExecutedPlayersAsEligible(this.instance),
                    actionInformation.eligible(), "Eligible should be all players that are to be executed.");
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
            while (ItemDB.amountOfItems(this.healerPlayerID, ITEM) > 0) { // Making sure player has <1 medkit
                ItemDB.deletePlayerItem(this.healerPlayerID, ITEM);
            }

            if (!this.healerPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.healerPlayer);
            }

            this.instance.getInstanceState().setPhase(DayPhase.MORNING); // Making sure it is morning
            assertEquals(new ArrayList<>(), (new Healer()).getInformation(this.instance, this.healerPlayerID),
                    "When the player has less then 1 medkit it should not get any information.");

            while (ItemDB.amountOfItems(this.healerPlayerID, ITEM) < 1) { // Make sure player has >0 medkits
                ItemDB.addPlayerItem(this.healerPlayerID, ITEM);
            }

            this.instance.getInstanceState().setPhase(DayPhase.DAY); // Making sure it is not morning
            assertEquals(new ArrayList<>(), (new Healer()).getInformation(this.instance, this.healerPlayerID),
                    "When it is not morning the player should not get any information.");
            this.instance.getInstanceState().setPhase(DayPhase.MORNING); // Making sure it is morning

            this.instance.killPlayer(this.healerPlayer); // Making sure player is deceased
            assertEquals(new ArrayList<>(), (new Healer()).getInformation(this.instance, this.healerPlayerID),
                    "When the player is deceased it should not get any information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        Healer healer = new Healer();
        List<Action> actionList = healer.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.HEAL);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.healerPlayerID, ITEM);
            (new Healer()).replenishAction(1, this.healerPlayerID);
            assertEquals(amountOfItems, ItemDB.amountOfItems(this.healerPlayerID, ITEM),
                    "Amount of items of Healer should still be the same.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
