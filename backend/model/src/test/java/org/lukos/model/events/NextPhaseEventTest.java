package org.lukos.model.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.listeners.NextPhaseListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for {@link NextPhaseEvent}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 12-04-2022
 */
public class NextPhaseEventTest {

    /** The instance of {@link NextPhaseEvent} that can be used throughout testing */
    private NextPhaseEvent nextPhaseEvent;

    /**
     * Method used to set up the necessary parts for testing.
     */
    @BeforeEach
    public void setUp() {
        this.nextPhaseEvent = NextPhaseEvent.getNextPhaseEvent();
    }

    /** @utp.description Tests whether the singleton indeed only provides 1 instance of the class */
    @Test
    public void singletonTest() {
        assertEquals(NextPhaseEvent.getNextPhaseEvent(), NextPhaseEvent.getNextPhaseEvent());
    }

    /** @utp.description Tests whether the {@code subscribe()} function does indeed add a listener to the subscribers */
    @Test
    public void subscribeTest() {
        try {
            EventListener listener = new EventListener();
            nextPhaseEvent.subscribe(listener);
            nextPhaseEvent.notify(1);
            assertEquals(1, listener.timesNotified);
            for (int i = 0; i < 5; i++) {
                nextPhaseEvent.notify(i);
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
            EventListener listener = new EventListener();
            nextPhaseEvent.subscribe(listener);
            nextPhaseEvent.notify(1);
            nextPhaseEvent.unsubscribe(listener);
            nextPhaseEvent.notify(1);
            assertEquals(1, listener.timesNotified);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /** @utp.description Tests whether the {@code notify()} function notifies the listeners correctly. */
    @Test
    public void notifyTest() {
        try {
            EventListener listener = new EventListener();
            nextPhaseEvent.subscribe(listener);
            nextPhaseEvent.notify(1);
            assertEquals(1, listener.timesNotified);
            assertEquals(1, listener.gid);

            for (int i = 0; i < 5; i++) {
                nextPhaseEvent.notify(i);
                assertEquals(i, listener.gid);
            }
            assertEquals(6, listener.timesNotified);
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Implementation of {@link NextPhaseListener} needed to test.
     */
    private static class EventListener implements NextPhaseListener {
        /** The amount of times this listener has been notified */
        int timesNotified = 0;
        /** The {@code gid} that is put into the update function */
        int gid = -1;

        @Override
        public void nextPhaseUpdate(int gid) {
            this.timesNotified++;
            this.gid = gid;
        }
    }
}
