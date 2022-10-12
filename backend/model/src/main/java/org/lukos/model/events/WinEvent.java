package org.lukos.model.events;

import org.lukos.model.exceptions.GameException;
import org.lukos.model.listeners.WinEventListener;
import org.lukos.model.rolesystem.Group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Event that gets triggered when a game has concluded and there are winners.
 * <p>
 * This is a singleton class to prevent listeners hooking up to the wrong event.
 *
 * @author Martijn van Andel (1251104)
 * @since 04-04-2022
 */
public class WinEvent {
    /** List with the listeners that are listening to this event */
    private final List<WinEventListener> listeners;

    /** Private constructor since it is a singleton */
    private WinEvent() {
        this.listeners = new ArrayList<>();
    }

    /**
     * Returns the instance of this event.
     *
     * @return this event
     */
    public static WinEvent getWinEvent() {
        return SingletonHelper.uniqueInstance;
    }

    /**
     * Method to subscribe to this event, so that a listener gets notified when this event occurs.
     *
     * @param listener the subscriber
     */
    public void subscribe(WinEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Method to unsubscribe from this event, so that the listener will not get any more notifications when this event
     * occurs.
     *
     * @param listener the subscriber
     */
    public void unsubscribe(WinEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Method to notify all subscribers that a win event occurred in {@code Instance} with the ID {@code gid}.
     *
     * @param gid the ID of the {@code Instance}
     * @throws SQLException  when an SQL exception occurs
     * @throws GameException when an exception in the game logic occurs
     */
    public void notify(int gid, Group winGroup) throws SQLException, GameException, ReflectiveOperationException {
        for (WinEventListener listener : listeners) {
            listener.win(gid, winGroup);
        }
    }

    /**
     * Helper class to ensure that there will only be 1 single instance at all times, taking into account
     * thread-safety.
     */
    private static class SingletonHelper {
        private static final WinEvent uniqueInstance = new WinEvent();
    }
}
