//package org.lukos.model.instances;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.lukos.model.exceptions.user.NoSuchPlayerException;
//import org.lukos.model.exceptions.location.HouseDoesNotExistException;
//import org.lukos.model.user.player.Player;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Test cases for {@code InstanceState}.
// *
// * @author Martijn van Andel (1251104)
// * @since 23-02-2022
// */
//public class InstanceStateTest {
//
//    private List<Player> alive;
//    private Player p;
//    private InstanceState is;
//
//    @BeforeEach
//    public void init() {
//        try {
//            alive = new ArrayList<>();
//            p = new Player(123, 1234);
//            alive.add(p);
//
//            is = new InstanceState(123); // New arraylist to decouple alive from is.getAlive()
//        } catch (SQLException | HouseDoesNotExistException e) {
//            fail("Unexpected exception: "+ e);
//        }
//
//    }
//
//    /**
//     * @utp.description Tests whether an InstanceState is properly created.
//     */
//    @Test
//    public void createInstanceStateTest() {
//        try {
//            int expectedDay = 0;
//            if (is.getPhase() == DayPhase.DAY) {
//                expectedDay = 1;
//            }
//            assertEquals(expectedDay, is.getDay());
//            assertEquals(alive, is.getAlive());
//
//            alive.remove(p);
//            assertNotEquals(alive, is.getAlive());
//        } catch (SQLException e) {
//            fail("Unexpected exception: "+ e);
//        }
//    }
//
//    /**
//     * @utp.description Tests moving to next day phase.
//     */
//    @Test
//    public void phaseChangeTest() {
//        try {
//            is.setPhase(DayPhase.DAY);
//            is.setDay(1);
//            assertEquals(DayPhase.DAY, is.getPhase());
//
//            is.nextPhase(); // Vote
//            assertNotEquals(DayPhase.DAY, is.getPhase());
//            assertEquals(DayPhase.VOTE, is.getPhase());
//
//            is.nextPhase(); // Execution
//            is.nextPhase(); // Evening
//            is.nextPhase(); // Night
//            is.nextPhase(); // Morning, day++
//            assertEquals(DayPhase.MORNING, is.getPhase());
//            assertEquals(2, is.getDay());
//        } catch (SQLException e) {
//            fail("Unexpected exception: "+ e);
//        }
//    }
//
//    /**
//     * @utp.description Tests setting phase from LocalDateTime.
//     */
//    @Test
//    public void setPhaseFromTimeTest() {
//
//        assertEquals( DayPhase.MORNING, is.getPhaseFromTime(8*60+25)); // Corresponds to 8:25
//        assertEquals( DayPhase.DAY, is.getPhaseFromTime(8*60+30)); // Corresponds to 8:30
//        assertEquals( DayPhase.DAY, is.getPhaseFromTime(17*60+59)); // Corresponds to 17:59
//
//        assertEquals( DayPhase.VOTE, is.getPhaseFromTime(18*60)); // Corresponds to 18:00
//        assertEquals( DayPhase.EXECUTION, is.getPhaseFromTime(20*60)); // Corresponds to 20:00
//        assertEquals( DayPhase.EVENING, is.getPhaseFromTime(20*60+5)); // Corresponds to 20:05
//        assertEquals( DayPhase.NIGHT, is.getPhaseFromTime(0)); // Corresponds to 00:00
//
//        assertEquals( DayPhase.NIGHT, is.getPhaseFromTime(60+23)); // Corresponds to 01:23
//        assertNotEquals(DayPhase.EVENING, is.getPhaseFromTime(9*60+15)); // Corresponds to 9:15 (!= EVENING)
//
//    }
//
//    /**
//     * @utp.description Tests whether the gameSpeed can be properly retrieved and adjusted.
//     */
//    @Test
//    public void gameSpeedTest() {
//        try {
//            // Test initial value of gameSpeed
//            assertEquals(is.getGameSpeed(), 0);
//
//            is.setDay(5);
//            assertEquals(is.getGameSpeed(), 1);
//        } catch (SQLException e) {
//            fail("Unexpected exception: "+ e);
//        }
//    }
//
//    /**
//     * @utp.description Tests whether a player can be 'killed' properly, i.e. removed from the 'alive' list.
//     */
//    @Test
//    public void killPlayerTestCorrectUsage() {
//        try {
//            // p in alive
//            assertTrue(is.getAlive().contains(p));
//
//            is.killPlayer(p);
//            // p not in alive
//            assertFalse(is.getAlive().contains(p));
//        } catch (SQLException e) {
//            fail("Unexpected exception: "+ e);
//        }
//    }
//
//    /**
//     * @utp.description Tests whether trying to kill a non-existing player is properly handled.
//     */
//    @Test
//    public void killNonExistingPlayerTest() {
//        try {
//            Player randomPlayer = new Player(12345, 123456);
//            is.killPlayer(randomPlayer);
//
//            fail("Should have thrown exception.");
//        } catch (NoSuchPlayerException e) {
//            assertTrue(true, "Correct exception thrown.");
//        } catch (Exception e) {
//            fail("Unexpected exception thrown.");
//        }
//    }
//
//    /**
//     * @utp.description Tests whether a player is successfully revived.
//     */
//    @Test
//    public void revivePlayerTest() {
//        try {
//            Player p = new Player(123, 1234);
//            // Assert player is not alive, just to be sure
//            assertFalse(is.getAlive().contains(p));
//
//            is.revivePlayer(p);
//            assertTrue(is.getAlive().contains(p));
//        } catch (SQLException | HouseDoesNotExistException e) {
//            fail("Unexpected exception: "+ e);
//        }
//    }
//}
