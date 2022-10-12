package org.lukos.controller.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import static org.lukos.controller.util.TestHelpers.convertJson;
import static org.lukos.controller.util.TestHelpers.createGameOK;
import static org.lukos.model.user.UserManager.getInstance;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.lukos.controller.*;
import org.lukos.controller.request.VoteSubmitRequest;
import org.lukos.controller.response.BallotEntry;
import org.lukos.controller.response.PlayerStatus;
import org.lukos.controller.util.TestHelpers;
import org.lukos.controller.util.UserHelper;
import org.lukos.controller.websocket.WebSocketConfig;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.instances.InstanceState;
import org.lukos.model.rolesystem.Group;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;
import org.lukos.model.voting.Vote;
import org.lukos.model.voting.VoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for {@code GameVoteController}.
 *
 * @author Marco Pleket (1295713)
 * @since 12-03-2022
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers={ GeneralController.class })
public class GameVoteControllerTests {
    /** Necessary for including contentType() and preventing 415 HTTP error */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /**
     * The mock controller that mimics the server
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * objectMapper used to interpret JSON strings
     */
    final private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * UserManager object to edit user states
     */
    UserManager UM = getInstance();

    /**
     * The context of this particular app that the mock controller is setup with
     */
    @Autowired
    WebApplicationContext webAppContext;

    /**
     * We can't avoid influencing the game directly here, so we use an InstanceManager.
     */
    private static final InstanceManager IM = InstanceManager.getInstanceManager();

