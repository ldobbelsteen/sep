package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.BeforeEach;
import org.lukos.database.InstanceDB;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.user.PlayerIdentifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Parent for test cases for {@link KillMarkedPlayersLynchTest} and {@link KillMarkedPlayersNightTest}
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class KillMarkedPlayersTest  extends ActionTest {

    protected PreActionDT preActionDT;

    @BeforeEach
    public void markPlayers() {
        try {
            InstanceDB.modifyExecuted(instanceId, secondPlayer.getPlayerIdentifier(), true);
            InstanceDB.modifyExecuted(instanceId, thirdPlayer.getPlayerIdentifier(), true);

            List<PlayerIdentifier> playerList = new ArrayList<>();
            playerList.add(secondPlayer.getPlayerIdentifier());
            playerList.add(thirdPlayer.getPlayerIdentifier());

            ActionEnc actionEnc = new ActionEnc(new ArrayList<>(), playerList);
            preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);

        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
}
