package org.lukos.model.voting;

import org.junit.jupiter.api.Test;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.exceptions.voting.NotAllowedToJoinVoteException;
import org.lukos.model.exceptions.voting.VotingException;
import org.lukos.model.rolesystem.roles.mainroles.Townsperson;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.user.*;
import org.lukos.model.user.player.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link AlphaWolfVote}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 16-03-2022
 */
public class AlphaWolfVoteTest extends VoteTest {

    @Override
    protected Vote createNewInstance() {
        try {
            return new AlphaWolfVote(super.gameInstanceID, new ArrayList<>());
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            fail("Should not have thrown an error on empty list initialization");
            e.printStackTrace();
        }
        throw new NullPointerException("AlphaWolfVote is null");
    }

    /**
     * Tests the constructor with 1 parameter.
     *
     * @utp.description Tests the constructor with 1 parameter.
     */
    @Test
    public void constructorSingleParameterTest1() {
        try {
            // Create the vote
            int vid = (new AlphaWolfVote(super.gameInstanceID, new ArrayList<>())).getVid();

            // Test the single parameter constructor
            Vote vote = new AlphaWolfVote(vid);
            assertEquals(new ArrayList<>(), vote.getAllowed(), "Allowed list should be the same as an empty one.");
            assertEquals(VoteType.ALPHA_WOLF, vote.getVoteType(), "Type should be the same.");
            assertEquals(new ArrayList<>(), vote.getBallots(), "Ballots list should be the same as an empty one.");
            assertFalse(vote.started(), "Vote should not be started.");
            assertFalse(vote.ended(), "Vote should not be ended.");
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            e.printStackTrace();
            fail("Should not throw constructor error.");
        }
    }

    /**
     * Tests the constructor with 1 parameter.
     *
     * @utp.description Tests the constructor with 1 parameter.
     */
    @Test
    public void constructorSingleParameterTest2() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            List<PlayerIdentifier> allowedIDs = allowed.stream().map(Player::getPlayerIdentifier).toList();
            setWerewolfRole(allowed);

            // Create the vote
            int vid = (new AlphaWolfVote(super.gameInstanceID, new ArrayList<>(allowed))).getVid();

