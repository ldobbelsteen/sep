package org.lukos.model.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.listeners.WinEventListener;
import org.lukos.model.rolesystem.Group;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link WinEvent}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 12-04-2022
 */
public class WinEventTest {

    /** The instance of {@link WinEvent} that can be used throughout testing */
    private WinEvent winEvent;

    /**
     * Method used to set up the necessary parts for testing.
     */
    @BeforeEach
    public void setUp() {
        this.winEvent = WinEvent.getWinEvent();
    }

    /** @utp.description Tests whether the singleton indeed only provides 1 instance of the class */
    @Test
    public void singletonTest() {
        assertEquals(WinEvent.getWinEvent(), WinEvent.getWinEvent());
    }

    /** @utp.description Tests whether the {@code subscribe()} function does indeed add a listener to the subscribers */
    @Test
    public void subscribeTest() {
        try {
            WinEventTest.EventListener listener = new WinEventTest.EventListener();
            winEvent.subscribe(listener);
            winEvent.notify(1, null);
            assertEquals(1, listener.timesNotified);
            for (int i = 0; i < 5; i++) {
                winEvent.notify(i, null);
            }
            assertEquals(6, listener.timesNotified);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * @utp.description Tests whether the {@code subscribe()} function does indeed remove a listener from the
     * subscribers
     */
    @Test
    public void unsubscribeTest() {
        try {
            WinEventTest.EventListener listener = new WinEventTest.EventListener();
            winEvent.subscribe(listener);
            winEvent.notify(1, null);
            winEvent.unsubscribe(listener);
            winEvent.notify(1, null);
            assertEquals(1, listener.timesNotified);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code notify()} function notifies the listeners correctly. */
    @Test
    public void notifyTest() {
        try {
            WinEventTest.EventListener listener = new WinEventTest.EventListener();
            winEvent.subscribe(listener);
            winEvent.notify(1, null);
            assertEquals(1, listener.timesNotified);
            assertEquals(1, listener.gid);
            assertNull(listener.group);

            int i = 0;
            for (Group group : Group.values()) {
                winEvent.notify(i, group);
                assertEquals(i, listener.gid);
                assertEquals(group, listener.group);
                i++;
            }
            assertEquals(Group.values().length + 1, listener.timesNotified);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Implementation of {@link WinEventListener} needed to test.
     */
    private static class EventListener implements WinEventListener {
        /** The amount of times this listener has been notified */
        int timesNotified = 0;
        /** The {@code gid} that is put into the update function */
        int gid = -1;
        /** The {@link Group} that is put into the win function */
        Group group = null;

        @Override
        public void win(int gid, Group winGroup) {
            this.timesNotified++;
            this.gid = gid;
            this.group = winGroup;
        }
    }
}
