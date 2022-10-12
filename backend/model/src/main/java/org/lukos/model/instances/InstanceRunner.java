package org.lukos.model.instances;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.voting.VotingException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to update all instances to the next phase, based on real time progression.
 *
 * @author Rick van der Heijden (1461923)
 * @author Martijn van Andel (1251104)
 * @since 07-04-2022
 */
public class InstanceRunner {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /**
     * Scheduler objects for automatic time progression
     */
    Runnable gameRunnable = () -> {
        try {
            updateGamePhase();
        } catch (SQLException | GameException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    };
    private final ScheduledFuture<?> gameHandle = scheduler.scheduleAtFixedRate(gameRunnable, 1, 1, TimeUnit.MINUTES);

    /**
     * Returns the singleton of uniqueInstance.
     *
     * @return the singleton of uniqueInstance
     */
    public static InstanceRunner getInstanceRunner() {
        return InstanceRunner.SingletonHelper.uniqueInstance;
    }

    /**
     * Checks if the games need to move to the next phase, based on the current time.
     *
     * @throws GameException                when a game-logic operation fails
     * @throws SQLException                 when a database operation fails
     * @throws ReflectiveOperationException when a reflective operation fails
     */
    void updateGamePhase() throws GameException, SQLException, ReflectiveOperationException {
        LocalDateTime now = LocalDateTime.now();
        int currentMinute = now.getHour() * 60 + now.getMinute();

        for (IInstance i : InstanceManager.getInstanceManager().getInstances()) {
            if (i.getInstanceState().getPhase() == InstanceState.getPhaseFromTime(currentMinute).previous()) {

                i.nextPhase();
            }
        }
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final InstanceRunner uniqueInstance = new InstanceRunner();
    }
}
