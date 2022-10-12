package org.lukos.model.rolesystem.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.DatabaseConnection;
import org.lukos.database.ItemDB;
import org.lukos.database.SuccessorDB;
import org.lukos.model.GameTest;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.actionsystem.SuccessorType;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.actionsystem.NoSuchActionException;
import org.lukos.model.exceptions.actionsystem.NotAllowedToPerformActionException;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.rolesystem.MainRole;
import org.lukos.model.rolesystem.RoleActionInformation;
import org.lukos.model.rolesystem.roles.mainroles.Townsperson;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.rolesystem.util.GeneralPurposeHelper;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link AlphaWolf}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-04-2022
 */
public class AlphaWolfTest extends GameTest {

    private static final String ITEM = "kill";
    private IInstance instance;
    private PlayerIdentifier alphaWolfPlayerID;
    private Player alivePlayer;
    private Player aliveWolfPlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("AlphaWolfTest", "Sub1"), "User1");
            this.instance = InstanceManager.getInstanceManager().getInstance(testUser.createGame("AlphaWolfTest", 1));
            Player alphaWolfPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("AlphaWolfTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            User testUser3 = UserManager.getInstance().createUser(new IssuerSub("AlphaWolfTest", "Sub3"), "User3");
            testUser3.joinGame(this.instance.getIid());
            this.aliveWolfPlayer = testUser3.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(1);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            MainRole alivePlayerRole = new Townsperson();
            this.alivePlayer.setMainRole(alivePlayerRole);
            MainRole aliveWolfPlayerRole = new Werewolf();
            this.aliveWolfPlayer.setMainRole(aliveWolfPlayerRole);
            alphaWolfPlayer.setMainRole(new Werewolf());

            alphaWolfPlayer.addJob(new AlphaWolf());
            this.alphaWolfPlayerID = alphaWolfPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        AlphaWolf alphaWolf = new AlphaWolf();
        CharacterType type = CharacterType.SHADY;

        assertEquals(type, alphaWolf.getCharacter(), String.format("AlphaWolf should have %s character type.", type));
    }

    /** @utp.description Tests whether the {@code initializeActions} function gives the right amount of items. */
    @Test
    public void initializeActionsTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM);
            (new AlphaWolf()).initializeActions(this.alphaWolfPlayerID);
            assertEquals(amountOfItems + 1, ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM),
                    "The amount of items of AlphaWolf should have been increased by 1.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the successor action. */
    @Test
    public void performSuccessorActionTest() {
        try {
            AlphaWolf alphaWolf = new AlphaWolf();
            PreActionDT correctPreAction = new PreActionDT(this.alphaWolfPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.aliveWolfPlayer.getPlayerIdentifier())));
            SuccessorDB.removeSuccessor(this.instance.getIid(), SuccessorType.ALPHA_WOLF);

            alphaWolf.performAction(correctPreAction, Action.SUCCESSOR_ALPHA_WOLF);

            assertEquals(this.aliveWolfPlayer.getPlayerIdentifier(),
                    SuccessorDB.getSuccessor(this.instance.getIid(), SuccessorType.ALPHA_WOLF),
                    "Successor should be assigned.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the kill action. */
    @Test
    public void performKillActionTest() {
        try {
            AlphaWolf alphaWolf = new AlphaWolf();
            PreActionDT correctPreAction = new PreActionDT(this.alphaWolfPlayerID,
                    new ActionEnc(Collections.singletonList(this.alivePlayer.getHouse()), new ArrayList<>()));
            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.alphaWolfPlayerID, ITEM);
            }

            int amountOfItems = ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM);
            alphaWolf.performAction(correctPreAction, Action.ALPHA_WOLF_KILL);
            assertEquals(amountOfItems - 1, ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM),
                    "Item should have been reduced by 1");

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.alphaWolfPlayerID.userID());
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
            AlphaWolf alphaWolf = new AlphaWolf();
            PreActionDT correctPreAction = new PreActionDT(this.alphaWolfPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));

            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) > 0) {
                ItemDB.deletePlayerItem(this.alphaWolfPlayerID, ITEM);
            }

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                alphaWolf.performAction(correctPreAction, Action.ALPHA_WOLF_KILL);
                fail("Without kill items you should not be able to kill.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            ItemDB.addPlayerItem(this.alphaWolfPlayerID, ITEM);

            try {
                alphaWolf.performAction(correctPreAction, Action.HEAL);
                fail("AlphaWolf can either kill or choose a successor nothing else.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NotAllowedToPerformActionException.class;
            PreActionDT wrongPreAction =
                    new PreActionDT(this.alphaWolfPlayerID, new ActionEnc(new ArrayList<>(), new ArrayList<>()));

            try {
                alphaWolf.performAction(wrongPreAction, Action.ALPHA_WOLF_KILL);
                fail("You cannot kill without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            wrongPreAction = new PreActionDT(this.alphaWolfPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));

            try {
                alphaWolf.performAction(wrongPreAction, Action.ALPHA_WOLF_KILL);
                fail("You cannot kill without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) > 1) {
                ItemDB.deletePlayerItem(this.alphaWolfPlayerID, ITEM);
            }
            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.alphaWolfPlayerID, ITEM);
            }
            assertEquals(1, ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM), "AlphaWolf should have 1 kill item.");

            List<Integer> locations = new ArrayList<>();
            locations.add(this.alivePlayer.getHouse());
            locations.add(this.aliveWolfPlayer.getHouse());
            wrongPreAction = new PreActionDT(this.alphaWolfPlayerID, new ActionEnc(locations, new ArrayList<>()));

            try {
                alphaWolf.performAction(wrongPreAction, Action.ALPHA_WOLF_KILL);
                fail("You cannot kill without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            SuccessorDB.modifyOrCreateSuccessor(this.instance.getIid(), SuccessorType.ALPHA_WOLF,
                    this.aliveWolfPlayer.getPlayerIdentifier());
            try {
                alphaWolf.performAction(correctPreAction, Action.SUCCESSOR_ALPHA_WOLF);
                fail("You cannot choose a successor without good data.");
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
     * @utp.description Tests whether the {@code getInformation()} function returns all information when met with the
     * right requirements.
     */
    @Test
    public void getAllInformationTest() {
        try {
            // Making sure the alphawolf has no successor
            SuccessorDB.removeSuccessor(this.instance.getIid(), SuccessorType.ALPHA_WOLF);
            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) < 1) { // making sure we have >0 kills
                ItemDB.addPlayerItem(this.alphaWolfPlayerID, ITEM);
            }
            int amountOfItems = ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening

            List<RoleActionInformation> informationList =
                    (new AlphaWolf()).getInformation(this.instance, this.alphaWolfPlayerID);
            assertEquals(2, informationList.size(), "Size of information list should be 2.");

            RoleActionInformation killActionInfo = informationList.get(0);
            checkKillInformation(killActionInfo, amountOfItems);

            RoleActionInformation successorActionInfo = informationList.get(1);
            checkSuccessorInformation(successorActionInfo);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation()} function returns the kill information when met with
     * the right requirements.
     */
    @Test
    public void getKillInformationTest() {
        try {
            // Making sure the alphawolf has q successor
            SuccessorDB.modifyOrCreateSuccessor(this.instance.getIid(), SuccessorType.ALPHA_WOLF,
                    this.aliveWolfPlayer.getPlayerIdentifier());
            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) < 1) { // making sure we have >0 kills
                ItemDB.addPlayerItem(this.alphaWolfPlayerID, ITEM);
            }
            int amountOfItems = ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING); // Making sure it is evening

            List<RoleActionInformation> informationList =
                    (new AlphaWolf()).getInformation(this.instance, this.alphaWolfPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");

            RoleActionInformation killActionInfo = informationList.get(0);
            checkKillInformation(killActionInfo, amountOfItems);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation()} function returns the successor information when met
     * with the right requirements.
     */
    @Test
    public void getSuccessorInformationTest() {
        try {
            // Making sure the alphawolf has no successor
            SuccessorDB.removeSuccessor(this.instance.getIid(), SuccessorType.ALPHA_WOLF);
            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) > 0) { // making sure we have <0 kills
                ItemDB.deletePlayerItem(this.alphaWolfPlayerID, ITEM);
            }

            List<RoleActionInformation> informationList =
                    (new AlphaWolf()).getInformation(this.instance, this.alphaWolfPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");

            RoleActionInformation successorActionInfo = informationList.get(0);
            checkSuccessorInformation(successorActionInfo);
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
            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) < 1) {
                ItemDB.addPlayerItem(this.alphaWolfPlayerID, ITEM);
            }

            SuccessorDB.modifyOrCreateSuccessor(this.instance.getIid(), SuccessorType.ALPHA_WOLF,
                    this.aliveWolfPlayer.getPlayerIdentifier());
            this.instance.getInstanceState().setPhase(DayPhase.DAY); // Making sure it is evening
            assertEquals(new ArrayList<>(), (new AlphaWolf()).getInformation(this.instance, this.alphaWolfPlayerID),
                    "When the player has a successor and it is not evening it should not get any information.");

            while (ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM) > 0) { // Making sure player has <1 kills
                ItemDB.deletePlayerItem(this.alphaWolfPlayerID, ITEM);
            }
            assertEquals(new ArrayList<>(), (new AlphaWolf()).getInformation(this.instance, this.alphaWolfPlayerID),
                    "When the player has a successor and 0 kills and it is not evening it should not get any " +
                            "information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Checks whether the kill action information is correct.
     *
     * @param killActionInfo    the information that needs to be checked.
     * @param amountOfKillItems the amount of kill actions
     * @throws ReflectiveOperationException when a reflective operation fails
     * @throws SQLException                 when a database operation fails
     * @throws GameException                when a game-logic operation fails
     */
    private void checkKillInformation(RoleActionInformation killActionInfo, int amountOfKillItems)
            throws ReflectiveOperationException, SQLException, GameException {
        assertEquals(Action.ALPHA_WOLF_KILL, killActionInfo.action(), "Action should be ALPHA_WOLF_KILL.");
        assertEquals(GeneralPurposeHelper.getLocationOfNonWolvesAsEligible(this.instance), killActionInfo.eligible(),
                "Eligible should be all non wolves locations.");
        assertEquals(amountOfKillItems, killActionInfo.numberOfVotes(), "Number of votes should be amountOfKillItems.");
    }

    /**
     * Checks whether the successor action information is correct.
     *
     * @param successorActionInfo the information  that needs to be checked.
     * @throws ReflectiveOperationException when a reflective operation fails
     * @throws SQLException                 when a database operation fails
     * @throws GameException                when a game-logic operation fails
     */
    private void checkSuccessorInformation(RoleActionInformation successorActionInfo)
            throws ReflectiveOperationException, SQLException, GameException {
        assertEquals(Action.SUCCESSOR_ALPHA_WOLF, successorActionInfo.action(),
                "Action should be SUCCESSOR_ALPHA_WOLF.");
        assertEquals(GeneralPurposeHelper.getAliveWolvesExceptSomeoneAsEligible(this.instance, this.alphaWolfPlayerID),
                successorActionInfo.eligible(), "Eligible should be all non wolves locations.");
        assertEquals(1, successorActionInfo.numberOfVotes(), "Number of votes should be 1.");
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        AlphaWolf alphaWolf = new AlphaWolf();
        List<Action> actionList = alphaWolf.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.SUCCESSOR_ALPHA_WOLF);
        expected.add(Action.ALPHA_WOLF_KILL);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function gives the right amount of items. */
    @Test
    public void replenishActionTest() {
        try {
            int amountOfItems = ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM);
            while (amountOfItems < 1) {
                ItemDB.addPlayerItem(this.alphaWolfPlayerID, ITEM);
                amountOfItems = ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM);
            }
            while (amountOfItems > 1) {
                ItemDB.deletePlayerItem(this.alphaWolfPlayerID, ITEM);
                amountOfItems = ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM);
            }

            assertEquals(1, ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM), "Player should only have 1 kill item.");
            (new AlphaWolf()).replenishAction(0, this.alphaWolfPlayerID);
            assertEquals(1, ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM),
                    "Player should still have only 1 kill item.");

            (new AlphaWolf()).replenishAction(2, this.alphaWolfPlayerID);
            assertEquals(3, ItemDB.amountOfItems(this.alphaWolfPlayerID, ITEM), "Player should have 3 kill items.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
