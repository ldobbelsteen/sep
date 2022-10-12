package org.lukos.model.actionsystem.actions;

import org.lukos.model.actionsystem.Action;
import org.lukos.model.actionsystem.PreActionDT;

import java.time.Instant;

/**
 * Action used to convert a players role
 * 
 * @author Lucas Gether-Rønning
 * @since 26-02-22
 *
 * @author Valentijn van den Berg (1457446)
 * @since 25-03-22
 */
public class Convert extends Action {

    public Convert() {
        super("Convert");
    }

    // FIXME: One problem will be checking if the player has already been converted today, because if that is the case the player should not be converted again.
    @Override
    public void execute(PreActionDT data, Instant time, int actionId) {
//        List<Player> playersToConvert = new ArrayList<>(data.players());
//        data.locations().forEach(location -> {
//            try {
//                playersToConvert.addAll(location.getPlayersAtLocation());
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//
//        for (Player player: playersToConvert) {
//            // TODO: when Follower class has been updated, make the players join the correct cult
//            try {
//                player.addDoubleRole(new Follower());
//            } catch (DuplicatePurposeException e) {
//                // Check if the player is part of another cult already
//                // TODO: remove double role and add the new one if the player is part of another cult already.
//                e.printStackTrace();
//            }
//        }
    }
}
