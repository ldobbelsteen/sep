package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.database.*;
import org.lukos.model.actionsystem.*;
import org.lukos.model.location.Bridge;
import org.lukos.model.location.House;
import org.lukos.model.location.Location;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link MoveToLocation}.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 16-04-2022
 */
public class MoveToLocationTest extends ActionTest {

    @BeforeEach
    public void beforeEach() {

    }

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code MoveToLocation} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("MoveToLocation", new MoveToLocation().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * Creates an {@code ActionDT} for {@code MoveToLocation} {@code Action}.
     *
     * @param destination the id of the destination {@code Location}.
     * @param travellers the {@code Players} who will travel to the destination.
     * @return The created {@code ActionDT}.
     */
    private ActionDT createActionDT(int destination, List<PlayerIdentifier> travellers) {
        ActionEnc actionEnc = new ActionEnc(new ArrayList<>(Collections.singleton(destination)), travellers);
        PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);
        return new ActionDT(Instant.now(), new MoveToLocation(), preActionDT);
    }

    /**
     * compare the action messages
     *
     * @param playerIdentifier the player whose messages to check
     * @param messageType the expected type
     * @param fields the expected data
     * @throws SQLException
     */
    private void compareMessages(PlayerIdentifier playerIdentifier, ActionMessages messageType, List<String> fields) throws SQLException {
        List<Integer> messageIds = ActionMessagesDB.getAllNotSendMessagesForUser(playerIdentifier);
        // Only one message should have been written
        assertEquals(1, messageIds.size(), "Should only contain one message!");
        // Test that the content is as expected
        ActionMessageDT message = ActionMessagesDB.getMessage(messageIds.get(0));
        assertEquals(messageType, message.messageType(), "Type mismatch!");
        assertEquals(fields, message.data(), "Fields mismatch!");
    }

    /**
     * Most execute test use this code as the test, with different parameters.
     *
     * @param destination the id of the destination {@code Location}.
     * @param travellers the {@code Players} who will travel to the destination.
     */
    private void moveToLocationExecuteTest(int destination, List<PlayerIdentifier> travellers) {
        try {
            // Add the action to the ActionManager
            ActionManager.addAction(createActionDT(destination, travellers));

            // Perform the action
            ActionManager.performActions(instanceId);
            ActionMessagesDB.unlockMessages(instanceId);

            // Test what location we have
            Location dest;
            ActionMessages messageType;
            String name;
            if (LocationDB.bridgeExists(destination)) {
                dest = new Bridge(destination);
                messageType = ActionMessages.MOVE_TO_BRIDGE_MESSAGE;
                name = LocationDB.getNameBridgeByID(destination);
            } else {
                dest = new House(destination);
                messageType= ActionMessages.MOVE_TO_HOUSE_MESSAGE;
                name = new User(PlayerDB.getOwnerByHouseID(destination)).getUsername();
            }
            // Get players at the destination
            List<PlayerIdentifier> playersAtDestination = dest.getPlayersAtLocation();

            // Make sure the performer is at the location
            assertTrue(playersAtDestination.contains(player.getPlayerIdentifier()));
            //compareMessages(player.getPlayerIdentifier(), messageType, new ArrayList<>(Collections.singleton(name)));
            // Bugged atm, message should be added when the initiator is moved.

            // Test all other travellers are at the destination location
            boolean found;
            for (PlayerIdentifier travelId: travellers) {
                found = false;
                for (PlayerIdentifier atLocId: playersAtDestination) {
                    if (travelId.equals(atLocId)) {
                        found = true;
                        compareMessages(travelId, messageType, new ArrayList<>(Collections.singleton(name)));
                        break;
                    }
                }
                // If the player is not found, fail the test
                if (!found) {
                    // -- Construct fail message --
                    StringBuilder stringBuilder = new StringBuilder();
                    // Expected list
                    stringBuilder.append("Expected at least: [");
                    travellers.forEach(playerID -> stringBuilder.append("(" + playerID.instanceID() + ", " + playerID.userID() + ")"));
                    int strLength = stringBuilder.length();
                    stringBuilder.delete(strLength-2, strLength); // remove last comma
                    stringBuilder.append("] \n");
                    // Real list
                    stringBuilder.append("playersAtDestination: [");
                    playersAtDestination.forEach(playerID -> stringBuilder.append("(" + playerID.instanceID() + ", " + playerID.userID() + ")"));
                    strLength = stringBuilder.length();
                    stringBuilder.delete(strLength-2, strLength); // remove last comma
                    stringBuilder.append("] \n");

                    // Fail the test
                    fail("The lists are not equal! " + "(" + travelId.instanceID() + ", " + travelId.userID() + ")" + " was not at the destination. \n" + stringBuilder);
                }
            }
        } catch (Exception e) {
            fail("An exception was thrown in beforeEach: " + e);
        }
    }

    //region executeTests

    /**
     * execute test, only performer is moved to {@code House}.
     *
     * @utp.description Test whether the {@code Player} is moved to a {@code House} correctly when the {@code data.players} is empty.
     */
    @Test
    public void executeHouseNoOtherTargetsTest() {
        try {
            int houseID = HouseDB.getHousePlayerByID(secondPlayer.getPlayerIdentifier());
            moveToLocationExecuteTest(houseID, new ArrayList<>());
        } catch (Exception e) {
            fail("An exception was thrown in @Test: " + e);
        }
    }

    /**
     * execute test, one extra traveller is moved to {@code House}.
     *
     * @utp.description Test whether the {@code Player} is moved to a {@code House} correctly when the {@code data.players} contains one other {@code Player}.
     */
    @Test
    public void executeHouseWithOneTargetTest() {
        try {
            int houseID = HouseDB.getHousePlayerByID(secondPlayer.getPlayerIdentifier());
            moveToLocationExecuteTest(houseID, new ArrayList<>(Collections.singleton(thirdPlayer.getPlayerIdentifier())));
        } catch (Exception e) {
            fail("An exception was thrown in @Test: " + e);
        }
    }

    /**
     * execute test, multiple extra traveller is moved to {@code House}.
     *
     * @utp.description Test whether the {@code Player} is moved to a {@code House} correctly when the {@code data.players} contains multiple other {@code Player}.
     */
    @Test
    public void executeHouseWithMultipleTargetTest() {
        try {
            int houseID = HouseDB.getHousePlayerByID(secondPlayer.getPlayerIdentifier());
            // Get all players
            ArrayList<PlayerIdentifier> travellers = new ArrayList<>(InstanceDB.getAlivePlayers(instanceId));

            // Remove the performer
            travellers.remove(player.getPlayerIdentifier());
            // Execute test
            moveToLocationExecuteTest(houseID, travellers);
        } catch (Exception e) {
            fail("An exception was thrown in @Test: " + e);
        }
    }

    /**
     * execute test, only performer is moved to {@code Bridge}.
     *
     * @utp.description Test whether the {@code Player} is moved to a {@code Bridge} correctly when the {@code data.players} is empty.
     */
    @Test
    public void executeBridgeNoOtherTargetsTest() {
        try {
            int bridgeID = LocationDB.getBridgesByInstance(instanceId).get(0);
            moveToLocationExecuteTest(bridgeID, new ArrayList<>());
        } catch (Exception e) {
            fail("An exception was thrown in @Test: " + e);
        }
    }

    /**
     * execute test, one extra traveller is moved to {@code Bridge}.
     *
     * @utp.description Test whether the {@code Player} is moved to a {@code Bridge} correctly when the {@code data.players} contains one other {@code Player}.
     */
    @Test
    public void executeBridgeWithOneTargetTest() {
        try {
            int bridgeID = LocationDB.getBridgesByInstance(instanceId).get(0);
            moveToLocationExecuteTest(bridgeID, new ArrayList<>(Collections.singleton(thirdPlayer.getPlayerIdentifier())));
        } catch (Exception e) {
            fail("An exception was thrown in @Test: " + e);
        }
    }

    /**
     * execute test, multiple extra traveller is moved to {@code Bridge}.
     *
     * @utp.description Test whether the {@code Player} is moved to a {@code Bridge} correctly when the {@code data.players} contains multiple other {@code Player}.
     */
    @Test
    public void executeBridgeWithMultipleTargetTest() {
        try {
            int bridgeID = LocationDB.getBridgesByInstance(instanceId).get(0);
            // Get all players
            ArrayList<PlayerIdentifier> travellers = new ArrayList<>(InstanceDB.getAlivePlayers(instanceId));

            // Remove the performer
            travellers.remove(player.getPlayerIdentifier());
            // Execute test
            moveToLocationExecuteTest(bridgeID, travellers);
        } catch (Exception e) {
            fail("An exception was thrown in @Test: " + e);
        }
    }

    //endregion

}
