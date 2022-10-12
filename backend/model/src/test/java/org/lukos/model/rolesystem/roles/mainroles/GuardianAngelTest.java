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
 * Test cases for {@link GuardianAngel}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-04-2022
 */
public class GuardianAngelTest extends GameTest {

    private static final String ITEM = "guard";
    private IInstance instance;
    private Player guardianAngelPlayer;
    private PlayerIdentifier guardianAngelPlayerID;
    private Player alivePlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("GuardianAngelTest", "Sub1"), "User1");
            this.instance =
                    InstanceManager.getInstanceManager().getInstance(testUser.createGame("GuardianAngelTest", 1));
            this.guardianAngelPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("GuardianAngelTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(1);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.alivePlayer.setMainRole(new Werewolf());

            this.guardianAngelPlayer.setMainRole(new GuardianAngel());
            this.guardianAngelPlayerID = this.guardianAngelPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        GuardianAngel guardianAngel = new GuardianAngel();
        CharacterType type = CharacterType.NOT_SHADY;
        Group group = Group.TOWNSPEOPLE;

        assertEquals(type, guardianAngel.getCharacter(),
                String.format("GuardianAngel should have %s character type.", type));
        assertEquals(group, guardianAngel.getGroup(), String.format("GuardianAngel should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionsTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM);
            (new GuardianAngel()).initializeActions(this.guardianAngelPlayerID);
            assertEquals(amountOfItems, ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM),
                    "Amount of items of GuardianAngel shouldn't have changed.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the right action. */
    @Test
    public void performActionTest() {
        try {
            GuardianAngel guardianAngel = new GuardianAngel();
            PreActionDT correctPreAction = new PreActionDT(this.guardianAngelPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            while (ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.guardianAngelPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM);
            guardianAngel.performAction(correctPreAction, Action.PROTECT);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.guardianAngelPlayerID.userID());
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
            GuardianAngel guardianAngel = new GuardianAngel();
            PreActionDT correctPreAction = new PreActionDT(this.guardianAngelPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            PreActionDT wrongPreAction =
                    new PreActionDT(this.guardianAngelPlayerID, new ActionEnc(new ArrayList<>(), new ArrayList<>()));

            while (ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.guardianAngelPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                guardianAngel.performAction(correctPreAction, Action.PROTECT);
                fail("Without guards you should not be able to protect.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.guardianAngelPlayerID, ITEM);

            try {
                guardianAngel.performAction(correctPreAction, Action.HEAL);
                fail("GuardianAngel can only protect.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = WrongInputException.class;

            try {
                guardianAngel.performAction(wrongPreAction, Action.PROTECT);
                fail("You cannot protect without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NotAllowedToPerformActionException.class;
            this.instance.killPlayer(this.guardianAngelPlayer);

            try {
                guardianAngel.performAction(correctPreAction, Action.PROTECT);
                fail("You cannot protect when you are not alive.");
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
            while (ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM) < 1) { // making sure we have >0 guard
                ItemDB.addPlayerItem(this.guardianAngelPlayerID, ITEM);
            }

            if (!this.guardianAngelPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.guardianAngelPlayer);
            }
            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening

            List<RoleActionInformation> informationList =
                    (new GuardianAngel()).getInformation(this.instance, this.guardianAngelPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");
            RoleActionInformation actionInformation = informationList.get(0);
            assertEquals(Action.PROTECT, actionInformation.action(), "Action should be PROTECT.");
            assertEquals(GeneralPurposeHelper.getAlivePlayersAsEligible(this.instance), actionInformation.eligible(),
                    "Eligible should be all players.");
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
            while (ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM) > 0) { // Making sure player has <1 guard
                ItemDB.deletePlayerItem(this.guardianAngelPlayerID, ITEM);
            }

            if (!this.guardianAngelPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.guardianAngelPlayer);
            }

            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening
            assertEquals(new ArrayList<>(),
                    (new GuardianAngel()).getInformation(this.instance, this.guardianAngelPlayerID),
                    "When the player has less then 1 guard it should not get any information.");

            while (ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.guardianAngelPlayerID, ITEM);
            }

            this.instance.getInstanceState().setPhase(DayPhase.DAY);
            assertEquals(new ArrayList<>(),
                    (new GuardianAngel()).getInformation(this.instance, this.guardianAngelPlayerID),
                    "When it is not evening the player should not get any information.");
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.instance.killPlayer(this.guardianAngelPlayer);
            assertEquals(new ArrayList<>(),
                    (new GuardianAngel()).getInformation(this.instance, this.guardianAngelPlayerID),
                    "When the player is deceased it should not get any information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        GuardianAngel guardianAngel = new GuardianAngel();
        List<Action> actionList = guardianAngel.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.PROTECT);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest() {
        try {
            (new GuardianAngel()).replenishAction(0, this.guardianAngelPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM),
                    "GuardianAngel should have had 1 item after replenishing.");

            for (int i = 0; i < 5; i++) {
                ItemDB.addPlayerItem(this.guardianAngelPlayerID, ITEM);
            }
            assertEquals(6, ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM),
                    "GuardianAngel should have 6 items.");
            (new GuardianAngel()).replenishAction(1, this.guardianAngelPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.guardianAngelPlayerID, ITEM),
                    "GuardianAngel should have had 1 item after replenishing, even with 5 items.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
