package org.lukos.model.events;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.listeners.NextPhaseListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Event that gets triggered when a game goes into another phase.
 * <p>
 * This is a singleton class to prevent listeners hooking up to the wrong event.
 *
 * @author Rick van der Heijden (1461923)
 * @since 31-03-2022
 */
public class NextPhaseEvent {
    /** List with the listeners that are listening to this event */
    private final List<NextPhaseListener> listeners;

    /** Private constructor since it is a singleton */
    private NextPhaseEvent() {
        this.listeners = new ArrayList<>();
    }

    /**
     * Returns the instance of this event.
     *
     * @return this event
     */
    public static NextPhaseEvent getNextPhaseEvent() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * Method to subscribe to this event, so that a listener gets notified when this event occurs.
     *
     * @param listener the subscriber
     */
    public void subscribe(NextPhaseListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Method to unsubscribe from this event, so that the listener will not get any more notifications when this event
     * occurs.
     *
     * @param listener the subscriber
     */
    public void unsubscribe(NextPhaseListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Method to notify all subscribers that a phase has changed in an {@code Instance} with the ID {@code gid}.
     *
     * @param gid the ID of the {@code Instance}
     * @throws SQLException  when an SQL exception occurs
     * @throws GameException when an exception in the game logic occurs
     */
    public void notify(int gid) throws SQLException, GameException {
        for (NextPhaseListener listener : listeners) {
            listener.nextPhaseUpdate(gid);
        }
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final NextPhaseEvent uniqueInstance = new NextPhaseEvent();
    }
}