            // Test the single parameter constructor
            Vote vote = new AlphaWolfVote(vid);
            assertEquals(2, vote.getAllowed().size(), "Both allowed lists should have the same size.");
            assertTrue(allowedIDs.containsAll(vote.getAllowed()), "Allowed should contain all vote.getAllowed().");
            assertTrue(vote.getAllowed().containsAll(allowedIDs), "vote.getAllowed() should contain all Allowed.");
            assertEquals(VoteType.ALPHA_WOLF, vote.getVoteType(), "Type should be the same.");
            assertEquals(new ArrayList<>(), vote.getBallots(), "Ballots list should be the same as an empty one.");
            assertFalse(vote.started(), "Vote should not be started.");
            assertFalse(vote.ended(), "Vote should not be ended.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests the constructor with 2 parameters.
     *
     * @utp.description Tests the constructor with 2 parameters.
     */
    @Test
    public void constructorMultiParameterTest1() {
        try {
            Vote vote = new AlphaWolfVote(super.gameInstanceID, new ArrayList<>());

            assertEquals(new ArrayList<>(), vote.getAllowed(), "Allowed list should be the same as an empty one.");
            assertEquals(VoteType.ALPHA_WOLF, vote.getVoteType(), "Type should be the same.");
            assertEquals(new ArrayList<>(), vote.getBallots(), "Ballots list should be the same as an empty one.");
            assertFalse(vote.started(), "Vote should not be started.");
            assertFalse(vote.ended(), "Vote should not be ended.");
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            fail("Should not throw constructor error.");
        }
    }

    /**
     * Tests the constructor with 2 parameters.
     *
     * @utp.description Tests the constructor with 2 parameters.
     */
    @Test
    public void constructorMultiParameterTest2() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            List<PlayerIdentifier> allowedIDs = allowed.stream().map(Player::getPlayerIdentifier).toList();
            setWerewolfRole(allowed);
            Vote vote = new AlphaWolfVote(super.gameInstanceID, new ArrayList<>(allowed));

            assertEquals(2, vote.getAllowed().size(), "Both allowed lists should have the same size.");
            assertTrue(allowedIDs.containsAll(vote.getAllowed()), "Allowed should contain all vote.getAllowed().");
            assertTrue(vote.getAllowed().containsAll(allowedIDs), "vote.getAllowed() should contain all Allowed.");
            assertEquals(VoteType.ALPHA_WOLF, vote.getVoteType(), "Type should be the same.");
            assertEquals(new ArrayList<>(), vote.getBallots(), "Ballots list should be the same as an empty one.");
            assertFalse(vote.started(), "Vote should not be started.");
            assertFalse(vote.ended(), "Vote should not be ended.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests whether the constructor throws an error when a no wolf tries to enter the vote.
     *
     * @utp.description Tests whether the constructor throws an error when a no wolf tries to enter the vote.
     */
    @Test
    public void constructorExceptionTest1() {
        Class<?> expected = NotAllowedToJoinVoteException.class;
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            setTownspersonRole(allowed);
            setWereWolfRole(allowed.get(0));
            new AlphaWolfVote(super.gameInstanceID, allowed);
            fail("Should have thrown an error.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * Tests whether the constructor throws an error when a no wolf tries to enter the vote.
     *
     * @utp.description Tests whether the constructor throws an error when a no wolf tries to enter the vote.
     */
    @Test
    public void constructorExceptionTest2() {
        Class<?> expected = NotAllowedToJoinVoteException.class;
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            setTownspersonRole(allowed);
            new AlphaWolfVote(super.gameInstanceID, allowed);
            fail("Should have thrown an error.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * Tests whether the constructor throws an error when a no wolf tries to enter the vote.
     *
     * @utp.description Tests whether the constructor throws an error when a no wolf tries to enter the vote.
     */
    @Test
    public void constructorExceptionTest3() {
        Class<?> expected = NotAllowedToJoinVoteException.class;
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);
            User henkie = UserManager.getInstance().createUser(new IssuerSub("vote", "test3"), "Henkie");
            henkie.joinGame(gameID);
            User chef = UserManager.getInstance().createUser(new IssuerSub("vote", "test4"), "Chef");
            chef.joinGame(gameID);
            User chefke = UserManager.getInstance().createUser(new IssuerSub("vote", "test5"), "Chefke");
            chefke.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            allowed.add(henkie.getPlayer());
            allowed.add(chef.getPlayer());
            allowed.add(chefke.getPlayer());

            setTownspersonRole(allowed);
            setWereWolfRole(allowed.get(0));
            setWereWolfRole(allowed.get(2));
            setWereWolfRole(allowed.get(3));
            new AlphaWolfVote(super.gameInstanceID, allowed);
            fail("Should have thrown an error.");
        } catch (Exception e) {
            assertTrue(expected.isInstance(e),
                    "type: " + e.getClass().getName() + " should have be instance of " + expected);
            assertNotNull(e.getMessage(), "Message should not be null");
        }
    }

    /**
     * Tests whether the {@code getAllowed()} function returns the right players (2 players).
     *
     * @utp.description Tests whether the {@code getAllowed()} function returns the right players (2 players).
     */
    @Test
    public void getAllowedTest1() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            List<PlayerIdentifier> allowedIDs = allowed.stream().map(Player::getPlayerIdentifier).toList();
            setWerewolfRole(allowed);

            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, new ArrayList<>(allowed));
            assertEquals(allowed.size(), vote.getAllowed().size(), "Both lists should have the same size.");
            assertTrue(allowedIDs.containsAll(vote.getAllowed()), "Allowed should contain all vote.getAllowed().");
            assertTrue(vote.getAllowed().containsAll(allowedIDs), "vote.getAllowed() should contain all Allowed.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests whether the {@code getAllowed()} function returns the right players (5 players).
     *
     * @utp.description Tests whether the {@code getAllowed()} function returns the right players (5 players).
     */
    @Test
    public void getAllowedTest2() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);
            User henkie = UserManager.getInstance().createUser(new IssuerSub("vote", "test3"), "Henkie");
            henkie.joinGame(gameID);
            User chef = UserManager.getInstance().createUser(new IssuerSub("vote", "test4"), "Chef");
            chef.joinGame(gameID);
            User chefke = UserManager.getInstance().createUser(new IssuerSub("vote", "test5"), "Chefke");
            chefke.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            allowed.add(henkie.getPlayer());
            allowed.add(chef.getPlayer());
            allowed.add(chefke.getPlayer());
            List<PlayerIdentifier> allowedIDs = allowed.stream().map(Player::getPlayerIdentifier).toList();
            setWerewolfRole(allowed);

            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, new ArrayList<>(allowed));
            assertEquals(allowed.size(), vote.getAllowed().size(), "Both lists should have the same size.");
            assertTrue(allowedIDs.containsAll(vote.getAllowed()), "Allowed should contain all vote.getAllowed().");
            assertTrue(vote.getAllowed().containsAll(allowedIDs), "vote.getAllowed() should contain all Allowed.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests whether the {@code getAllowed()} function returns the right players (5 players) even after creation of
     * another list with the original players.
     *
     * @utp.description Tests whether the {@code getAllowed()} function returns the right players (5 players) even after
     * creation of another list with the original players.
     */
    @Test
    public void getAllowedTest3() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);
            User henkie = UserManager.getInstance().createUser(new IssuerSub("vote", "test3"), "Henkie");
            henkie.joinGame(gameID);
            User chef = UserManager.getInstance().createUser(new IssuerSub("vote", "test4"), "Chef");
            chef.joinGame(gameID);
            User chefke = UserManager.getInstance().createUser(new IssuerSub("vote", "test5"), "Chefke");
            chefke.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            allowed.add(henkie.getPlayer());
            allowed.add(chef.getPlayer());
            allowed.add(chefke.getPlayer());
            setWerewolfRole(allowed);
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, new ArrayList<>(allowed));

            List<Player> newAllowed = new ArrayList<>();
            newAllowed.add(henkie.getPlayer());
            newAllowed.add(chefke.getPlayer());
            newAllowed.add(henk.getPlayer());
            newAllowed.add(jan.getPlayer());
            newAllowed.add(chef.getPlayer());
            List<PlayerIdentifier> newAllowedIDs = newAllowed.stream().map(Player::getPlayerIdentifier).toList();

            assertEquals(newAllowed.size(), vote.getAllowed().size(), "Both lists should have the same size.");
            assertTrue(newAllowedIDs.containsAll(vote.getAllowed()), "Allowed should contain all vote.getAllowed().");
            assertTrue(vote.getAllowed().containsAll(newAllowedIDs), "vote.getAllowed() should contain all Allowed.");
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests whether the {@code getBallots()} function returns the right ballots (2 ballots).
     *
     * @utp.description Tests whether the {@code getBallots()} function returns the right ballots (2 ballots).
     */
    @Test
    public void getBallotsTest1() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            setWerewolfRole(allowed);
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, new ArrayList<>(allowed));
            vote.start();

