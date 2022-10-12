package org.lukos.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.model.actionsystem.ActionManager;
import org.lukos.model.actionsystem.PreActionDT;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.rolesystem.CharacterType;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.rolesystem.MainRole;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@code UserStatistic} class.
 *
 * @author Valentijn van den Berg (1457446)
 * @since 11-03-2022
 */
public class UserStatisticTest {
// TODO: fix test cases

//    private UserStatistic userStatistic;
//
//    /** Run before each test, also tests the constructor without parameters. */
//    @BeforeEach
//    public void beforeEach() {
//        userStatistic = new UserStatistic();
//        // Test whether the instance was initialized correctly
//        assertEquals(0, userStatistic.getGamesPlayed(), "Games played was not zero!");
//        assertEquals(0, userStatistic.getTimePlayed(), "Time played was not zero!");
//        assertEquals(0, sumMap(userStatistic.getWinsPerRole()),
//                "All values in the win map should be zero!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "All values in the lost map should be zero!");
//        assertEquals(0, userStatistic.getTotalWins(), "Total wins should be 0!");
//    }
//
//    /** Test the constructor with parameters. */
//    @Test
//    public void constructor1Test() {
//        // Create win map and add a game
//        Map<String, Integer> wins = new HashMap<>();
//        wins.put("SomeMainRole", 1);
//        // Create losses map and add two games
//        Map<String, Integer> losses = new HashMap<>();
//        losses.put("AnotherMainRole", 2);
//        // Create a new UserStatistic object
//        UserStatistic us = new UserStatistic(wins, losses, 3, 1, 650);
//        // Test that all values have been set correctly
//        assertEquals(3, us.getGamesPlayed(), "Games played should be 3!");
//        assertEquals(1, us.getTotalWins(), "Total wins should be 1!");
//        assertEquals(650, us.getTimePlayed(), "Time played should be 650!");
//        assertEquals(wins, us.getWinsPerRole(), "The win map is not equal!");
//        assertEquals(losses, us.getLossesPerRole(), "The losses map is not equal!");
//    }
//
//    /**
//     * Helper that sums the values from a map.
//     *
//     * @param map the map of which the values are summed
//     */
//    private int sumMap(Map<String, Integer> map) {
//        // Turns map into Stream and then to IntStream (with mapToInt) to take the sum of the map values.
//        return map.values().stream().mapToInt(i -> i).sum();
//    }
//
//    //region addGame
//    /** addGame test, add a game that has been won. */
//    @Test
//    public void addGameWonTest() {
//        // Add a game
//        userStatistic.addGame(new SomeMainRole(), true);
//
//        // Test that the game was added correctly
//        assertEquals(1, userStatistic.getGamesPlayed(),
//                "Games played should have increased by one!");
//        assertEquals(1, userStatistic.getWinsPerRole().get("SomeMainRole"),
//                "'SomeMainRole' wins should have increased by one!");
//        assertEquals(1, sumMap(userStatistic.getWinsPerRole()),
//                "The sum of the win map should be one!");
//        assertEquals(1, userStatistic.getTotalWins(),
//                "Total wins should have increased by one!");
//
//        // Test that no other values were changed
//        assertFalse(userStatistic.getLossesPerRole().containsKey("SomeMainRole"),
//                "'SomeMainRole' should not be in the losses map!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "The sum of the lost map should be zero!");
//        assertEquals(0, userStatistic.getTimePlayed(),
//                "Time played should still be zero!");
//
//    }
//
//    /** addGame test, add a game that has been lost. */
//    @Test
//    public void addGameLostTest() {
//        // Add a game
//        userStatistic.addGame(new SomeMainRole(), false);
//
//        // Test that the game was added correctly
//        assertEquals(1, userStatistic.getGamesPlayed(),
//                "Games played should have increased by one!");
//        assertFalse(userStatistic.getWinsPerRole().containsKey("SomeMainRole"),
//                "'SomeMainRole' wins should not be in the win map!");
//        assertEquals(1, userStatistic.getLossesPerRole().get("SomeMainRole"),
//                "'SomeMainRole' losses should have increased by one!");
//        assertEquals(1, sumMap(userStatistic.getLossesPerRole()),
//                "The sum of the lost map should be one!");
//
//        // Test that no other values were changed
//        assertEquals(0, userStatistic.getTotalWins(),
//                "Total wins should be zero!");
//        assertEquals(0, sumMap(userStatistic.getWinsPerRole()),
//                "The sum of the win map should be zero!");
//        assertFalse(userStatistic.getWinsPerRole().containsKey("SomeMainRole"),
//                "'SomeMainRole' should not be in the win map!");
//        assertEquals(0, userStatistic.getTimePlayed(),
//                "Time played should still be zero!");
//    }
//
//    /** addGame test, add games for two different roles. */
//    @Test
//    public void addGameDifferentRolesTest() {
//        // Add the first game with SomeMainRole role
//        userStatistic.addGame(new SomeMainRole(), true);
//
//        // Test that the game was added correctly
//        assertEquals(1, userStatistic.getGamesPlayed(),
//                "[first game] Games played should have increased by one!");
//        assertEquals(1, userStatistic.getWinsPerRole().get("SomeMainRole"),
//                "[first game] 'someMainRole' wins should have increased by one!");
//        assertEquals(1, userStatistic.getTotalWins(),
//                "[first game] Total wins should be increased by one!");
//        assertFalse(userStatistic.getWinsPerRole().containsKey("AnotherMainRole"),
//                "[first game] AnotherMainRole should not be in the map yet!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "[first game] The sum of the losses map should be zero!");
//        assertEquals(0, userStatistic.getTimePlayed(),
//                "[first game] Time played should still be zero!");
//
//        // Add the second game with AnotherMainRole role
//        userStatistic.addGame(new AnotherMainRole(), true);
//
//        // Test that this game was added correctly
//        assertEquals(2, userStatistic.getGamesPlayed(),
//                "[second game] Games played should have increased by one!");
//        assertEquals(1, userStatistic.getWinsPerRole().get("SomeMainRole"),
//                "[second game] 'someMainRole' wins should have stayed one!");
//        assertEquals(1, userStatistic.getWinsPerRole().get("AnotherMainRole"),
//                "[second game] 'AnotherMainRole' wins should have stayed one!");
//        assertEquals(2, userStatistic.getTotalWins(),
//                "[second game] Total wins should be two!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "[second game] The sum of the losses map should be zero!");
//        assertEquals(0, userStatistic.getTimePlayed(),
//                "[second game] Time played should still be zero!");
//    }
//    //endregion
//    //region addTime
//    /** addTime test, simple test, where we add 60 seconds. */
//    @Test
//    public void addTimeTest1() {
//        userStatistic.addTime(60);
//
//        assertEquals(60, userStatistic.getTimePlayed(),
//                "60 seconds was not added correctly!");
//        // Make sure all other values are not changed
//        assertEquals(0, userStatistic.getGamesPlayed(),
//                "Games played should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getWinsPerRole()),
//                "All values in the win map should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "All values in the lost map should still be zero!");
//        assertEquals(0, userStatistic.getTotalWins(),
//                "Total wins should still be 0!");
//    }
//
//    /** addTime test, simple test, where we add 60 seconds and then 1000 seconds. */
//    @Test
//    public void addTimeTest2() {
//        userStatistic.addTime(60);
//
//        assertEquals(60, userStatistic.getTimePlayed(),
//                "60 seconds was not added correctly!");
//        // Make sure all other values are not changed
//        assertEquals(0, userStatistic.getGamesPlayed(),
//                "Games played should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getWinsPerRole()),
//                "All values in the win map should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "All values in the lost map should still be zero!");
//        assertEquals(0, userStatistic.getTotalWins(),
//                "Total wins should still be 0!");
//
//        userStatistic.addTime(1000);
//
//        assertEquals(1060, userStatistic.getTimePlayed(),
//                "1060 seconds was not added correctly!");
//        // Make sure all other values are not changed
//        assertEquals(0, userStatistic.getGamesPlayed(),
//                "Games played should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getWinsPerRole()),
//                "All values in the win map should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "All values in the lost map should still be zero!");
//        assertEquals(0, userStatistic.getTotalWins(),
//                "Total wins should still be 0!");
//    }
//
//    /** addTime test, add max integer amount of time than add another second, this last second should not be added! */
//    @Test
//    public void addTimeMaxIntTest1() {
//        // Add Integer.MAX_VALUE time and test
//        userStatistic.addTime(Integer.MAX_VALUE);
//        assertEquals(Integer.MAX_VALUE, userStatistic.getTimePlayed(),
//                "Time was was not added correctly!");
//
//        // Add another second
//        userStatistic.addTime(1);
//        assertEquals(Integer.MAX_VALUE, userStatistic.getTimePlayed(),
//                "The one second should not have been added!!");
//        // Make sure all other values are not changed
//        assertEquals(0, userStatistic.getGamesPlayed(),
//                "Games played should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getWinsPerRole()),
//                "All values in the win map should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "All values in the lost map should still be zero!");
//        assertEquals(0, userStatistic.getTotalWins(),
//                "Total wins should still be 0!");
//    }
//
//    /**
//     * addTime test, add max integer-1 amount of time than add another 3 seconds, so we go over,
//     * timePlayed should cap at Integer.MAX_VALUE
//     */
//    @Test
//    public void addTimeMaxIntTest2() {
//        // Add Integer.MAX_VALUE time and test
//        userStatistic.addTime(Integer.MAX_VALUE-1);
//        assertEquals(Integer.MAX_VALUE-1, userStatistic.getTimePlayed(),
//                "Time was was not added correctly!");
//
//        // Add another second
//        userStatistic.addTime(3);
//        assertEquals(Integer.MAX_VALUE, userStatistic.getTimePlayed(),
//                "The one second should not have been added!!");
//
//        // Make sure all other values are not changed
//        assertEquals(0, userStatistic.getGamesPlayed(),
//                "Games played should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getWinsPerRole()),
//                "All values in the win map should still be zero!");
//        assertEquals(0, sumMap(userStatistic.getLossesPerRole()),
//                "All values in the lost map should still be zero!");
//        assertEquals(0, userStatistic.getTotalWins(),
//                "Total wins should still be 0!");
//    }
//    //endregion
//    //region getBasicStat
//    /**
//     * getBasicStats test, no games played yet,
//     * should return 0 for all 4 keys and no other keys should be in the map
//     */
//    @Test
//    public void getBasicStatsEmptyTest() {
//        createBasicExpectationMapAndCompare(0, 0, 0, 0);
//    }
//
//    /** getBasicStats test, add one win game */
//    @Test
//    public void getBasicStatOneGameTest() {
//        // Add game
//        userStatistic.addGame(new SomeMainRole(), true);
//        // Test
//        createBasicExpectationMapAndCompare(1, 1, 0, 0);
//    }
//
//    /** getBasicStats test, add some play time */
//    @Test
//    public void getBasicStatTimeTest() {
//        // Add time
//        userStatistic.addTime(60);
//        // Test
//        createBasicExpectationMapAndCompare(0, 0, 0, 60);
//    }
//
//    /** getBasicStats test, add some games and play time */
//    @Test
//    public void getBasicStatAllTest() {
//        // Add time
//        userStatistic.addTime(600);
//        createBasicExpectationMapAndCompare(0, 0, 0, 600);
//        //add games
//        userStatistic.addGame(new SomeMainRole(), true);
//        userStatistic.addGame(new AnotherMainRole(), false);
//        userStatistic.addGame(new SomeMainRole(), true);
//
//        // Test
//        createBasicExpectationMapAndCompare(3, 2, 1, 600);
//    }
//
//    /**
//     * Helper that creates the expected map then compares to the real map.
//     *
//     * @param gamesPlayed Total number of games played
//     * @param totalWins Total number of wins
//     * @param totalLosses Total number of losses
//     * @param timePlayed Total time played
//     * @return the expected map
//     */
//    private Map<String, Integer> createBasicExpectationMapAndCompare(
//            int gamesPlayed, int totalWins, int totalLosses, int timePlayed) {
//        Map<String, Integer> expected = new HashMap<>();
//        expected.put("gamesPlayed", gamesPlayed);
//        expected.put("totalWins", totalWins);
//        expected.put("totalLosses", totalLosses);
//        expected.put("timePlayed", timePlayed);
//        // Test that the expected map equals the real map
//        assertEquals(expected, userStatistic.getBasicStats(), "Expected map not equal to the result map!");
//
//        return expected;
//    }
//    //endregion
//    //region getAllStats
//    /** allStats test, no games played and no play time */
//    @Test
//    public void createAllStatsTest() {
//        createAllExpectationMapAndCompare(0, 0, 0, 0, 0, 0, 0, 0);
//    }
//
//    /** allStats test, one game played */
//    @Test
//    public void createAllStatsOneGameTest() {
//        // Add a game
//        userStatistic.addGame(new SomeMainRole(), true);
//
//        // Test
//        createAllExpectationMapAndCompare(1, 0, 0, 0, 1, 1, 0, 0);
//    }
//
//    /** allStats test, only change time played */
//    @Test
//    public void createAllStatsOnlyTimeTest() {
//        // Add time
//        userStatistic.addTime(100);
//
//        // Test
//        createAllExpectationMapAndCompare(0, 0, 0, 0, 0, 0, 0, 100);
//    }
//
//    /** allStats test, 3 game played and played 600 seconds */
//    @Test
//    public void createAllStatsFullTest1() {
//        // Add time
//        userStatistic.addTime(600);
//
//        // Add three games
//        userStatistic.addGame(new SomeMainRole(), true);
//        userStatistic.addGame(new AnotherMainRole(), false);
//        userStatistic.addGame(new SomeMainRole(), true);
//
//        // Test
//        createAllExpectationMapAndCompare(2, 0, 0, 1, 3, 2, 1, 600);
//    }
//
//    /** allStats test, 3 game played and played 600 seconds, with flipped wins/losses */
//    @Test
//    public void createAllStatsFullTest2() {
//        // Add time
//        userStatistic.addTime(600);
//
//        // Add three games
//        userStatistic.addGame(new SomeMainRole(), false);
//        userStatistic.addGame(new AnotherMainRole(), true);
//        userStatistic.addGame(new SomeMainRole(), false);
//
//        // Test
//        createAllExpectationMapAndCompare(0, 2, 1, 0, 3, 1, 2, 600);
//    }
//
//    /** allStats test, 9 game played and played 111000 seconds */
//    @Test
//    public void createAllStatsFullTest3() {
//        // Add time
//        userStatistic.addTime(111000);
//
//        // Add nine games
//        userStatistic.addGame(new SomeMainRole(), true);
//        userStatistic.addGame(new AnotherMainRole(), false);
//        userStatistic.addGame(new SomeMainRole(), true);
//        userStatistic.addGame(new SomeMainRole(), false);
//        userStatistic.addGame(new AnotherMainRole(), false);
//        userStatistic.addGame(new AnotherMainRole(), true);
//        userStatistic.addGame(new SomeMainRole(), false);
//        userStatistic.addGame(new AnotherMainRole(), true);
//        userStatistic.addGame(new SomeMainRole(), false);
//
//        // Test
//        createAllExpectationMapAndCompare(2, 3, 2, 2,9, 4, 5, 111000);
//    }
//
//    /**
//     * Creates an expected map for the total stats and compares it to the real map.
//     *
//     * @param someMainRoleWins number of times the user won as SomeMainRole
//     * @param someMainRoleLosses number of times the user lost as SomeMainRole
//     * @param anotherMainRoleWins number of times the user won as AnotherMainRole
//     * @param anotherMainRoleLosses number of times the user lost as AnotherMainRole
//     * @param gamesPlayed Total number of games played
//     * @param totalWins Total number of wins
//     * @param totalLosses Total number of losses
//     * @param timePlayed Total time played
//     */
//    private void createAllExpectationMapAndCompare(
//            int someMainRoleWins, int someMainRoleLosses, int anotherMainRoleWins, int anotherMainRoleLosses,
//            int gamesPlayed, int totalWins, int totalLosses, int timePlayed) {
//        // Generate basic stats
//        Map<String, Integer> basic = createBasicExpectationMapAndCompare(gamesPlayed, totalWins, totalLosses, timePlayed);
//        // Generate wins and losses map
//        Map<String, Integer> wins = new HashMap<>();
//        Map<String, Integer> losses = new HashMap<>();
//        // fill wins and losses per role maps
//        if (someMainRoleWins > 0) {
//            wins.put("SomeMainRole", someMainRoleWins);
//        }
//        if (someMainRoleLosses > 0) {
//            losses.put("SomeMainRole", someMainRoleLosses);
//        }
//        if (anotherMainRoleWins > 0) {
//            wins.put("AnotherMainRole", anotherMainRoleWins);
//        }
//        if (anotherMainRoleLosses > 0) {
//            losses.put("AnotherMainRole", anotherMainRoleLosses);
//        }
//        // Create the all stats map
//        Map<String,  Map<String, Integer>> expected = new HashMap<>();
//        expected.put("basic", basic);
//        expected.put("winsPerRole", wins);
//        expected.put("lossesPerRole", losses);
//        // Test
//        assertEquals(wins, userStatistic.getWinsPerRole(), "Wins map mismatch!");
//        assertEquals(losses, userStatistic.getLossesPerRole(), "Losses map mismatch!");
//        assertEquals(expected, userStatistic.getAllStats(), "All stats mismatch!");
//    }
//
//    /** fake main role for testing */
//    private static class SomeMainRole extends MainRole {
//
//        public SomeMainRole() {
//            super(CharacterType.NOT_SHADY, Group.TOWNSPEOPLE);
//        }
//
//        @Override
//        public void performAction(PreActionDT data) {
//        }
//
//        @Override
//        public void performAction(ActionManager actionManager, PreActionDT data) throws GameException, SQLException {
//
//        }
//
//        @Override
//        public void replenishAction(int gameSpeed) {
//
//        }
//    }
//
//    /** second fake main role for testing */
//    private static class AnotherMainRole extends MainRole {
//
//        public AnotherMainRole() {
//            super(CharacterType.NOT_SHADY, Group.ARSONIST);
//        }
//
//        @Override
//        public void performAction(PreActionDT data) {
//        }
//
//        @Override
//        public void performAction(ActionManager actionManager, PreActionDT data) throws GameException, SQLException {
//
//        }
//
//        @Override
//        public void replenishAction(int gameSpeed) {
//
//        }
//    }
}
