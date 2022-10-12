package org.lukos.model.actionsystem;

import jdk.jfr.Experimental;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.*;
import org.lukos.model.GameTest;
import org.lukos.model.actionsystem.actions.*;
import org.lukos.model.exceptions.user.NoSuchRoleException;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.rolesystem.roles.mainroles.Graverobber;
import org.lukos.model.rolesystem.roles.mainroles.GuardianAngel;
import org.lukos.model.rolesystem.roles.mainroles.Poisoner;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.player.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test cases for {@link ActionManager}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 23-03-2022
 */
public class ActionManagerTest extends GameTest {

    /** Instance id */
    private int instanceId;

    /** First player */
    private Player player;

    /** Second player */
    private Player secondPlayer;

    /** Third player */
    private Player thirdPlayer;


    @BeforeEach
    public void beforeTests()  {
        try {
            /// Get the instance manager
            InstanceManager im = InstanceManager.getInstanceManager();

            // Create 3 users
            int gameMasterId = UserDB.createUser("is1", "sub1", "User1");
            int user2Id = UserDB.createUser("is2", "sub2", "User2");
            int user3Id = UserDB.createUser("is3", "sub3", "User3");

            // Create a new game and get the player
            instanceId = (new User(gameMasterId)).createGame("PlayerTestGame", 1);
            IInstance instance = im.getInstance(instanceId);
            player = (new User(gameMasterId)).getPlayer();

            // Test that player was initialized correctly
            assertEquals(instanceId, player.getPlayerIdentifier().instanceID(), "instanceId mismatch!");
            assertEquals(gameMasterId, player.getPlayerIdentifier().userID(), "uid should be the id of the 'gamemaster'!");
            assertNotNull(player.getDoubleRoles(), "DoubleRoles should not be null!");
            assertEquals(new ArrayList<DoubleRole>(), player.getDoubleRoles(), "DoubleRoles should be empty!");
            assertNotNull(player.getDeathnote(), "Deathnote should not be null!");
            assertNull(player.getDeathnote().getContent(), "Deathnote should be empty at creation!");
            assertTrue(player.getDeathnote().getChangeable(), "Deathnote should be changeable at creation!");
            assertNotNull(player.getJobs(), "Jobs should not be null!");
            assertEquals(new ArrayList<Job>(), player.getJobs(), "Jobs should be empty!");
            try {
                player.getMainRole();
            } catch (NoSuchRoleException e) {
                assertNotNull(e.getMessage(), "Exception message should not be null!");
            } catch (Exception e) {
                fail("NoSuchRoleException should have been thrown. Instead the following exception was thrown: " + e);
            }

            // Create enough players and tart the game
            // Second player joins the game
            (new User(user2Id)).joinGame(instanceId);
            secondPlayer = (new User(user2Id)).getPlayer();

            // Create third player
            (new User(user3Id)).joinGame(instanceId);
            thirdPlayer = (new User(user3Id)).getPlayer();

            // Add 9 more dummy players to reach minimum number of player
            int userId;
            for (int i = 0; i < 9; i++) {
                userId = UserDB.createUser("is" + (i + 4), "sub" + (i + 4), "User" + (i + 4));
                (new User(userId)).joinGame(instanceId);
            }

            // Start the game
            instance.startGame(gameMasterId);

            // Make sure its day phase on day 1
            instance.getInstanceState().setDay(1);
            instance.getInstanceState().setPhase(DayPhase.EXECUTION);
            instance.nextPhase();

            // Make player a grave robber and secondPlayer a werewolf
            player.setMainRole(new Graverobber());
            player.getMainRole().initializeActions(player.getPlayerIdentifier());
            secondPlayer.setMainRole(new Werewolf());

            // kill second player
            instance.killPlayer(secondPlayer);

            // Test that the operations were successful and checking precondition that some tests assume to be true
            assertEquals(Graverobber.class, player.getMainRole().getClass(), "The player should be a grave robber!");
            assertEquals(Werewolf.class, secondPlayer.getMainRole().getClass(), "The secondPlayer should be a werewolf!");
            assertFalse(secondPlayer.alive(), "secondPayer should be dead!");
            assertTrue(PlayerDB.getPlayersAtLocation(player.getHouse()).contains(player.getPlayerIdentifier()), "The actions should not have been executed!"); // Player should still be at their home
            assertEquals(0, ActionManager.actionsInBuffer(instanceId), "Items in buffer should be zero at the start!");

        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * Create an {@code ActionDT} for an {@code action} that will be performed by {@code player} and with {@code secondPlayer as target}.
     *
     * @param action the action to be performed.
     * @return the created {@code ActionDT}
     */
    public ActionDT createActionDT(Action action) {
        return createActionDT(action, new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(secondPlayer.getPlayerIdentifier()))));
    }