            try {
                Ballot ballot1 =
                        new Ballot(henk.getPlayer().getPlayerIdentifier(), jan.getPlayer().getPlayerIdentifier());
                Ballot ballot2 =
                        new Ballot(jan.getPlayer().getPlayerIdentifier(), henk.getPlayer().getPlayerIdentifier());
                vote.submitVote(henk.getPlayer().getPlayerIdentifier(), ballot1);
                vote.submitVote(jan.getPlayer().getPlayerIdentifier(), ballot2);

                List<Ballot> ballots = new ArrayList<>();
                ballots.add(ballot1);
                ballots.add(ballot2);

                assertEquals(ballots.size(), vote.getBallots().size(), "Both lists should have the same size.");
                assertTrue(ballots.containsAll(vote.getBallots()), "Ballots should contain all vote.getBallots().");
                assertTrue(vote.getBallots().containsAll(ballots), "vote.getBallots() should contain all Ballots.");
            } catch (VotingException e) {
                e.printStackTrace();
                fail("Should not throw an error.");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests whether the {@code getBallots()} function returns the right ballots (5 ballots).
     *
     * @utp.description Tests whether the {@code getBallots()} function returns the right ballots (5 ballots).
     */
    @Test
    public void getBallotsTest2() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);
            User henkie = UserManager.getInstance().createUser(new IssuerSub("vote", "test3"), "Henkie");
            henkie.joinGame(gameID);
            User chef = UserManager.getInstance().createUser(new IssuerSub("vote", "test4"), "Chef");
            chef.joinGame(gameID);
            User chefke = UserManager.getInstance().createUser(new IssuerSub("vote", "test5"), "Chefke");
            chefke.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            allowed.add(henkie.getPlayer());
            allowed.add(chef.getPlayer());
            allowed.add(chefke.getPlayer());

