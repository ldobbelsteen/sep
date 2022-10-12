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
import org.lukos.model.instances.InstanceState;
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
 * Test cases for {@link Archer}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class ArcherTest extends GameTest {

    private static final String ITEM = "arrow";
    private IInstance instance;
    private Player archerPlayer;
    private PlayerIdentifier archerPlayerID;
    private Player alivePlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("ArcherTest", "Sub1"), "User1");
            this.instance = InstanceManager.getInstanceManager().getInstance(testUser.createGame("ArcherTest", 1));
            this.archerPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("ArcherTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(4);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.alivePlayer.setMainRole(new Werewolf());

            this.archerPlayer.setMainRole(new Archer());
            this.archerPlayerID = this.archerPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the constructor initializes the character and group correctly.
     */
    @Test
    public void constructorTest() {
        Archer archer = new Archer();
        CharacterType type = CharacterType.SHADY;
        Group group = Group.TOWNSPEOPLE;

        assertEquals(type, archer.getCharacter(), String.format("Archer should have %s character type.", type));
        assertEquals(group, archer.getGroup(), String.format("Archer should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the right action. */
    @Test
    public void performActionTest() {
        try {
            Archer archer = new Archer();
            PreActionDT correctPreAction = new PreActionDT(this.archerPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            while (ItemDB.amountOfItems(this.archerPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.archerPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.archerPlayerID, ITEM);
            archer.performAction(correctPreAction, Action.SHOOT);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.archerPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.archerPlayerID.userID());
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
            Archer archer = new Archer();
            PreActionDT correctPreAction = new PreActionDT(this.archerPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            PreActionDT wrongPreAction =
                    new PreActionDT(this.archerPlayerID, new ActionEnc(new ArrayList<>(), new ArrayList<>()));

            while (ItemDB.amountOfItems(this.archerPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.archerPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                archer.performAction(correctPreAction, Action.SHOOT);
                fail("Without arrows you should not be able to shoot.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.archerPlayerID, ITEM);

            try {
                archer.performAction(correctPreAction, Action.HEAL);
                fail("Archer can only shoot.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = WrongInputException.class;

            try {
                archer.performAction(wrongPreAction, Action.SHOOT);
                fail("You cannot shoot without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NotAllowedToPerformActionException.class;
            this.instance.killPlayer(this.archerPlayer);

            try {
                archer.performAction(correctPreAction, Action.SHOOT);
                fail("You cannot shoot when you are not alive.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.archerPlayerID, ITEM);
            (new Archer()).initializeActions(this.archerPlayerID);
            assertEquals(amountOfItems, ItemDB.amountOfItems(this.archerPlayerID, ITEM),
                    "Amount of items of Archer shouldn't have changed.");
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
            while (ItemDB.amountOfItems(this.archerPlayerID, ITEM) < 1) { // making sure we have >0 arrows
                ItemDB.addPlayerItem(this.archerPlayerID, ITEM);
            }

            if (!this.archerPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.archerPlayer);
            }

            List<RoleActionInformation> informationList =
                    (new Archer()).getInformation(this.instance, this.archerPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");
            RoleActionInformation actionInformation = informationList.get(0);
            assertEquals(Action.SHOOT, actionInformation.action(), "Action should be SHOOT.");
            assertEquals(GeneralPurposeHelper.getAlivePlayersAsEligible(this.instance), actionInformation.eligible(),
                    "Eligible should be all alive players.");
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
            while (ItemDB.amountOfItems(this.archerPlayerID, ITEM) > 0) { // Making sure player has <1 arrows
                ItemDB.deletePlayerItem(this.archerPlayerID, ITEM);
            }

            if (!this.archerPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.archerPlayer);
            }
            assertEquals(new ArrayList<>(), (new Archer()).getInformation(this.instance, this.archerPlayerID),
                    "When the player has less then 1 arrow it should not get any information.");

            this.instance.killPlayer(this.archerPlayer);
            assertEquals(new ArrayList<>(), (new Archer()).getInformation(this.instance, this.archerPlayerID),
                    "When the player has less then 1 arrow and is deceased it should not get any information.");

            while (ItemDB.amountOfItems(this.archerPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.archerPlayerID, ITEM);
            }
            assertEquals(new ArrayList<>(), (new Archer()).getInformation(this.instance, this.archerPlayerID),
                    "When the player is deceased it should not get any information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s.
     */
    @Test
    public void getActionsTest() {
        Archer archer = new Archer();
        List<Action> actionList = archer.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.SHOOT);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest() {
        try {
            InstanceState instanceState = this.instance.getInstanceState();
            instanceState.setDay(12);
            Archer archer = new Archer();
            archer.replenishAction(0, this.archerPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.archerPlayerID, ITEM), "Archer should have 1 arrow at day 12.");

            instanceState.setDay(2);
            archer.replenishAction(0, this.archerPlayerID);
            assertEquals(0, ItemDB.amountOfItems(this.archerPlayerID, ITEM), "Archer should have 0 arrows at day 2.");

            instanceState.setDay(4);
            archer.replenishAction(0, this.archerPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.archerPlayerID, ITEM), "Archer should have 1 arrow at day 4.");

            instanceState.setDay(0);
            archer.replenishAction(0, this.archerPlayerID);
            assertEquals(0, ItemDB.amountOfItems(this.archerPlayerID, ITEM), "Archer should have 1 arrow at day 0.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