    /**
     * Create an {@code ActionDT} for an {@code action} that will be performed by {@code player}.
     *
     * @param action the action to be performed.
     * @param actionEnc the target
     * @return the created {@code ActionDT}
     */
    public ActionDT createActionDT(Action action, ActionEnc actionEnc) {
        return createActionDT(action, actionEnc, Instant.now());
    }

    /**
     * Create an {@code ActionDT} for an {@code action} that will be performed by {@code player}.
     *
     * @param action the action to be performed.
     * @param actionEnc the target
     * @param time the time this object was received
     * @return the created {@code ActionDT}
     */
    public ActionDT createActionDT(Action action, ActionEnc actionEnc, Instant time) {
        return new ActionDT(time, action, new PreActionDT(player.getPlayerIdentifier(), actionEnc));
    }

    /**
     * simple constructor test, this constructor should not be used.
     *
     * @utp.description Test whether the {@code ActionManager} is initialized correctly.
     */
    @Test
    public void constructorTest() {
        try {
            new ActionManager();
        } catch (Exception e) {
            fail("An exception was thrown during the construction of the ActionManager: " + e);
        }
    }

    //region Clear

    /**
     * clear() test, empty actions buffer.
     *
     * @utp.description Test whether the action buffer is empty after clearing, when the action buffer was empty before.
     */
    @Test
    public void clearEmptyTest() {
        try {
            // Clear the buffer
            ActionManager.clear(instanceId);

            // Test
            assertEquals(0, ActionManager.actionsInBuffer(instanceId), "The buffer should be empty after clearing!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * clear() test, one action in the buffer.
     *
     * @utp.description Test whether the action buffer is empty after clearing, when the action buffer contained one action before.
     */
    @Test
    public void clearSingleTest() {
        try {
            // Create ActionManager with empty buffer
            ActionDT action = createActionDT(new ChangeRole());

            // Add an action to the buffer
            ActionManager.addAction(action);

            // Clear the buffer
            ActionManager.clear(instanceId);

            // Test
            assertEquals(0, ActionManager.actionsInBuffer(instanceId), "The buffer should be empty after clearing!");
            assertEquals(Graverobber.class, player.getMainRole().getClass(), "The action should not have been executed!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * clear() test, populated actions buffer.
     *
     * @utp.description Test whether the action buffer is empty after clearing, when the action buffer was populated before.
     */
    @Test
    public void clearPopulatedTest() {
        try {
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(Collections.singleton(secondPlayer.getHouse())), new ArrayList<>());
            for (int i = 0 ; i < 10; i++) {
                ActionManager.addAction(createActionDT(new MoveToLocation(), actionEnc));
            }

            // Clear the buffer
            ActionManager.clear(instanceId);

            // Test
            assertEquals(0, ActionManager.actionsInBuffer(instanceId), "The buffer should be empty after clearing!");
            assertTrue(PlayerDB.getPlayersAtLocation(player.getHouse()).contains(player.getPlayerIdentifier()), "The actions should not have been executed!"); // Player should still be at their home
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    //endregion
    //region performActions

    /**
     * performAction() test, with empty buffer, no actions should be performed.
     *
     * @utp.description Test whether no actions are performed when the buffer is empty.
     */
    @Test
    public void performActionsEmptyTest() {
        try {
            // Perform the actions
            ActionManager.performActions(instanceId);

            // All precondition should still hold as nothing should have changed
            assertEquals(0, ActionManager.actionsInBuffer(instanceId), "The buffer should be empty after performing the actions!");
            assertEquals(Graverobber.class, player.getMainRole().getClass(), "The player should be a grave robber!");
            assertEquals(Werewolf.class, secondPlayer.getMainRole().getClass(), "The secondPlayer should be a werewolf!");
            assertFalse(secondPlayer.alive(), "secondPayer should be dead!");
            assertTrue(PlayerDB.getPlayersAtLocation(player.getHouse()).contains(player.getPlayerIdentifier()), "The actions should not have been executed!"); // Player should still be at their home
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * performAction() test, with one action in the buffer, one action should be performed.
     *
     * @utp.description Test whether one action is performed when the buffer contains one action.
     */
    @Test
    public void performActionsSingleTest() {
        try {
            // Create ActionManager with empty buffer
            ActionDT action = createActionDT(new ChangeRole());

            // Add an action to the buffer
            ActionManager.addAction(action);

            // Perform the actions
            ActionManager.performActions(instanceId);

            // Test
            assertEquals(0, ActionManager.actionsInBuffer(instanceId), "The buffer should be empty after clearing!");
            assertEquals(Werewolf.class, player.getMainRole().getClass(), "The action should have been executed! The player should have been a werewolf!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * performAction() test, with populated buffer, no actions should be performed.
     *
     * @utp.description Test whether all actions, and no more, are performed when the buffer is populated.
     */
    @Test
    public void performActionsPopulatedTest() {
        try {
            // Add multiple actions to the buffer
            // player moves to secondPlayers house
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(Collections.singleton(secondPlayer.getHouse())), new ArrayList<>());
            ActionManager.addAction(createActionDT(new MoveToLocation(), actionEnc));

            // player robs secondPlayers grave
            ActionManager.addAction(createActionDT(new ChangeRole()));

            // thirdPlayer moves to players home
            ActionEnc actionEnc2 = new ActionEnc(new ArrayList<>(Collections.singleton(player.getHouse())), new ArrayList<>());
            PreActionDT preActionDT = new PreActionDT(thirdPlayer.getPlayerIdentifier(), actionEnc2);
            ActionManager.addAction(new ActionDT(Instant.now(), new MoveToLocation(),preActionDT));

            // Perform the actions
            ActionManager.performActions(instanceId);

            // Test all actions have been executed correctly
            assertEquals(0, ActionManager.actionsInBuffer(instanceId), "The buffer should be empty after performing the actions!");
            assertEquals(Werewolf.class, player.getMainRole().getClass(), "The action should have been executed! The player should have been a werewolf!");
            assertTrue(PlayerDB.getPlayersAtLocation(secondPlayer.getHouse()).contains(player.getPlayerIdentifier()), "The actions should not have been executed!"); // Player should be at secondPlayer's house
            assertTrue(PlayerDB.getPlayersAtLocation(player.getHouse()).contains(thirdPlayer.getPlayerIdentifier()), "The actions should not have been executed!"); // thirdPlayer should be at player's house
            assertFalse(secondPlayer.alive(), "secondPayer should still be dead!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * performActions() test, add different actions and check they were executed in the correct order.
     * In this test we first protect a player and then try to kill them, the player will live.
     *
     *
     * @utp.description Test whether actions are executed in the correct order.
     */
    @Test
    public void performActionOrderTest1() {
        try {
            // Make player a guardianAngel
            player.setMainRole(new GuardianAngel());
            player.getMainRole().initializeActions(player.getPlayerIdentifier());

            // Make thirdPlayer a poisoner
            thirdPlayer.setMainRole(new Poisoner());
            thirdPlayer.getMainRole().initializeActions(thirdPlayer.getPlayerIdentifier());

            // Check that the roles are set correctly
            assertEquals(GuardianAngel.class, player.getMainRole().getClass(), "player should have be a guardianAngel!");
            assertEquals(Poisoner.class, thirdPlayer.getMainRole().getClass(), "thirdPlayer should have be a poisoner!");

            // add protection to player
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(player.getPlayerIdentifier())));
            ActionManager.addAction(createActionDT(new ProtectPlayers(), actionEnc));
            // add kill player
            ActionEnc actionEnc2 = new ActionEnc(new ArrayList<>(Collections.singleton(player.getHouse())), new ArrayList<>());
            PreActionDT preActionDT = new PreActionDT(thirdPlayer.getPlayerIdentifier(), actionEnc2);
            ActionManager.addAction(new ActionDT(Instant.now().plusSeconds(50), new KillPlayers(), preActionDT));
            List<Integer> actionIDs = ActionsDB.getActions(instanceId, "NOT_EXECUTED");
            for (int id: actionIDs) {
                ActionDT action = ActionsDB.getActionFromID(id);
                System.out.println("[ActionManagerTest-performActionOrderTest1] Action:" + action.action().getName() + ", Seconds:" + action.time().getEpochSecond() + ", Nanos:" + action.time().getNano());
            }

            // perform actions
            ActionManager.performActions(instanceId);

            // test, player should not be on the execution list
            List<PlayerIdentifier> toBeExecuted = InstanceDB.getToBeExecuted(instanceId);
            assertTrue(player.alive(), "Player should be alive!");
            assertTrue(thirdPlayer.alive(), "thirdPlayer should be alive!");
            assertEquals(0, toBeExecuted.size(), "To be executed should be empty!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * performActions() test, add different actions and check they were executed in the correct order.
     * In this test we first kill a player and then protect them, the player will die.
     *
     * @utp.description Test whether actions are executed in the correct order.
     */
    @Test
    public void performActionOrderTest2() {
        try {
            // Make player a guardianAngel
            player.setMainRole(new GuardianAngel());
            player.getMainRole().initializeActions(player.getPlayerIdentifier());

            // Make thirdPlayer a poisoner
            thirdPlayer.setMainRole(new Poisoner());
            thirdPlayer.getMainRole().initializeActions(thirdPlayer.getPlayerIdentifier());

            // Check that the roles are set correctly
            assertEquals(GuardianAngel.class, player.getMainRole().getClass(), "player should have be a guardianAngel!");
            assertEquals(Poisoner.class, thirdPlayer.getMainRole().getClass(), "thirdPlayer should have be a poisoner!");

            // add kill player
            ActionEnc actionEnc2 = new ActionEnc(new ArrayList<>(Collections.singleton(player.getHouse())), new ArrayList<>());
            PreActionDT preActionDT = new PreActionDT(thirdPlayer.getPlayerIdentifier(), actionEnc2);
            ActionManager.addAction(new ActionDT(Instant.now().minusSeconds(50), new KillPlayers(), preActionDT));

            // add protection to player
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(player.getPlayerIdentifier())));
            ActionManager.addAction(createActionDT(new ProtectPlayers(), actionEnc));
            List<Integer> actionIDs = ActionsDB.getActions(instanceId, "NOT_EXECUTED");
            for (int id: actionIDs) {
                ActionDT action = ActionsDB.getActionFromID(id);
                System.out.println("[ActionManagerTest-performActionOrderTest2] Action:" + action.action().getName() + ", Seconds:" + action.time().getEpochSecond() + ", Nanos:" + action.time().getNano());
            }

            // perform actions
            ActionManager.performActions(instanceId);

            // test, player should not be on the execution list
            List<PlayerIdentifier> toBeExecuted = InstanceDB.getToBeExecuted(instanceId);
            assertTrue(player.alive(), "Player should be alive!");
            assertTrue(thirdPlayer.alive(), "thirdPlayer should be alive!");
            assertEquals(1, toBeExecuted.size(), "ToBeExecuted should contain exactly one player!");
            assertEquals(player.getPlayerIdentifier(), toBeExecuted.get(0), "Player should be on the list!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * performActions() test, create {@code Action} first and add it to the {@code ActionManager} at the end.
     * Then check that this action is executed first. This checks whether the sorting is done correctly.
     *
     * @utp.description Test whether an action that is executed at the correct time when it is not in the correct position in the list.
     */
    @Test
    public void performActionSortTest() {
        try {
            // Make player a guardianAngel
            player.setMainRole(new GuardianAngel());
            player.getMainRole().initializeActions(player.getPlayerIdentifier());

            // Make thirdPlayer a poisoner
            thirdPlayer.setMainRole(new Poisoner());
            thirdPlayer.getMainRole().initializeActions(thirdPlayer.getPlayerIdentifier());

            // Check that the roles are set correctly
            assertEquals(GuardianAngel.class, player.getMainRole().getClass(), "player should have be a guardianAngel!");
            assertEquals(Poisoner.class, thirdPlayer.getMainRole().getClass(), "thirdPlayer should have be a poisoner!");

            // create kill player action, but do NOT add it to the ActionManager
            ActionEnc actionEnc2 = new ActionEnc(new ArrayList<>(Collections.singleton(player.getHouse())), new ArrayList<>());
            PreActionDT preActionDT = new PreActionDT(thirdPlayer.getPlayerIdentifier(), actionEnc2);
            ActionDT actionDT = new ActionDT(Instant.now().minusSeconds(10), new KillPlayers(), preActionDT);

            // add protection to player
            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), new ArrayList<>(Collections.singleton(player.getPlayerIdentifier())));
            ActionManager.addAction(createActionDT(new ProtectPlayers(), actionEnc));

            // add kill action to ActionManager
            ActionManager.addAction(actionDT);

            // perform actions
            ActionManager.performActions(instanceId);

            // test, player should not be on the execution list
            List<PlayerIdentifier> toBeExecuted = InstanceDB.getToBeExecuted(instanceId);
            assertTrue(player.alive(), "Player should be alive!");
            assertTrue(thirdPlayer.alive(), "thirdPlayer should be alive!");
            assertEquals(1, toBeExecuted.size(), "ToBeExecuted should contain exactly one player!");
            assertEquals(player.getPlayerIdentifier(), toBeExecuted.get(0), "Player should be on the list!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    //endregion
    //region addAction

    /**
     * addAction(ActionDT) test, with empty actions buffer.
     *
     * @utp.description Test whether an action is correctly added when the buffer was empty.
     */
    @Test
    public void addActionEmptyTest() {
        try {
            // Create an action
            ActionDT action = createActionDT(new ChangeRole());

            // Add the action to the buffer
            ActionManager.addAction(action);

            // Test
            assertEquals(1, ActionManager.actionsInBuffer(instanceId), "The buffer should be empty after clearing!");
            assertTrue(ActionsDB.getActionFromID(ActionsDB.getNotExecutedActions(instanceId).get(0)).action() instanceof ChangeRole, "The action in the buffer is not of type ChangeRole!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * addAction(ActionDT) test, add multiple actions.
     *
     * @utp.description Test whether an action is correctly added when the buffer was populated already.
     */
    @Test
    public void addActionPopulatedTest() {
        try {
            // Create three actions
            ActionDT action1 = createActionDT(new ChangeRole());
            ActionDT action2 = createActionDT(new ProtectPlayers());
            ActionDT action3 = createActionDT(new RevivePlayers());

            // Add the actions to the buffer
            ActionManager.addAction(action1);
            ActionManager.addAction(action2);
            ActionManager.addAction(action3);

            // Test
            assertEquals(3, ActionManager.actionsInBuffer(instanceId), "Three actions should be in the buffer!");

            // Test all actions were added correctly
            boolean foundChangeRole = false;
            boolean foundProtectPlayers = false;
            boolean foundRevivePlayers = false;
            for (int act : ActionsDB.getNotExecutedActions(instanceId)) {
                Action action = ActionsDB.getActionFromID(act).action();
                // if changeRole has not been found and action is a ChangeRole action, set found to true
                if (!foundChangeRole && action instanceof ChangeRole) {
                    foundChangeRole = true;
                } else if (!foundProtectPlayers && action instanceof ProtectPlayers) {
                    foundProtectPlayers = true;
                } else if (!foundRevivePlayers && action instanceof RevivePlayers) {
                    foundRevivePlayers = true;
                }
            }
            assertTrue(foundChangeRole, "ChangeRole was not added correctly!");
            assertTrue(foundProtectPlayers, "ProtectPlayers was not added correctly!");
            assertTrue(foundRevivePlayers, "RevivePlayers was not added correctly!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * addAction test, add compAction without children, only compAction should be in the buffer!.
     *
     * @utp.description Test whether one {@code Action} is added to the {@code ActionManager} when an empty {@code CompAction} is added.
     */
    @Test
    public void addActionEmptyCompActionTest() {
        try {
            // Create compAction
            CompAction compAction = new CompAction("test");

            // Add to the ActionManager
            ActionManager.addAction(createActionDT(compAction));

            // Test nothing was added to the buffer
            assertEquals(1, ActionManager.actionsInBuffer(instanceId), "Only compAction should be in the buffer!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * addAction test, add compAction with children, all children should be added individually.
     *
     * @utp.description Test whether all children are added to the {@code ActionManager} when an populated {@code CompAction} is added.
     */
    @Test
    public void addActionPopulatedCompActionTest() {
        try {
            // Create three actions
            ActionDT action1 = createActionDT(new ChangeRole());
            ActionDT action2 = createActionDT(new ProtectPlayers());
            ActionDT action3 = createActionDT(new RevivePlayers());

            // Add the actions to the compAction
            CompAction compAction = new CompAction("Test2");
            compAction.add(action1);
            compAction.add(action2);
            compAction.add(action3);

            // Add compAction to the ActionManager
            ActionManager.addAction(createActionDT(compAction));

            // Test
            assertEquals(4, ActionManager.actionsInBuffer(instanceId), "Four actions (compAction + 3 children) should be in the buffer!");

            // Test all actions were added correctly
            boolean foundChangeRole = false;
            boolean foundProtectPlayers = false;
            boolean foundRevivePlayers = false;
            boolean foundCompAction = false;
            for (int act : ActionsDB.getNotExecutedActions(instanceId)) {
                Action action;
                try {
                    action = ActionsDB.getActionFromID(act).action();
                } catch (ClassNotFoundException e) {
                    foundCompAction = true;
                    continue;
                }
                // if changeRole has not been found and action is a ChangeRole action, set found to true
                if (!foundChangeRole && action instanceof ChangeRole) {
                    foundChangeRole = true;
                } else if (!foundProtectPlayers && action instanceof ProtectPlayers) {
                    foundProtectPlayers = true;
                } else if (!foundRevivePlayers && action instanceof RevivePlayers) {
                    foundRevivePlayers = true;
                }
            }
            assertTrue(foundChangeRole, "ChangeRole was not added correctly!");
            assertTrue(foundProtectPlayers, "ProtectPlayers was not added correctly!");
            assertTrue(foundRevivePlayers, "RevivePlayers was not added correctly!");
            assertTrue(foundCompAction, "CompAction was not added correctly!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }
    //endregion

    /**
     * actionsInBuffer test, without adding actions
     *
     * @utp.description Test whether the number of {@code Actions} in the buffer is zero at the start of the game.
     */
    @Test
    public void actionsInBufferEmptyTest() {
        try {
            assertEquals(0, ActionManager.actionsInBuffer(instanceId), "Actions in buffer should be zero!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }

    /**
     * actionsInBuffer test, three actions.
     *
     * @utp.description Test whether the number of {@code Actions} in the buffer is three after adding three {@code Actions}.
     */
    @Test
    public void actionsInBufferPopulatedTest() {
        try {
            // Create three actions
            ActionDT action1 = createActionDT(new ChangeRole());
            ActionDT action2 = createActionDT(new ProtectPlayers());
            ActionDT action3 = createActionDT(new RevivePlayers());

            // Add the actions to the buffer
            ActionManager.addAction(action1);
            ActionManager.addAction(action2);
            ActionManager.addAction(action3);

            // Test
            assertEquals(3, ActionManager.actionsInBuffer(instanceId), "Three actions should be in the buffer!");
        } catch (Exception e) {
            fail("An exception was thrown! " + e);
        }
    }
}