package org.lukos.model.voting;

import org.junit.jupiter.api.Test;
import org.lukos.model.GameTest;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link BallotCheckerHelper}.
 *
 * @author Rick van der Heijden (1461923)
 * @since 12-04-2022
 */
public class BallotCheckerHelperTest extends GameTest {

    /** @utp.description Tests whether the function {@code ballotChecker} demonstrates the right behaviour. */
    @Test
    public void ballotCheckerTest() {
        try {
            User user1 = UserManager.getInstance().createUser(new IssuerSub("BallotCheckerHelper", "Test1"), "Test1");
            int gameID = user1.createGame("BallotChecker", 1);
            User user2 = UserManager.getInstance().createUser(new IssuerSub("BallotCheckerHelper", "Test2"), "Test2");
            user2.joinGame(gameID);

            IInstance instance = InstanceManager.getInstanceManager().getInstance(gameID);
            instance.startGame(user1.getUid());
            instance.getInstanceState().setDay(1);

            user1.getPlayer().setMainRole(new Werewolf());
            user2.getPlayer().setMainRole(new Werewolf());

            PlayerIdentifier playerID1 = user1.getPlayer().getPlayerIdentifier();
            PlayerIdentifier playerID2 = user2.getPlayer().getPlayerIdentifier();

            Vote vote = instance.startVote(VoteType.ALPHA_WOLF);
            int vid = vote.getVid();
            vote.submitVote(playerID1, new Ballot(playerID1, playerID2));

            BallotCheckerHelper.ballotChecker(vid);
            assertTrue(vote.isBusy());
            assertFalse(vote.ended());

            vote.submitVote(playerID2, new Ballot(playerID2, playerID2));
            BallotCheckerHelper.ballotChecker(vid);
            assertFalse(vote.isBusy());
            assertTrue(vote.ended());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e);
        }
    }
}
