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
 * Test cases for {@link Clairvoyant}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class ClairvoyantTest extends GameTest {

    private static final String ITEM = "seen";
    private IInstance instance;
    private Player clairvoyantPlayer;
    private PlayerIdentifier clairvoyantPlayerID;
    private Player alivePlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("ClairvoyantTest", "Sub1"), "User1");
            this.instance = InstanceManager.getInstanceManager().getInstance(testUser.createGame("ClairvoyantTest", 1));
            this.clairvoyantPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("ClairvoyantTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(1);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.alivePlayer.setMainRole(new Werewolf());

            this.clairvoyantPlayer.setMainRole(new Clairvoyant());
            this.clairvoyantPlayerID = this.clairvoyantPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        Clairvoyant clairvoyant = new Clairvoyant();
        CharacterType type = CharacterType.NOT_SHADY;
        Group group = Group.TOWNSPEOPLE;

        assertEquals(type, clairvoyant.getCharacter(),
                String.format("Clairvoyant should have %s character type.", type));
        assertEquals(group, clairvoyant.getGroup(), String.format("Clairvoyant should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionsTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM);
            (new Clairvoyant()).initializeActions(this.clairvoyantPlayerID);
            assertEquals(amountOfItems, ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM),
                    "Amount of items of Clairvoyant shouldn't have changed.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the right action. */
    @Test
    public void performActionTest() {
        try {
            Clairvoyant clairvoyant = new Clairvoyant();
            PreActionDT correctPreAction = new PreActionDT(this.clairvoyantPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            while (ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.clairvoyantPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM);
            clairvoyant.performAction(correctPreAction, Action.CLAIRVOYANT_SEE_ROLE);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.clairvoyantPlayerID.userID());
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
            Clairvoyant clairvoyant = new Clairvoyant();
            PreActionDT correctPreAction = new PreActionDT(this.clairvoyantPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));

            while (ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.clairvoyantPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                clairvoyant.performAction(correctPreAction, Action.CLAIRVOYANT_SEE_ROLE);
                fail("Without seen items you should not be able to see roles.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.clairvoyantPlayerID, ITEM);

            try {
                clairvoyant.performAction(correctPreAction, Action.HEAL);
                fail("Clairvoyant can only see roles.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = WrongInputException.class;
            PreActionDT wrongPreAction =
                    new PreActionDT(this.clairvoyantPlayerID, new ActionEnc(new ArrayList<>(), new ArrayList<>()));

            try {
                clairvoyant.performAction(wrongPreAction, Action.CLAIRVOYANT_SEE_ROLE);
                fail("You cannot see roles without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            wrongPreAction = new PreActionDT(this.clairvoyantPlayerID,
                    new ActionEnc(Collections.singletonList(this.clairvoyantPlayer.getHouse()), new ArrayList<>()));

            try {
                clairvoyant.performAction(wrongPreAction, Action.CLAIRVOYANT_SEE_ROLE);
                fail("You cannot see roles without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NotAllowedToPerformActionException.class;
            this.instance.killPlayer(this.clairvoyantPlayer);

            try {
                clairvoyant.performAction(correctPreAction, Action.CLAIRVOYANT_SEE_ROLE);
                fail("You cannot see roles when you are not alive.");
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
            while (ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM) < 1) { // making sure we have >0 seen
                ItemDB.addPlayerItem(this.clairvoyantPlayerID, ITEM);
            }

            if (!this.clairvoyantPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.clairvoyantPlayer);
            }
            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening

            List<RoleActionInformation> informationList =
                    (new Clairvoyant()).getInformation(this.instance, this.clairvoyantPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");
            RoleActionInformation actionInformation = informationList.get(0);
            assertEquals(Action.CLAIRVOYANT_SEE_ROLE, actionInformation.action(),
                    "Action should be CLAIRVOYANT_SEE_ROLE.");
            assertEquals(GeneralPurposeHelper.getAllPlayersAsEligible(this.instance), actionInformation.eligible(),
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
            while (ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM) > 0) { // Making sure player has <1 seen
                ItemDB.deletePlayerItem(this.clairvoyantPlayerID, ITEM);
            }

            if (!this.clairvoyantPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.clairvoyantPlayer);
            }

            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening
            assertEquals(new ArrayList<>(), (new Clairvoyant()).getInformation(this.instance, this.clairvoyantPlayerID),
                    "When the player has less then 1 seen it should not get any information.");

            while (ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.clairvoyantPlayerID, ITEM);
            }

            this.instance.getInstanceState().setPhase(DayPhase.DAY);
            assertEquals(new ArrayList<>(), (new Clairvoyant()).getInformation(this.instance, this.clairvoyantPlayerID),
                    "When it is not evening the player should not get any information.");
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.instance.killPlayer(this.clairvoyantPlayer);
            assertEquals(new ArrayList<>(), (new Clairvoyant()).getInformation(this.instance, this.clairvoyantPlayerID),
                    "When the player is deceased it should not get any information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        Clairvoyant clairvoyant = new Clairvoyant();
        List<Action> actionList = clairvoyant.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.CLAIRVOYANT_SEE_ROLE);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest() {
        try {
            (new Clairvoyant()).replenishAction(0, this.clairvoyantPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM),
                    "Clairvoyant should have had 1 item after replenishing.");

            for (int i = 0; i < 5; i++) {
                ItemDB.addPlayerItem(this.clairvoyantPlayerID, ITEM);
            }
            assertEquals(6, ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM), "Clairvoyant should have 6 items.");
            (new Clairvoyant()).replenishAction(1, this.clairvoyantPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.clairvoyantPlayerID, ITEM),
                    "Clairvoyant should have had 1 item after replenishing, even with 5 items.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
