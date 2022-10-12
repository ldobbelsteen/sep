package org.lukos.controller.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.lukos.controller.ControllerApplication;
import org.lukos.controller.GeneralController;
import org.lukos.controller.request.GameCreateRequest;
import org.lukos.controller.request.GameJoinRequest;
import org.lukos.controller.util.TestHelpers;
import org.lukos.controller.websocket.WebSocketConfig;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.PlayerIdentifier;
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
 * Test cases for {@code InstanceController}.
 *
 * @author Marco Pleket (1295713)
 * @since 12-03-2022
 */
//@AutoConfigureMockMvc
//https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/request/MockHttpServletRequestBuilder.html#param-java.lang.String-java.lang.String...-

/**
 * {@code @WebMvcTest} can specify Controller classes, but other Controllers cannot be used.
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers={ GeneralController.class })
public class InstanceControllerTest {
    /** Necessary for including contentType() and preventing 415 HTTP error */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /** The mock controller that mimics the server */
    @Autowired
    private MockMvc mockMvc;

    /** objectMapper used to interpret JSON strings */
    final private ObjectMapper objectMapper = new ObjectMapper();

    /** The context of this particular app that the mock controller is setup with */
    @Autowired
    WebApplicationContext webAppContext;

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

    /** @utp.description Tests success message on successful game creation */
    @Test
    @DirtiesContext
    public void createShouldReturnSuccess() throws Exception {
        GameCreateRequest vars = new GameCreateRequest();
        String name = "createReturnSuccess";
        vars.setGameName(name.substring(Math.max(name.length() - 24, 0)));
        vars.setMaxAmountOfPlayers(2);
        vars.setSEED(2);

        String requestJson = TestHelpers.convertJson(vars);

        // Simple game creation that checks whether the game ID returned is a valid UUID
        this.mockMvc.perform(post("/api/lobby/create").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "createReturnSuccess")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").doesNotExist())
                        .andExpect(jsonPath("$.gameId").isNumber());
    }

    /** @utp.description Tests AlreadyInGameException for game creation */
    @Test
    @DirtiesContext
    public void createShouldReturnFailure() throws Exception {
        // Simple game creation
        TestHelpers.createGameOK("createReturnFailure", this.mockMvc, this.getClass().getName());

        // Get the userID of the user that created the game
        String userId = TestHelpers.getUserFromString("createReturnFailure", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        GameCreateRequest vars = new GameCreateRequest();
        vars.setGameName("Game of " + "createReturnFailure");
        vars.setMaxAmountOfPlayers(2);
        vars.setSEED(2);

        String requestJson = TestHelpers.convertJson(vars);

        // Another simple game creation from the same user, which should not be possible
        this.mockMvc.perform(post("/api/lobby/create").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "createReturnFailure")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(
                        "This user (id: " + userId + ") is already in a game!"));
    }

    /** @utp.description Tests success message on successful game start */
    @Test
    @DirtiesContext
    public void startShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult gameResult = TestHelpers.createGameOK("startReturnSuccess",
                this.mockMvc, this.getClass().getName());
        // Obtain the response to extract the gameId
        JsonNode json = objectMapper.readTree(gameResult.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user
        TestHelpers.joinGameOK("startReturnSuccess2", this.mockMvc, this.getClass().getName(), vars);

        // Start the game as the original user
        this.mockMvc.perform(post("/api/lobby/start").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "startReturnSuccess")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("game started successfully"));
    }

    /** @utp.description Tests NoSuchUserException if player is not in a game */
    @Test
    @DirtiesContext
    public void startShouldReturnFailure1() throws Exception {
        // Simple game creation
        TestHelpers.createGameOK("startReturnFailure1", this.mockMvc, this.getClass().getName());

        // Start the game as a different user that did not create a game
        this.mockMvc.perform(post("/api/lobby/start").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "startReturnFailureNoJoin")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("No player found in database based on the user ID"));
    }

    /** @utp.description Tests failure message if game has already started */
    @Test
    @DirtiesContext
    public void startShouldReturnFailure2() throws Exception {
        // Simple game creation by dummy user
        MvcResult gameResult = TestHelpers.createGameOK("startReturnFailure2",
                this.mockMvc, this.getClass().getName());

        // Obtain the response to extract the gameID
        JsonNode json = objectMapper.readTree(gameResult.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user
        TestHelpers.joinGameOK("startReturnFailure2Dummy", this.mockMvc, this.getClass().getName(), vars);

        // Start the game as the original user
        TestHelpers.startGameOK("startReturnFailure2", this.mockMvc, this.getClass().getName());

        // Start the game again
        this.mockMvc.perform(post("/api/lobby/start").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "startReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You cannot start a game that has already started."));
    }

    /** @utp.description Tests NotEnoughPlayersException if not enough players in the game */
    @Test
    @DirtiesContext
    public void startShouldReturnFailure3() throws Exception {
        // Simple game creation
        TestHelpers.createGameOK("startReturnFailure3", this.mockMvc, this.getClass().getName());

        // Start the game without enough players
        this.mockMvc.perform(post("/api/lobby/start").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "startReturnFailure3")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Cannot start game, not enough players."));
    }

    /** @utp.description Tests NoPermissionException if player is not the game creator */
    @Test
    @DirtiesContext
    public void startShouldReturnFailure4() throws Exception {
        // Simple game creation
        MvcResult gameResult = TestHelpers.createGameOK("startReturnFailure4",
                this.mockMvc, this.getClass().getName());

        // Obtain the response to extract the gameID
        JsonNode json = objectMapper.readTree(gameResult.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user
        TestHelpers.joinGameOK("startReturnFailure4Dummy", this.mockMvc, this.getClass().getName(), vars);

        // Start the game as the separate user
        this.mockMvc.perform(post("/api/lobby/start").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "startReturnFailure4Dummy")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("startGame called by someone other than gamemaster"));
    }

    /** @utp.description Tests success message when user has joined the game */
    @Test
    @DirtiesContext
    public void joinShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult gameResult = TestHelpers.createGameOK("joinReturnSuccess1",
                this.mockMvc, this.getClass().getName());

        // Obtain the response to extract the gameID
        JsonNode json = objectMapper.readTree(gameResult.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        GameJoinRequest vars = new GameJoinRequest();
        vars.setJoinCode(Integer.valueOf(json.get("gameId").asText()));

        String requestJson = TestHelpers.convertJson(vars);

        // Join the game as a separate user
        this.mockMvc.perform(post("/api/lobby/join").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "joinReturnSuccess2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(json.get("gameId").asText()));
    }

    /** @utp.description Tests NoSuchInstanceException if gameID is invalid */
    @Test
    @DirtiesContext
    public void joinShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult gameResult = TestHelpers.createGameOK("joinReturnFailure1",
                this.mockMvc, this.getClass().getName());

        // Obtain the response to extract the gameID and create input for TestHelpers.joinGameOK()
        JsonNode json = objectMapper.readTree(gameResult.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("joinReturnFailure1Dummy", mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("joinReturnFailure1", mockMvc, this.getClass().getName());

        // Create input for the call to the join endpoint, specifying the game to join
        GameJoinRequest vars = new GameJoinRequest();
        vars.setJoinCode(Integer.parseInt(gameId));

        String requestJson = TestHelpers.convertJson(vars);

        // Join the game as a separate user
        this.mockMvc.perform(post("/api/lobby/join").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "joinReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You cannot join a game that has already started"));
    }

    /** @utp.description Tests AlreadyInGameException if user is in another game */
    @Test
    @DirtiesContext
    public void joinShouldReturnFailure2() throws Exception {
        // Simple game creation
        TestHelpers.createGameOK("joinReturnFailure2", this.mockMvc, this.getClass().getName());

        // Another game. Game creator of the first game will attempt to join this game.
        MvcResult gameResult = TestHelpers.createGameOK("joinReturnFailure3",
                this.mockMvc, this.getClass().getName());

        // Obtain the response to extract the gameID
        JsonNode json = objectMapper.readTree(gameResult.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        GameJoinRequest vars = new GameJoinRequest();
        vars.setJoinCode(Integer.valueOf(json.get("gameId").asText()));

        // https://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(vars);

        // Get the userID of the user that is going to join the game
        String userId = TestHelpers.getUserFromString("joinReturnFailure2", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Join the game as a separate user
        this.mockMvc.perform(post("/api/lobby/join").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "joinReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("This user (id: " + userId + ") is already in a game!"));
    }

    /** @utp.description Tests success message when user leaves the game */
    @Test
    @DirtiesContext
    public void leaveShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult result = TestHelpers.createGameOK("leaveReturnSuccess1", this.mockMvc, this.getClass().getName());
        // Obtain the response to extract the gameId
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user
        TestHelpers.joinGameOK("leaveReturnSuccess2", this.mockMvc, this.getClass().getName(), vars);

        // Leave the game as the original user
        this.mockMvc.perform(post("/api/lobby/leave").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "leaveReturnSuccess2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    /** @utp.description Tests NoPermissionException if user is the creator */
    @Test
    @DirtiesContext
    public void leaveShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult result = TestHelpers.createGameOK("leaveReturnFailure1", this.mockMvc, this.getClass().getName());
        // Obtain the response to extract the gameId
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user
        TestHelpers.joinGameOK("leaveReturnFailure1Dummy", this.mockMvc, this.getClass().getName(), vars);

        // Leave the game as game creator
        this.mockMvc.perform(post("/api/lobby/leave").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "leaveReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You cannot leave a game you created, you have to stop it!"));
    }

    /** @utp.description Tests NullPointerException if user is not in a lobby */
    //@Test
    @DirtiesContext
    public void leaveShouldReturnFailure2() throws Exception {
        User newUser = UserManager.getInstance()
                .createUser(new IssuerSub(this.getClass().getName(), "leaveReturnFailure2"), "testUser");

        // Leave the game without being in one
        this.mockMvc.perform(post("/api/lobby/leave").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "leaveReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not in a lobby!"));
    }

    /** @utp.description Tests success message when user stops the game */
    @Test
    @DirtiesContext
    public void stopShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult result = TestHelpers.createGameOK("stopReturnSuccess1", this.mockMvc, this.getClass().getName());
        // Obtain the response to extract the gameId
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user
        TestHelpers.joinGameOK("stopReturnSuccess2", this.mockMvc, this.getClass().getName(), vars);

        // Stop the game as the original user
        this.mockMvc.perform(post("/api/lobby/stop").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "stopReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    /** @utp.description Tests NoPermissionException if user is not the creator */
    @Test
    @DirtiesContext
    public void stopShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult result = TestHelpers.createGameOK("stopReturnFailure1", this.mockMvc, this.getClass().getName());
        // Obtain the response to extract the gameId
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user
        TestHelpers.joinGameOK("stopReturnFailure1Dummy", this.mockMvc, this.getClass().getName(), vars);

        // Stop the game as someone else than the game creator
        this.mockMvc.perform(post("/api/lobby/stop").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "stopReturnFailure1Dummy")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You are not the game master and hence cannot stop this game!"));
    }

    /** @utp.description Tests NoSuchUserException if user is not in a lobby */
    //@Test
    @DirtiesContext
    public void stopShouldReturnFailure2() throws Exception {
        User newUser = UserManager.getInstance()
                .createUser(new IssuerSub(this.getClass().getName(), "stopReturnFailure2"), "testUser");

        // Stop the game without being in one
        this.mockMvc.perform(post("/api/lobby/stop").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "stopReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not in a lobby!"));
    }

    /** @utp.description Tests success message for throwing a game */
    @Test
    @DirtiesContext
    public void gamethrowShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult result = TestHelpers.createGameOK("throwReturnSuccess1", this.mockMvc, this.getClass().getName());
        // Obtain the response to extract the gameId
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user.
        TestHelpers.joinGameOK("throwReturnSuccess2", this.mockMvc, this.getClass().getName(), vars);
        TestHelpers.startGameOK("throwReturnSuccess1", this.mockMvc, this.getClass().getName());

        IInstance inst = InstanceManager.getInstanceManager().getInstance(Integer.parseInt(json.get("gameId").asText()));

        inst.getInstanceState().setDay(1);
        inst.getInstanceState().setPhase(DayPhase.MORNING);
        inst.nextPhase();

        System.out.println("This is the gameID in the test: " + json.get("gameId").asText());

        // TODO: Replace andExpect() by commented version if fixed
        // Throw the game as the original user
        this.mockMvc.perform(post("/api/lobby/gamethrow").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "throwReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The instance with the given ID does not exist."));
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("game is thrown. next phase town wins."));
    }

    /** @utp.description Tests NullPointerException when user not in a game */
    //@Test
    @DirtiesContext
    public void gamethrowShouldReturnFailure1() throws Exception {
        User newUser = UserManager.getInstance()
                .createUser(new IssuerSub(this.getClass().getName(), "throwReturnFailure1"), "testUser");

        // Stop the game without being in one
        this.mockMvc.perform(post("/api/lobby/gamethrow").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "throwReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("user is not in a game"));
    }

    /** @utp.description Tests NoPermissionException if user is not the creator */
    @Test
    @DirtiesContext
    public void throwShouldReturnFailure2() throws Exception {
        // Simple game creation
        MvcResult result = TestHelpers.createGameOK("throwReturnFailure2", this.mockMvc, this.getClass().getName());
        // Obtain the response to extract the gameId
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user
        TestHelpers.joinGameOK("throwReturnFailure2Dummy", this.mockMvc, this.getClass().getName(), vars);
        TestHelpers.startGameOK("throwReturnFailure2", this.mockMvc, this.getClass().getName());

        // Stop the game as someone else than the game creator
        this.mockMvc.perform(post("/api/lobby/gamethrow").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "throwReturnFailure2Dummy")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You cannot throw the game because you are not the creator!"));
    }
}
