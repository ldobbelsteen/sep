package org.lukos.model.actionsystem.actions;

import org.lukos.database.ActionMessagesDB;
import org.lukos.database.PlayerDB;
import org.lukos.database.util.LocationHelper;
import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.location.Bridge;
import org.lukos.model.location.House;
import org.lukos.model.location.Location;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Moves the initiator and all players in {@code data.players()} to {@code data.data().location().get(0)}.
 *
 * @author Martijn van Andel (1251104)
 * @author Valentijn van den Berg (1457446)
 * @since 23-03-2022
 * @since 05-04-22
 */
public class MoveToLocation extends Action {

    public MoveToLocation() {
        super("MoveToLocation");
    }

    @Override
    public void execute(PreActionDT data, Instant time, int actionId) throws SQLException, GameException {
        IInstance instance = InstanceManager.getInstanceManager().getInstance(data.playerIdentifier().instanceID());

        // Get the new location
        Location destination = LocationHelper.getLocationByID(data.data().locations().get(0));

        // Move the initiator
        instance.movePlayer(new Player(data.playerIdentifier()), destination);
        // Move all other players is in the target list
        for (PlayerIdentifier pID : data.data().players()) {
            Player p = UserManager.getInstance().getUser(pID.userID()).getPlayer();
            instance.movePlayer(p, LocationHelper.getLocationByID(data.data().locations().get(0)));

            // Write a message to the database
            if (destination instanceof House house) {
                this.writeMessageToDataBase(house, p, actionId);
            } else if (destination instanceof Bridge bridge) {
                this.writeMessageToDataBase(bridge, p, actionId);
            }
        }
    }

    /**
     * Creates a message when travelling to a house.
     *
     * @param destination the destination {@code Location}
     * @param traveller   the player who traveled there
     * @param actionId    the actionID of {@code this}
     * @throws SQLException when there is a database error
     */
    private void writeMessageToDataBase(House destination, Player traveller, int actionId) throws SQLException {
        List<String> fields = new ArrayList<>();
        // Find the owner of the house and add their username to the message data
        fields.add(new User(PlayerDB.getOwnerByHouseID(destination.getId())).getUsername());

        // Write message to the database
        ActionMessagesDB.addNewMessage(new ActionMessageDT(ActionMessages.MOVE_TO_HOUSE_MESSAGE, fields), actionId,
                traveller.getPlayerIdentifier());
    }

    /**
     * Creates a message when travelling to a bridge.
     *
     * @param destination the destination {@code Location}
     * @param traveller   the player who traveled there
     * @param actionId    the actionID of {@code this}
     * @throws SQLException when there is a database error
     */
    private void writeMessageToDataBase(Bridge destination, Player traveller, int actionId) throws SQLException {
        List<String> fields = new ArrayList<>();
        // Add the bridge name to the message data
        fields.add(destination.getName());

        // Write message to the database
        ActionMessagesDB.addNewMessage(new ActionMessageDT(ActionMessages.MOVE_TO_BRIDGE_MESSAGE, fields), actionId,
                traveller.getPlayerIdentifier());

    }
}
