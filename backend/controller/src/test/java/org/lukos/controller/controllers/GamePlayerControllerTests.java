package org.lukos.controller.controllers;

import static org.lukos.controller.util.TestHelpers.convertJson;
import static org.lukos.model.user.UserManager.getInstance;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.lukos.controller.ControllerApplication;
import org.lukos.controller.GeneralController;
import org.lukos.controller.request.PlayerSubmitDeathnoteRequest;
import org.lukos.controller.util.TestHelpers;
import org.lukos.controller.websocket.WebSocketConfig;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
import org.lukos.model.user.player.Player;
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

/**
 * Test cases for {@code GamePlayerController}.
 *
 * @author Marco Pleket (1295713)
 * @since 12-03-2022
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers={ GeneralController.class })
public class GamePlayerControllerTests {
    /** Necessary for including contentType() and preventing 415 HTTP error */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /** The mock controller that mimics the server */
    @Autowired
    private MockMvc mockMvc;

    /** objectMapper used to interpret JSON strings */
    final private ObjectMapper objectMapper = new ObjectMapper();

    /** UserManager object to edit user states */
    UserManager UM = getInstance();

    /** The context of this particular app that the mock controller is setup with */
    @Autowired
    WebApplicationContext webAppContext;

    /** We can't avoid influencing the game directly here, so we use an InstanceManager. */
    private static final InstanceManager IM = InstanceManager.getInstanceManager();

    /** Before each test, configure the mock controller with the app and security context */
    @BeforeEach
    void setupPrincipal() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply(springSecurity())
                .build();
    }

    // We mock the OAuth2User through a method explained here:
    // https://github.com/spring-projects/spring-security/issues/8459

    /** @utp.description Test whether the failure message is correct when there was a {@code SQLException}. */
    @Test
    @DirtiesContext
    public void triggerSQLException() throws Exception {
        this.mockMvc.perform(get("/api/game/4816/player/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "playersReturnSuccess")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The instance with the given ID does not exist."));
    }

    /** @utp.description Tests success message for successful retrieval of list of players */
    @Test
    @DirtiesContext
    public void getPlayerListShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("playersReturnSuccess",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("playersReturnSuccess2",
                this.mockMvc, this.getClass().getName(), map);

        // Get player IDs
        String userId1 = TestHelpers.getUserFromString("playersReturnSuccess", this.mockMvc,
                this.getClass().getName(), this.objectMapper);
        String userId2 = TestHelpers.getUserFromString("playersReturnSuccess2", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Check whether the list is correct when the game has not yet started
        this.mockMvc.perform(get("/api/game/" + gameId + "/player/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "playersReturnSuccess")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.id").value(userId1))
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.playerStatus").doesNotExist())
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.name").value("testUser"))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.id").value(userId2))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.playerStatus").doesNotExist())
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.name").value("testUser"));

        // Start the game
        TestHelpers.startGameOK("playersReturnSuccess",
                this.mockMvc, this.getClass().getName());

        // Check whether the list is correct
        this.mockMvc.perform(get("/api/game/" + gameId + "/player/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "playersReturnSuccess")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.id").value(userId1))
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.playerStatus").value("ALIVE"))
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.name").value("testUser"))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.id").value(userId2))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.playerStatus").value("ALIVE"))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.name").value("testUser"));

        // Necessary to also check the condition under which the player is not a townsperson or any innocent role
        this.mockMvc.perform(get("/api/game/" + gameId + "/player/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "playersReturnSuccess2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.id").value(userId1))
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.playerStatus").value("ALIVE"))
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.name").value("testUser"))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.id").value(userId2))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.playerStatus").value("ALIVE"))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.name").value("testUser"));

        // Get the game instance to force 'kill' a user. Then, check the list again.
        IInstance gameInstance = IM.getInstance(Integer.parseInt(gameId));
        User user2 = UM.getUser(Integer.parseInt(userId2));
        gameInstance.killPlayer(user2.getPlayer());

        // Check if the player list is still correct
        this.mockMvc.perform(get("/api/game/" + gameId + "/player/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "playersReturnSuccess")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.id").value(userId1))
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.playerStatus").value("ALIVE"))
                .andExpect(jsonPath("$.players[0].simplePlayerEntry.name").value("testUser"))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.id").value(userId2))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.playerStatus").value("DECEASED"))
                .andExpect(jsonPath("$.players[1].simplePlayerEntry.name").value("testUser"));
    }

    /** @utp.description Tests NoSuchUserException if player not in game or not permitted */
    @Test
    @DirtiesContext
    public void getPlayerListShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("playersReturnFailure1",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("playersReturnFailure2",
                this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("playersReturnFailure1",
                this.mockMvc, this.getClass().getName());

        // Check if getting the list of players is not allowed
        this.mockMvc.perform(get("/api/game/" + gameId + "/player/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "playersReturnFailure3")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("No player found in database based on the user ID"));

        // Create a new game
        game = TestHelpers.createGameOK("playersReturnFailure3",
                this.mockMvc, this.getClass().getName());

        // Extract gameId of second game
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId2 = json.get("gameId").asText();
        // Make gameId2 into an input
        MultiValueMap<String, String> map2 = new LinkedMultiValueMap<String, String>();
        map2.add("gameId", gameId2);

        TestHelpers.joinGameOK("playersReturnFailure4",
                this.mockMvc, this.getClass().getName(), map2);
        TestHelpers.startGameOK("playersReturnFailure3",
                this.mockMvc, this.getClass().getName());

        // Check if getting the list of players of the other game is not allowed
        this.mockMvc.perform(get("/api/game/" + gameId + "/player/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "playersReturnFailure3")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not in a game!"));
    }

    /** @utp.description Tests success message for successful deathnote submission */
    @Test
    @DirtiesContext
    public void submitDeathnoteListShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("deathNoteReturnSuccess1",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        PlayerSubmitDeathnoteRequest var = new PlayerSubmitDeathnoteRequest();
        var.setContent("This game has not started yet");
        String requestJson = convertJson(var);

        TestHelpers.joinGameOK("deathNoteReturnSuccess2", this.mockMvc, this.getClass().getName(), map);

        TestHelpers.startGameOK("deathNoteReturnSuccess1", this.mockMvc, this.getClass().getName());

        var.setContent("New entry");
        requestJson = convertJson(var);

        this.mockMvc.perform(post("/api/game/" + gameId + "/player/submit_deathnote")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "deathNoteReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    /** @utp.description Tests NoPermissionException if player not in the specified game */
    @Test
    @DirtiesContext
    public void submitDeathnoteListShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("deathNoteReturnFailure1Dummy",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Create the deathnote
        PlayerSubmitDeathnoteRequest var = new PlayerSubmitDeathnoteRequest();
        var.setContent("Deathnote for a wrong game");
        String requestJson = convertJson(var);

        // Check whether the game has started exception
        this.mockMvc.perform(post("/api/game/" + gameId + "/player/submit_deathnote")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "deathNoteReturnFailure1Dummy")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The game hasn't started yet"));

        TestHelpers.joinGameOK("deathNoteReturnFailure2Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("deathNoteReturnFailure1Dummy", this.mockMvc, this.getClass().getName());

        // Create a new game, whose gamemaster will try to submit a deathnote elsewhere
        TestHelpers.createGameOK("deathNoteReturnFailure1", this.mockMvc, this.getClass().getName());

        this.mockMvc.perform(post("/api/game/" + gameId + "/player/submit_deathnote")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "deathNoteReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not in a game!"));
    }

    /** @utp.description Tests NoPermissionException if player has died */
    @Test
    @DirtiesContext
    public void submitDeathnoteListShouldReturnFailure2() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("deathNoteReturnFailure2",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Create the deathnote
        PlayerSubmitDeathnoteRequest var = new PlayerSubmitDeathnoteRequest();
        var.setContent("Deathnote for a wrong game");
        String requestJson = convertJson(var);

        TestHelpers.joinGameOK("deathNoteReturnFailure3", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("deathNoteReturnFailure2", this.mockMvc, this.getClass().getName());

        String user = TestHelpers.getUserFromString("deathNoteReturnFailure2", mockMvc, this.getClass().getName(), objectMapper);

        Player player = UserManager.getInstance().getUser(Integer.parseInt(user)).getPlayer();
        InstanceManager.getInstanceManager().getInstance(Integer.parseInt(gameId)).killPlayer(player);

        this.mockMvc.perform(post("/api/game/" + gameId + "/player/submit_deathnote")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "deathNoteReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User cannot submit a new deathnote because the player died in the game!"));
    }

    /** @utp.description Tests success message for Obituary entry */
    @Test
    @DirtiesContext
    public void obituaryShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("obituaryReturnSuccess1",
                this.mockMvc, this.getClass().getName());

        // Obtain their user id
        String user = TestHelpers.getUserFromString("obituaryReturnSuccess1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("obituaryReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("obituaryReturnSuccess1", this.mockMvc, this.getClass().getName());

        Player player = UM.getUser(Integer.parseInt(user)).getPlayer();

        this.mockMvc.perform(get("/api/game/" + gameId + "/player/" + user + "/obituary")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "obituaryReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0].group").value(player.getMainRole().getGroup().toString()))
                .andExpect(jsonPath("$.roles[0].name").value(player.getMainRole().getClass().getSimpleName()))
                .andExpect(jsonPath("$.deathNote").value(player.getDeathnote().getContent()));

        // Use i to keep track of the index
        int i = 1;
        // Loop over all the double roles
        for (DoubleRole role : player.getDoubleRoles()) {
            this.mockMvc.perform(get("/api/game/" + gameId + "/player/" + user + "/obituary")
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "getRoleReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles[" + i + "].group").value(role.getGroup().toString()))
                    .andExpect(jsonPath("$.roles[" + i + "].name").value(role.getClass().getSimpleName()))
                    .andExpect(jsonPath("$.deathNote").value(player.getDeathnote().getContent()));
            i++;
        }

        // Loop over all the jobs
        for (Job job : player.getJobs()) {
            this.mockMvc.perform(get("/api/game/" + gameId + "/player/" + user + "/obituary")
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "getRoleReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles[" + i + "].group").doesNotExist())
                    .andExpect(jsonPath("$.roles[" + i + "].name").value(job.getClass().getName()))
                    .andExpect(jsonPath("$.deathNote").value(player.getDeathnote().getContent()));
            i++;
        }
    }

    /** @utp.description Tests NoPermissionException when player does not match authentication */
    @Test
    @DirtiesContext
    public void obituaryShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("obituaryReturnFailure1",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("obituaryReturnFailure1Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("obituaryReturnFailure1", this.mockMvc, this.getClass().getName());

        // Obtain the other person's user ID
        String user = TestHelpers.getUserFromString("obituaryReturnFailure1Dummy", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        this.mockMvc.perform(get("/api/game/" + gameId + "/player/" + user + "/obituary")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "ObituaryReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You cannot perform this command!"));
    }

    /** @utp.description Tests NoSuchUserException if not in the (correct) game */
    @Test
    @DirtiesContext
    public void obituaryShouldReturnFailure2() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("obituaryReturnFailure2Dummy",
                this.mockMvc, this.getClass().getName());

        // Obtain their user ID
        String user = TestHelpers.getUserFromString("obituaryReturnFailure2Dummy", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Check whether obituary is callable when the game has not yet started
        this.mockMvc.perform(get("/api/game/" + gameId + "/player/" + user + "/obituary")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "ObituaryReturnFailure2Dummy")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The game hasn't started yet"));

        TestHelpers.joinGameOK("obituaryReturnFailure3Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("obituaryReturnFailure2Dummy", this.mockMvc, this.getClass().getName());

        // Simple game creation
        game = TestHelpers.createGameOK("obituaryReturnFailure2", this.mockMvc, this.getClass().getName());

        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId2 = json.get("gameId").asText();
        // Make gameId into an input
        map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId2);

        TestHelpers.joinGameOK("obituaryReturnFailure3Join", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("obituaryReturnFailure2", this.mockMvc, this.getClass().getName());

        this.mockMvc.perform(get("/api/game/" + gameId + "/player/" + user + "/obituary")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "ObituaryReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You cannot perform this command!"));
    }
}
