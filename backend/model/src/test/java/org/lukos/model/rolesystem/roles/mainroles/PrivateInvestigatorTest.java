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
 * Test cases for {@link PrivateInvestigator}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 14-04-2022
 */
public class PrivateInvestigatorTest extends GameTest {

    private static final String ITEM = "investigation";
    private IInstance instance;
    private Player privateInvestigatorPlayer;
    private PlayerIdentifier privateInvestigatorPlayerID;
    private Player alivePlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser =
                    UserManager.getInstance().createUser(new IssuerSub("PrivateInvestigatorTest", "Sub1"), "User1");
            this.instance =
                    InstanceManager.getInstanceManager().getInstance(testUser.createGame("PrivateInvestigatorTest", 1));
            this.privateInvestigatorPlayer = testUser.getPlayer();

            User testUser2 =
                    UserManager.getInstance().createUser(new IssuerSub("PrivateInvestigatorTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(1);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.alivePlayer.setMainRole(new Werewolf());

            this.privateInvestigatorPlayer.setMainRole(new PrivateInvestigator());
            this.privateInvestigatorPlayerID = this.privateInvestigatorPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        PrivateInvestigator privateInvestigator = new PrivateInvestigator();
        CharacterType type = CharacterType.NOT_SHADY;
        Group group = Group.TOWNSPEOPLE;

        assertEquals(type, privateInvestigator.getCharacter(),
                String.format("PrivateInvestigator should have %s character type.", type));
        assertEquals(group, privateInvestigator.getGroup(),
                String.format("PrivateInvestigator should have %s group.", group));
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionsTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM);
            (new PrivateInvestigator()).initializeActions(this.privateInvestigatorPlayerID);
            assertEquals(amountOfItems, ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM),
                    "Amount of items of PrivateInvestigator shouldn't have changed.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the right action. */
    @Test
    public void performActionTest() {
        try {
            PrivateInvestigator privateInvestigator = new PrivateInvestigator();
            PreActionDT correctPreAction = new PreActionDT(this.privateInvestigatorPlayerID,
                    new ActionEnc(new ArrayList<>(),
                            Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            while (ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.privateInvestigatorPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM);
            privateInvestigator.performAction(correctPreAction, Action.PRIVATE_INVESTIGATE);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.privateInvestigatorPlayerID.userID());
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
            PrivateInvestigator privateInvestigator = new PrivateInvestigator();
            PreActionDT correctPreAction = new PreActionDT(this.privateInvestigatorPlayerID,
                    new ActionEnc(new ArrayList<>(),
                            Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));

            while (ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.privateInvestigatorPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                privateInvestigator.performAction(correctPreAction, Action.PRIVATE_INVESTIGATE);
                fail("Without investigations you should not be able to investigate.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.privateInvestigatorPlayerID, ITEM);

            try {
                privateInvestigator.performAction(correctPreAction, Action.HEAL);
                fail("PrivateInvestigator can only investigate.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NotAllowedToPerformActionException.class;
            this.instance.killPlayer(this.privateInvestigatorPlayer);

            try {
                privateInvestigator.performAction(correctPreAction, Action.PRIVATE_INVESTIGATE);
                fail("You cannot investigate when you are not alive.");
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
            // making sure we have >0 investigation
            while (ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.privateInvestigatorPlayerID, ITEM);
            }

            if (!this.privateInvestigatorPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.privateInvestigatorPlayer);
            }
            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening

            List<RoleActionInformation> informationList =
                    (new PrivateInvestigator()).getInformation(this.instance, this.privateInvestigatorPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");
            RoleActionInformation actionInformation = informationList.get(0);
            assertEquals(Action.PRIVATE_INVESTIGATE, actionInformation.action(),
                    "Action should be PRIVATE_INVESTIGATE.");
            assertEquals(GeneralPurposeHelper.getAllPlayersAsEligible(this.instance), actionInformation.eligible(),
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
            // Making sure player has <1 investigation
            while (ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.privateInvestigatorPlayerID, ITEM);
            }

            if (!this.privateInvestigatorPlayer.alive()) { // Making sure player is alive
                this.instance.revivePlayer(this.privateInvestigatorPlayer);
            }

            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening
            assertEquals(new ArrayList<>(),
                    (new PrivateInvestigator()).getInformation(this.instance, this.privateInvestigatorPlayerID),
                    "When the player has less then 1 investigation it should not get any information.");

            while (ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.privateInvestigatorPlayerID, ITEM);
            }

            this.instance.getInstanceState().setPhase(DayPhase.DAY);
            assertEquals(new ArrayList<>(),
                    (new PrivateInvestigator()).getInformation(this.instance, this.privateInvestigatorPlayerID),
                    "When it is not evening the player should not get any information.");
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            this.instance.killPlayer(this.privateInvestigatorPlayer);
            assertEquals(new ArrayList<>(),
                    (new PrivateInvestigator()).getInformation(this.instance, this.privateInvestigatorPlayerID),
                    "When the player is deceased it should not get any information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        PrivateInvestigator privateInvestigator = new PrivateInvestigator();
        List<Action> actionList = privateInvestigator.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.PRIVATE_INVESTIGATE);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest1() {
        try {
            (new PrivateInvestigator()).replenishAction(0, this.privateInvestigatorPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM),
                    "PrivateInvestigator should have had 1 item after replenishing.");

            for (int i = 0; i < 5; i++) {
                ItemDB.addPlayerItem(this.privateInvestigatorPlayerID, ITEM);
            }
            assertEquals(6, ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM),
                    "PrivateInvestigator should have 6 items.");
            (new PrivateInvestigator()).replenishAction(1, this.privateInvestigatorPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.privateInvestigatorPlayerID, ITEM),
                    "PrivateInvestigator should have had 1 item after replenishing, even with 5 items.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest2() {
        try {
            User piUser =
                    UserManager.getInstance().createUser(new IssuerSub("PrivateInvestigatorTest", "Sub69"), "User69");
            int bigInstanceID = piUser.createGame("ReplenishActionTest", 1);
            for (int i = 4; i < 30; i++) {
                User randomUser = UserManager.getInstance()
                        .createUser(new IssuerSub("PrivateInvestigatorTest", "Sub" + i), "User" + i);
                randomUser.joinGame(bigInstanceID);
            }
            IInstance bigInstance = InstanceManager.getInstanceManager().getInstance(bigInstanceID);
            bigInstance.startGame(piUser.getUid());
            piUser.getPlayer().setMainRole(new PrivateInvestigator());
            PlayerIdentifier piPlayerID = piUser.getPlayer().getPlayerIdentifier();

            (new PrivateInvestigator()).replenishAction(0, piPlayerID);
            assertEquals(2, ItemDB.amountOfItems(piPlayerID, ITEM),
                    "PrivateInvestigator should have had 2 item after replenishing.");

            for (int i = 0; i < 5; i++) {
                ItemDB.addPlayerItem(piPlayerID, ITEM);
            }
            assertEquals(7, ItemDB.amountOfItems(piPlayerID, ITEM), "PrivateInvestigator should have 7 items.");
            (new PrivateInvestigator()).replenishAction(1, piPlayerID);
            assertEquals(2, ItemDB.amountOfItems(piPlayerID, ITEM),
                    "PrivateInvestigator should have had 2 item after replenishing, even with 7 items.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