            setWerewolfRole(allowed);
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, new ArrayList<>(allowed));
            vote.start();

            try {
                PlayerIdentifier henkID = henk.getPlayer().getPlayerIdentifier();
                PlayerIdentifier janID = jan.getPlayer().getPlayerIdentifier();
                PlayerIdentifier chefID = chef.getPlayer().getPlayerIdentifier();
                PlayerIdentifier henkieID = henkie.getPlayer().getPlayerIdentifier();
                PlayerIdentifier chefkeID = chefke.getPlayer().getPlayerIdentifier();

                Ballot ballot1 = new Ballot(henkID, janID);
                Ballot ballot2 = new Ballot(janID, henkieID);
                Ballot ballot3 = new Ballot(henkieID, janID);
                Ballot ballot4 = new Ballot(chefID, henkID);
                Ballot ballot5 = new Ballot(chefkeID, henkieID);

                vote.submitVote(henkID, ballot1);
                vote.submitVote(janID, ballot2);
                vote.submitVote(henkieID, ballot3);
                vote.submitVote(chefID, ballot4);
                vote.submitVote(chefkeID, ballot5);

                List<Ballot> ballots = new ArrayList<>();
                ballots.add(ballot1);
                ballots.add(ballot2);
                ballots.add(ballot3);
                ballots.add(ballot4);
                ballots.add(ballot5);

                assertEquals(ballots.size(), vote.getBallots().size(), "Both lists should have the same size.");
                assertTrue(ballots.containsAll(vote.getBallots()), "Ballots should contain all vote.getBallots().");
                assertTrue(vote.getBallots().containsAll(ballots), "vote.getBallots() should contain all Ballots.");
            } catch (VotingException e) {
                e.printStackTrace();
                fail("Should not throw an error.");
            }
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }

    /**
     * Tests whether the result will be {@code null} without votes.
     *
     * @utp.description Tests whether the result of a {@code Vote} with zero {@code Ballot}s cast is {@code null} when
     * no {@code Player}s are allowed.
     * @utp.test_items {@code AlphaWolfVote}
     * @utp.input_specs -
     * @utp.output_specs Whether the result of a {@code Vote} with no players eligible to receive votes, and where zero
     * {@code Ballot}s are cast, is {@code null}.
     * @utp.env_needs -
     */
    @Test
    public void startEndBasicTest1() {
        try {
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, new ArrayList<>());
            vote.start();
            assertNull(vote.end());
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            fail("Should not throw constructor error.");
        }
    }

    /**
     * Tests whether the result will be null with parameters without votes.
     *
     * @utp.description Tests whether the result of a {@code Vote} with zero {@code Ballot}s cast is {@code null} when
     * at least some {@code Player}s are allowed.
     * @utp.test_items {@code AlphaWolfVote}
     * @utp.input_specs -
     * @utp.output_specs Whether the result of a {@code Vote} with two players eligible to receive votes, and where zero
     * {@code Ballot}s are cast, is {@code null}.
     * @utp.env_needs -
     */
    @Test
    public void startEndBasicTest2() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());

            setWerewolfRole(allowed);
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, allowed);
            vote.start();
            assertNull(vote.end());
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            fail("Should not throw constructor error.");
        }
    }

    /**
     * Tests whether vote opens.
     *
     * @utp.description Tests whether voting is impossible before a {@code AlphaWolfVote} is opened, and whether voting
     * is possible after a {@code AlphaWolfVote} has been opened.
     * @utp.test_items {@code AlphaWolfVote}
     * @utp.input_specs -
     * @utp.output_specs Whether voting throws a {@code VoteNotStartedException} before a {@code AlphaWolfVote} is
     * opened, and whether voting succeeds after a {@code AlphaWolfVote} has been opened.
     * @utp.env_needs -
     */
    @Test
    public void startTest() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());

            Ballot ballot = new Ballot(henk.getPlayer().getPlayerIdentifier(), jan.getPlayer().getPlayerIdentifier());
            setWerewolfRole(allowed);
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, allowed);

            vote.start();
            try {
                vote.submitVote(henk.getPlayer().getPlayerIdentifier(), ballot);
            } catch (Exception e) {
                fail("Thrown error while vote is open");
            }
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            fail("Should not throw constructor error.");
        }
    }

    /**
     * Tests whether the vote result is correct.
     *
     * @utp.description Tests whether the result of a {@code AlphaWolfVote} with 2 {@code Player}s eligible for
     * receiving votes is correct.
     * @utp.test_items {@code AlphaWolfVote}
     * @utp.input_specs -
     * @utp.output_specs Whether the result of a {@code AlphaWolfVote} in which {@code Player} Henk votes for {@code
     * Player} Jan and in which {@code Player} Jan votes for themselves is correct.
     * @utp.env_needs -
     */
    @Test
    public void end1Test() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            setWerewolfRole(allowed);
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, allowed);

            PlayerIdentifier henkID = henk.getPlayer().getPlayerIdentifier();
            PlayerIdentifier janID = jan.getPlayer().getPlayerIdentifier();

            vote.start();
            try {
                Ballot ballot1 = new Ballot(henkID, janID);
                Ballot ballot2 = new Ballot(janID, janID);

                vote.submitVote(henkID, ballot1);
                vote.submitVote(janID, ballot2);
            } catch (Exception e) {
                fail("Thrown error while vote is open");
            }
            Map<PlayerIdentifier, Integer> result = vote.end();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(2, result.get(janID));
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            fail("Should not throw constructor error.");
        }
    }

    /**
     * Tests whether the vote result is correct.
     *
     * @utp.description Tests whether the vote result is correct.
     */
    @Test
    public void end2Test() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);
            User henkie = UserManager.getInstance().createUser(new IssuerSub("vote", "test3"), "Henkie");
            henkie.joinGame(gameID);
            User chef = UserManager.getInstance().createUser(new IssuerSub("vote", "test4"), "Chef");
            chef.joinGame(gameID);
            User chefke = UserManager.getInstance().createUser(new IssuerSub("vote", "test5"), "Chefke");
            chefke.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            allowed.add(henkie.getPlayer());
            allowed.add(chef.getPlayer());
            allowed.add(chefke.getPlayer());
            setWerewolfRole(allowed);
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, allowed);

            PlayerIdentifier henkID = henk.getPlayer().getPlayerIdentifier();
            PlayerIdentifier janID = jan.getPlayer().getPlayerIdentifier();
            PlayerIdentifier chefID = chef.getPlayer().getPlayerIdentifier();
            PlayerIdentifier henkieID = henkie.getPlayer().getPlayerIdentifier();
            PlayerIdentifier chefkeID = chefke.getPlayer().getPlayerIdentifier();

            vote.start();
            try {
                Ballot ballot1 = new Ballot(henkID, janID);
                Ballot ballot2 = new Ballot(janID, janID);
                Ballot ballot3 = new Ballot(henkieID, janID);
                Ballot ballot4 = new Ballot(chefID, janID);
                Ballot ballot5 = new Ballot(chefkeID, henkieID);

                vote.submitVote(henkID, ballot1);
                vote.submitVote(janID, ballot2);
                vote.submitVote(henkieID, ballot3);
                vote.submitVote(chefID, ballot4);
                vote.submitVote(chefkeID, ballot5);
            } catch (Exception e) {
                fail("Thrown error while vote is open");
            }
            Map<PlayerIdentifier, Integer> result = vote.end();
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(4, result.get(janID));
            assertEquals(1, result.get(henkieID));
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            fail("Should not throw constructor error.");
        }
    }

    /**
     * Tests whether the vote result is correct.
     *
     * @utp.description Tests whether the vote result is correct.
     */
    @Test
    public void end3Test() {
        try {
            User henk = UserManager.getInstance().createUser(new IssuerSub("vote", "test1"), "Henk");
            int gameID = henk.createGame(gameName, gameSeed);
            User jan = UserManager.getInstance().createUser(new IssuerSub("vote", "test2"), "Jan");
            jan.joinGame(gameID);
            User henkie = UserManager.getInstance().createUser(new IssuerSub("vote", "test3"), "Henkie");
            henkie.joinGame(gameID);
            User chef = UserManager.getInstance().createUser(new IssuerSub("vote", "test4"), "Chef");
            chef.joinGame(gameID);
            User chefke = UserManager.getInstance().createUser(new IssuerSub("vote", "test5"), "Chefke");
            chefke.joinGame(gameID);

            List<Player> allowed = new ArrayList<>();
            allowed.add(henk.getPlayer());
            allowed.add(jan.getPlayer());
            allowed.add(henkie.getPlayer());
            allowed.add(chef.getPlayer());
            allowed.add(chefke.getPlayer());
            setWerewolfRole(allowed);
            AlphaWolfVote vote = new AlphaWolfVote(super.gameInstanceID, allowed);

            PlayerIdentifier henkID = henk.getPlayer().getPlayerIdentifier();
            PlayerIdentifier janID = jan.getPlayer().getPlayerIdentifier();
            PlayerIdentifier chefID = chef.getPlayer().getPlayerIdentifier();
            PlayerIdentifier henkieID = henkie.getPlayer().getPlayerIdentifier();
            PlayerIdentifier chefkeID = chefke.getPlayer().getPlayerIdentifier();

            vote.start();
            try {
                Ballot ballot1 = new Ballot(henkID, janID);
                Ballot ballot2 = new Ballot(janID, henkieID);
                Ballot ballot3 = new Ballot(henkieID, janID);
                Ballot ballot4 = new Ballot(chefID, henkID);
                Ballot ballot5 = new Ballot(chefkeID, henkieID);

                vote.submitVote(henkID, ballot1);
                vote.submitVote(janID, ballot2);
                vote.submitVote(henkieID, ballot3);
                vote.submitVote(chefID, ballot4);
                vote.submitVote(chefkeID, ballot5);
            } catch (Exception e) {
                fail("Thrown error while vote is open");
            }
            Map<PlayerIdentifier, Integer> result = vote.end();
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(2, result.get(janID));
            assertEquals(2, result.get(henkieID));
            assertEquals(1, result.get(henkID));
        } catch (GameException | SQLException | ReflectiveOperationException e) {
            fail("Should not throw constructor error.");
        }
    }

    /**
     * Tests whether IDs do not overlap and are not {@code null}.
     *
     * @utp.description Tests whether IDs do not overlap and are not {@code null}.
     */
    @Override @Test
    public void getVidTest() {
        super.getVidTest();
    }

    /**
     * @utp.description Tests whether the {@code submitVote()} function throws an {@code SQLException} when we try to
     * submit a ballot to a vote that does not exist.
     */
    @Override @Test
    public void submitVoteExceptionTest() {
        super.submitVoteExceptionTest();
    }

    /**
     * Tests whether the VoteTypes are the same as what we put into it.
     *
     * @utp.description Tests whether the VoteTypes are the same as what we put into it.
     */
    @Override @Test
    public void getVoteTypeTest() {
        super.getVoteTypeTest();
    }

    /**
     * @utp.description Tests whether the {@code isBusy()} only returns true when it is supposed to, i.e. it returns
     * true when it is started but not ended.
     */
    @Override @Test
    public void getBusyTest() {
        super.getBusyTest();
    }

    /**
     * @utp.description Tests the overridden {@code equal()} method.
     */
    @Override @Test
    public void equalsTest() {
        super.equalsTest();
    }

    // Sets townsperson as main role for a single player.
    private void setTownspersonRole(Player player) throws ReflectiveOperationException, SQLException {
        player.setMainRole(new Townsperson());
    }

    // Sets townsperson as main role for list of players
    private void setTownspersonRole(List<Player> players) throws ReflectiveOperationException, SQLException {
        for (Player player : players) {
            setTownspersonRole(player);
        }
    }

    // Sets werewolf as main role for a single player.
    private void setWereWolfRole(Player player) throws ReflectiveOperationException, SQLException {
        player.setMainRole(new Werewolf());
    }

    // Sets werewolf as main role for list of players.
    private void setWerewolfRole(List<Player> players) throws ReflectiveOperationException, SQLException {
        for (Player player : players) {
            setWereWolfRole(player);
        }
    }
}
