package org.lukos.model.rolesystem.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.DatabaseConnection;
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
import org.lukos.model.voting.Vote;
import org.lukos.model.voting.VoteType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link Mayor}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 06-04-2022
 */
public class MayorTest extends GameTest {

    private IInstance instance;
    private Player mayorPlayer;
    private PlayerIdentifier mayorPlayerID;
    private Player alivePlayer;

    @BeforeEach
    public void setUp() {
        try {
            User testUser = UserManager.getInstance().createUser(new IssuerSub("MayorTest", "Sub1"), "User1");
            this.instance = InstanceManager.getInstanceManager().getInstance(testUser.createGame("MayorTest", 1));
            this.mayorPlayer = testUser.getPlayer();

            User testUser2 = UserManager.getInstance().createUser(new IssuerSub("MayorTest", "Sub2"), "User2");
            testUser2.joinGame(this.instance.getIid());
            this.alivePlayer = testUser2.getPlayer();

            this.instance.startGame(testUser.getUid());
            this.instance.getInstanceState().setDay(1);
            this.instance.getInstanceState().setPhase(DayPhase.EVENING);

            MainRole alivePlayerRole = new Townsperson();
            this.alivePlayer.setMainRole(alivePlayerRole);
            this.mayorPlayer.setMainRole(new Werewolf());

            this.mayorPlayer.addJob(new Mayor());
            this.mayorPlayerID = this.mayorPlayer.getPlayerIdentifier();
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the constructor initializes the character and group correctly. */
    @Test
    public void constructorTest() {
        Mayor mayor = new Mayor();
        CharacterType type = CharacterType.NOT_SHADY;

        assertEquals(type, mayor.getCharacter(), String.format("Mayor should have %s character type.", type));
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the successor action. */
    @Test
    public void performSuccessorActionTest() {
        try {
            Mayor mayor = new Mayor();
            PreActionDT correctPreAction = new PreActionDT(this.mayorPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            SuccessorDB.removeSuccessor(this.instance.getIid(), SuccessorType.MAYOR);

            mayor.performAction(correctPreAction, Action.SUCCESSOR_MAYOR);

            assertEquals(this.alivePlayer.getPlayerIdentifier(), SuccessorDB.getSuccessor(this.instance.getIid(),
                    SuccessorType.MAYOR), "Successor should be assigned.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code performAction()} function performs the decide action. */
    @Test
    public void performDecideActionTest() {
        try {
            Mayor mayor = new Mayor();
            PreActionDT correctPreAction = new PreActionDT(this.mayorPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));
            createTieVote();

            mayor.performAction(correctPreAction, Action.MAYOR_DECIDE);

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnect()
                    .prepareStatement("SELECT * FROM Actions WHERE userID=?");
            preparedStatement.setInt(1, this.mayorPlayerID.userID());
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
            Mayor mayor = new Mayor();
            PreActionDT correctPreAction = new PreActionDT(this.mayorPlayerID, new ActionEnc(new ArrayList<>(),
                    Collections.singletonList(this.alivePlayer.getPlayerIdentifier())));

            Class<?> expected = NotAllowedToPerformActionException.class;
            try {
                mayor.performAction(correctPreAction, Action.MAYOR_DECIDE);
                fail("If there is no tie, the mayor shouldn't be able to decide.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            SuccessorDB.modifyOrCreateSuccessor(this.instance.getIid(), SuccessorType.MAYOR,
                    this.alivePlayer.getPlayerIdentifier());
            try {
                mayor.performAction(correctPreAction, Action.SUCCESSOR_MAYOR);
                fail("You cannot choose a successor without good data.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }

            expected = NoSuchActionException.class;
            try {
                mayor.performAction(correctPreAction, Action.HEAL);
                fail("Mayor can either decide or choose a successor nothing else.");
            } catch (Exception e) {
                assertTrue(expected.isInstance(e),
                        "type: " + e.getClass().getName() + " should have be instance of " + expected);
                assertNotNull(e.getMessage(), "Message should not be null");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code initializeActions()} function shows the right behaviour. */
    @Test
    public void initializeActionsTest() {
        try {
            (new Mayor()).initializeActions(new PlayerIdentifier(1, 1));
            assertTrue(true, "initializeActions() didn't thrown an error.");
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
            // Making sure the mayor has no successor
            SuccessorDB.removeSuccessor(this.instance.getIid(), SuccessorType.MAYOR);
            createTieVote();

            List<RoleActionInformation> informationList =
                    (new Mayor()).getInformation(this.instance, this.mayorPlayerID);
            assertEquals(2, informationList.size(), "Size of information list should be 2.");

            RoleActionInformation successorActionInfo = informationList.get(0);
            checkSuccessorInformation(successorActionInfo);

            RoleActionInformation decideActionInfo = informationList.get(1);
            checkDecideInformation(decideActionInfo);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code getInformation()} function returns the kill information when met with
     * the right requirements.
     */
    @Test
    public void getDecideInformationTest() {
        try {
            // Making sure the mayor has a successor
            SuccessorDB.modifyOrCreateSuccessor(this.instance.getIid(), SuccessorType.MAYOR,
                    this.alivePlayer.getPlayerIdentifier());
            createTieVote();

            List<RoleActionInformation> informationList =
                    (new Mayor()).getInformation(this.instance, this.mayorPlayerID);
            assertEquals(1, informationList.size(), "Size of information list should be 1.");

            RoleActionInformation decideActionInfo = informationList.get(0);
            checkDecideInformation(decideActionInfo);
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
            this.instance.getInstanceState().setPhase(DayPhase.DAY);
            List<RoleActionInformation> informationList =
                    (new Mayor()).getInformation(this.instance, this.mayorPlayerID);
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
            SuccessorDB.modifyOrCreateSuccessor(this.instance.getIid(), SuccessorType.MAYOR,
                    this.alivePlayer.getPlayerIdentifier());
            this.instance.getInstanceState().setPhase(DayPhase.EXECUTION); // Making sure it is evening
            assertEquals(new ArrayList<>(), (new Mayor()).getInformation(this.instance, this.mayorPlayerID),
                    "When the player has a successor and there is no tie vote it should not get any information.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Checks whether the kill action information is correct.
     *
     * @param killActionInfo the information that needs to be checked.
     * @throws SQLException when a database operation fails
     */
    private void checkDecideInformation(RoleActionInformation killActionInfo) throws SQLException {
        assertEquals(Action.MAYOR_DECIDE, killActionInfo.action(), "Action should be MAYOR_DECIDE.");
        assertEquals(GeneralPurposeHelper.getVoteTiedPlayersAsEligible(this.instance), killActionInfo.eligible(),
                "Eligible should be all players that have equal votes.");
        assertEquals(1, killActionInfo.numberOfVotes(), "Number of votes should be 1.");
    }

    /**
     * Checks whether the successor action information is correct.
     *
     * @param successorActionInfo the information  that needs to be checked.
     * @throws SQLException when a database operation fails
     */
    private void checkSuccessorInformation(RoleActionInformation successorActionInfo) throws SQLException {
        assertEquals(Action.SUCCESSOR_MAYOR, successorActionInfo.action(), "Action should be SUCCESSOR_MAYOR.");
        assertEquals(GeneralPurposeHelper.getAlivePlayersExceptSomeoneAsEligible(this.instance, this.mayorPlayerID),
                successorActionInfo.eligible(), "Eligible should be all players that have equal votes.");
        assertEquals(1, successorActionInfo.numberOfVotes(), "Number of votes should be 1.");
    }

    /**
     * This method creates a tie vote such that the mayor can have its decide action.
     *
     * @throws SQLException                 when a database operation fails
     * @throws GameException                when a game-logic operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    private void createTieVote() throws SQLException, GameException, ReflectiveOperationException {
        this.instance.getInstanceState().setDay(1);
        this.instance.getInstanceState().setPhase(DayPhase.DAY);
        this.instance.nextPhase();
        int voteID = -1;
        for (Vote vote : this.instance.getOngoingVotes()) {
            if (vote.getVoteType() == VoteType.LYNCH) {
                voteID = vote.getVid();
                break;
            }
        }
        assertTrue(voteID != -1, "Vote has not been initialized.");
        this.alivePlayer.vote(voteID, this.mayorPlayer);
        this.mayorPlayer.vote(voteID, this.alivePlayer);
        this.instance.nextPhase();
    }

    /** @utp.description Tests the {@code getActions()} functions returns the right {@link Action}s. */
    @Test
    public void getActionsTest() {
        Mayor mayor = new Mayor();
        List<Action> actionList = mayor.getActions();

        List<Action> expected = new ArrayList<>();
        expected.add(Action.SUCCESSOR_MAYOR);
        expected.add(Action.MAYOR_DECIDE);

        assertEquals(expected.size(), actionList.size(), "List should be of the same size.");
        assertTrue(expected.containsAll(actionList), "Actions should be expected.");
        assertTrue(actionList.containsAll(expected), "Actions should hold all expected.");
    }

    /** @utp.description Tests whether the {@code replenishAction()} function shows the right behaviour. */
    @Test
    public void replenishActionTest() {
        try {
            (new Mayor()).replenishAction(0, new PlayerIdentifier(1, 1));
            assertTrue(true, "replenishAction() didn't thrown an error.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