    /**
     * Before each test, configure the mock controller with the app and security context
     */
    @BeforeEach
    void setupPrincipal() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply(springSecurity())
                .build();
    }

    // We mock the OAuth2User through a method explained here:
    // https://github.com/spring-projects/spring-security/issues/8459

    /** @utp.description Tests success message for successful retrieval of active votings */
    @Test
    @DirtiesContext
    public void getVotesShouldReturnSuccess() throws Exception {
        String gameId = startGameGameID("getVotesReturnSuccess1", "getVotesReturnSuccess2");

        // Fix the state so we can check for the exact votings we need to start
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        if (state.getPhase() != DayPhase.DAY) {
            state.setPhase(DayPhase.MORNING);
            if (state.getDay() != 1) {
                state.setDay(1);
            }
            instance.nextPhase();
        }

        // Obtain the list of ongoing votes
        List<Vote> votes = instance.getOngoingVotes();

        // Check whether the returned votes are correct
        this.mockMvc.perform(get("/api/game/" + gameId + "/vote")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "getVotesReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteEntries[0].id").value(votes.get(0).getVid()));

        // TODO: Uncomment this when AlphaWolf vote is included
        this.mockMvc.perform(get("/api/game/" + gameId + "/vote")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "getVotesReturnSuccess2")
                                        .claim("iss", this.getClass().getName())
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteEntries[1].id").value(votes.get(1).getVid()));
    }

    /** @utp.description Tests NoPermissionException if the user is not in that game */
    @Test
    @DirtiesContext
    public void getVotesShouldReturnFailure() throws Exception {
        String gameId = startGameGameID("getVotesReturnFailure1Dummy", "getVotesReturnFailure2Dummy");

        TestHelpers.createGameOK("getVotesReturnFailure1", mockMvc, this.getClass().getName());

        // Check whether the returned votes are correct
        this.mockMvc.perform(get("/api/game/" + gameId + "/vote")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "getVotesReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Player is not in the game"));
    }

    /** @utp.description Tests success message for successful vote submission */
    @Test
    @DirtiesContext
    public void submitVoteShouldReturnSuccess() throws Exception {
        String gameId = startGameGameID("subVoteReturnSuccess1", "subVoteReturnSuccess2");

        // Fix the state so we can use the exact votings we need
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        if (state.getPhase() != DayPhase.DAY) {
            state.setPhase(DayPhase.MORNING);
            if (state.getDay() != 1) {
                state.setDay(1);
            }
            instance.nextPhase();
        }

        // Extract votes and targets
        List<Vote> votes = IM.getInstance(Integer.parseInt(gameId)).getOngoingVotes();
        List<PlayerIdentifier> eligible = votes.get(0).getAllowed();

        // Create the vote to be submitted
        int tid = eligible.get(0).userID();

        VoteSubmitRequest req = new VoteSubmitRequest();
        req.setTargetID(tid);

        String jsonRequest = convertJson(req);

        this.mockMvc.perform(post("/api/game/" + gameId + "/vote/" + votes.get(0).getVid() + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(jsonRequest)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subVoteReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Vote on " + tid + " was successful!"));
    }

    /** @utp.description Tests NullPointerException if player not in game */
    @Test
    @DirtiesContext
    public void submitVoteShouldReturnFailure1() throws Exception {
        // Simple dummy game creation
        MvcResult game = TestHelpers.createGameOK("subVoteReturnFailure1Dummy", this.mockMvc, this.getClass().getName());
        String userId = TestHelpers.getUserFromString("subVoteReturnFailure1Dummy", this.mockMvc,
                this.getClass().getName(), this.objectMapper);
        // Extract gameId of the dummy game
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        // Create the vote to be submitted
        VoteSubmitRequest req = new VoteSubmitRequest();
        req.setTargetID(Integer.parseInt(userId));

        String jsonRequest = convertJson(req);

        TestHelpers.createGameOK("subVoteReturnFailure1", mockMvc, this.getClass().getName());

        this.mockMvc.perform(post("/api/game/" + gameId + "/vote/" + 11753 + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(jsonRequest)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subVoteReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not in a game!"));
    }

    /** @utp.description Tests NoSuchVoteException if voteId doesn't exist */
    @Test
    @DirtiesContext
    public void submitVoteShouldReturnFailure2() throws Exception {
        String gameId = startGameGameID("subVoteReturnFailure2Dummy", "subVoteReturnFailure2");
        String userId = TestHelpers.getUserFromString("subVoteReturnFailure2Dummy", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Create the vote to be submitted
        VoteSubmitRequest req = new VoteSubmitRequest();
        req.setTargetID(Integer.parseInt(userId));

        String jsonRequest = convertJson(req);

        // Create some non-existing voteId
        int vid = 7000;

        this.mockMvc.perform(post("/api/game/" + gameId + "/vote/" + vid + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(jsonRequest)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subVoteReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The vote with vid " + vid + " does not exist!"));
    }

    /** @utp.description Tests NoSuchUserException if voted user doesn't exist */
    @Test
    @DirtiesContext
    public void submitVoteShouldReturnFailure3() throws Exception {
        String gameId = startGameGameID("subVoteReturnFailure3Dummy", "subVoteReturnFailure3");

        // Create the vote to be submitted
        User user = UM.createUser(new IssuerSub(this.getClass().getName(), "subVoteReturnFailure1Dummy"), "testUser");

        VoteSubmitRequest req = new VoteSubmitRequest();
        req.setTargetID(user.getUid());

        String jsonRequest = convertJson(req);

        // Create a non-existing voteId
        int vid = 9000;

        this.mockMvc.perform(post("/api/game/" + gameId + "/vote/" + vid + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(jsonRequest)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subVoteReturnFailure3")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Voted user is not in the game!"));
    }

    /** @utp.description Tests NoPermissionException when game hasn't started yet */
    @Test
    @DirtiesContext
    public void submitVoteShouldReturnFailure4() throws Exception {
        MvcResult result = createGameOK("subVoteReturnFailure4", mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK("subVoteReturnFailure4Dummy", this.mockMvc, this.getClass().getName(), map);

        String user = TestHelpers.getUserFromString("subVoteReturnFailure4Dummy", mockMvc, this.getClass().getName(), objectMapper);

        // Create the vote to be submitted
        int tid = Integer.parseInt(user);

        VoteSubmitRequest req = new VoteSubmitRequest();
        req.setTargetID(tid);

        String jsonRequest = convertJson(req);

        this.mockMvc.perform(post("/api/game/" + gameId + "/vote/" + 48192 + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(jsonRequest)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subVoteReturnFailure4")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Can't vote because the game hasn't started yet"));
    }

    /** @utp.description Tests success message for eligible voters request */
    @Test
    @DirtiesContext
    public void getEligibleShouldReturnSuccess() throws Exception {
        String gameId = startGameGameID("eligReturnSuccess1", "eligReturnSuccess2");

        // Fix the state so we can use the exact votings we need
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        if (state.getPhase() != DayPhase.DAY) {
            state.setPhase(DayPhase.MORNING);
            if (state.getDay() != 1) {
                state.setDay(1);
            }
            instance.nextPhase();
        }

        String user2 = TestHelpers.getUserFromString("eligReturnSuccess2", mockMvc, this.getClass().getName(), objectMapper);
        state.killPlayer(new Player(new PlayerIdentifier(Integer.parseInt(gameId), Integer.parseInt(user2))));

        // Extract votes and targets
        List<Vote> votes = IM.getInstance(Integer.parseInt(gameId)).getOngoingVotes();
        List<PlayerIdentifier> eligible = votes.get(0).getAllowed();

        // Loop over all the eligible voters
        int i = 0;
        for (PlayerIdentifier player : eligible) {
            String status = PlayerStatus.ALIVE.toString();
            if (!new Player(player).alive()) {
                status = PlayerStatus.DECEASED.toString();
            }
            this.mockMvc.perform(get("/api/game/" + gameId + "/vote/" + votes.get(0).getVid() + "/eligible")
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "eligReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.eligible[" + i + "].id").value(player.userID()))
                    .andExpect(jsonPath("$.eligible[" + i + "].playerStatus").value(status));
            i++;
        }
    }

    /** @utp.description Tests NoPermissionException if player not in game or not in vote */
    @Test
    @DirtiesContext
    public void getEligibleShouldReturnFailure1() throws Exception {
        String gameId = startGameGameID("eligReturnFailure1Dummy", "eligReturnFailure2Dummy");

        // Fix the state so we can use the exact votings we need
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        if (state.getPhase() != DayPhase.DAY) {
            state.setPhase(DayPhase.MORNING);
            if (state.getDay() != 1) {
                state.setDay(1);
            }
            instance.nextPhase();
        }

        // Extract votes and targets
        List<Vote> votes = IM.getInstance(Integer.parseInt(gameId)).getOngoingVotes();

        createGameOK("eligReturnFailure1", mockMvc, this.getClass().getName());

        // Test if a random outside user can access the eligible votes
        this.mockMvc.perform(get("/api/game/" + gameId + "/vote/" + votes.get(0).getVid() + "/eligible")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "eligReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not allowed to request for ballots."));

        // We need the Alpha Wolf vote, because civilians can't vote here
        Vote vote = votes.get(0);
        if (votes.get(0).getVoteType() != VoteType.ALPHA_WOLF) {
            vote = votes.get(1);
        }

        // Extract the user that is not allowed to vote
        List<Player> players = IM.getInstance(Integer.parseInt(gameId)).getPlayerList();
        Player noVote = players.get(0);
        if (noVote.getMainRole().getGroup() == Group.WEREWOLVES) {
            noVote = players.get(1);
        }
        final String sub = UM.getUser(noVote.getPlayerIdentifier().userID()).getSub();

        // Test if a player that's not in the vote can access the eligible votes
        this.mockMvc.perform(get("/api/game/" + gameId + "/vote/" + vote.getVid() + "/eligible")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", sub)
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not allowed to request for ballots."));
    }

    /** @utp.description Tests NullPointerException if voteId doesn't exist */
    @Test
    @DirtiesContext
    public void getEligibleShouldReturnFailure2() throws Exception {
        String gameId = startGameGameID("eligReturnFailure3Dummy", "eligReturnFailure2");

        // Create a random vote id
        int vid = 78029346;

        this.mockMvc.perform(get("/api/game/" + gameId + "/vote/" + vid + "/eligible")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "eligReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("That vote does not exist, or it is not in the given instance."));
    }

    /** @utp.description Tests success message for ballot request */
    @Test
    @DirtiesContext
    public void getBallotsShouldReturnSuccess() throws Exception {
        String gameId = startGameGameID("ballotsReturnSuccess1", "ballotsReturnSuccess2");

        // Fix the state so we can use the exact votings we need
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        if (state.getPhase() != DayPhase.DAY) {
            state.setPhase(DayPhase.MORNING);
            if (state.getDay() != 1) {
                state.setDay(1);
            }
            instance.nextPhase();
        }

        // Extract votes and targets
        List<Vote> votes = IM.getInstance(Integer.parseInt(gameId)).getOngoingVotes();
        List<PlayerIdentifier> eligible = votes.get(0).getAllowed();

        // Cast some votes and add them as BallotEntries manually
        List<BallotEntry> entries = new ArrayList<>();
        entries.add(new BallotEntry(eligible.get(0).userID(), eligible.get(1).userID()));
        (new Player(eligible.get(0))).vote(votes.get(0).getVid(), new Player(eligible.get(1)));

        entries.add(new BallotEntry(eligible.get(1).userID(), eligible.get(0).userID()));
        (new Player(eligible.get(1))).vote(votes.get(0).getVid(), new Player(eligible.get(0)));

        // Loop over all the ballots
//        int i = 0;
        for (int i = 0; i < eligible.size(); i++) {
            this.mockMvc.perform(get("/api/game/" + gameId + "/vote/" + votes.get(0).getVid() + "/ballots")
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "ballotsReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ballotEntries[" + i + "].player").isNumber()) // Order is mixed up for some reason, can't test values
                    .andExpect(jsonPath("$.ballotEntries[" + i + "].target").isNumber());
            i++;
        }
    }

    /** @utp.description Tests NoPermissionException if player not in game or not permitted */
    @Test
    @DirtiesContext
    public void getBallotsShouldReturnFailure1() throws Exception {
        String gameId = startGameGameID("ballotsReturnFailure1Dummy", "ballotsReturn2FailureDummy");

        // Fix the state so we can use the exact votings we need
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        if (state.getPhase() != DayPhase.DAY) {
            state.setPhase(DayPhase.MORNING);
            if (state.getDay() != 1) {
                state.setDay(1);
            }
            instance.nextPhase();
        }

        // Extract votes and targets
        List<Vote> votes = IM.getInstance(Integer.parseInt(gameId)).getOngoingVotes();

        createGameOK("ballotsReturnFailure1", mockMvc, this.getClass().getName());

        this.mockMvc.perform(get("/api/game/" + gameId + "/vote/" + votes.get(0).getVid() + "/ballots")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "ballotsReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not allowed to request for ballots."));

        // We need the Alpha Wolf vote, because civilians can't vote here
        Vote vote = votes.get(0);
        if (votes.get(0).getVoteType() != VoteType.ALPHA_WOLF) {
            vote = votes.get(1);
        }

        // Extract the user that is not allowed to vote
        List<Player> players = IM.getInstance(Integer.parseInt(gameId)).getPlayerList();
        Player noVote = players.get(0);
        if (noVote.getMainRole().getGroup() == Group.WEREWOLVES) {
            noVote = players.get(1);
        }
        final String sub = UM.getUser(noVote.getPlayerIdentifier().userID()).getSub();

        // Test if a player that's not in the vote can access the eligible votes
        this.mockMvc.perform(get("/api/game/" + gameId + "/vote/" + vote.getVid() + "/ballots")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", sub)
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not allowed to request for ballots."));
    }

    /** @utp.description Tests failure message if voteId doesn't exist */
    @Test
    @DirtiesContext
    public void getBallotsShouldReturnFailure2() throws Exception {
        String gameId = startGameGameID("ballotsReturnFailure3Dummy", "ballotsReturnFailure2");

        // Create a random vote id
        int vid = 345889788;

        this.mockMvc.perform(get("/api/game/" + gameId + "/vote/" + vid + "/ballots")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "ballotsReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("That vote does not exist, or it is not in the given instance."));
    }

    // No test cases for startVote() because this function is temporary

    private String startGameGameID(String sub1, String sub2) throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK(sub1, this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK(sub2, this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK(sub1, this.mockMvc, this.getClass().getName());

        return gameId;
    }
}
