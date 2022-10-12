package org.lukos.model.actionsystem.actions;

import org.junit.jupiter.api.Test;
import org.lukos.database.LocationDB;
import org.lukos.model.actionsystem.ActionDT;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.ActionManager;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.location.House;
import org.lukos.model.location.states.Burned;
import org.lukos.model.location.states.Repaired;
import org.lukos.model.location.states.Soaked;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for {@link CleanHouse}. This class is not used in the current version of the program, so the test are very
 * simple.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 19-04-2022
 */
public class CleanHouseTest extends ActionTest {

    /**
     * Simple constructor test.
     *
     * @utp.description Test whether the {@code CleanHouse} object is initialized correctly.
     */
    @Test
    public void constructorTest() {
        assertEquals("DrenchClean", new CleanHouse().getName(), "uhh, this is odd, the constructor is broken!");
    }

    /**
     * very simple test as this class is not used in the current version of the application.
     *
     * @utp.description Test whether the {@code House} is cleaned.
     */
    @Test
    public void cleanTest() {
        try {
            (new House(player.getHouse())).soak();

            List<Integer> locationList = new ArrayList<>();
            locationList.add(player.getHouse()); // Add house

            // create actionDT
            ActionEnc actionEnc = new ActionEnc(locationList, new ArrayList<>());
            PreActionDT preActionDT = new PreActionDT(player.getPlayerIdentifier(), actionEnc);
            ActionDT actionDT = new ActionDT(Instant.now(), new CleanHouse(), preActionDT);

            // Add action to manager
            ActionManager.addAction(actionDT);

            // perform action
            ActionManager.performActions(instanceId);

            assertEquals(Repaired.class, new House(player.getHouse()).getState().getClass(), "House should be cleaned");
        } catch (Exception e) {
            fail("An exception was thrown: " + e);
        }
    }
}
