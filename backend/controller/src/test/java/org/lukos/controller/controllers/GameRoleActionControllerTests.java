package org.lukos.controller.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.lukos.controller.ControllerApplication;
import org.lukos.controller.GeneralController;
import org.lukos.controller.request.ActionSubmitRequest;
import org.lukos.controller.util.TestHelpers;
import org.lukos.controller.websocket.WebSocketConfig;
import org.lukos.model.actionsystem.ActionEnc;
import org.lukos.model.actionsystem.ActionMessages;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.instances.InstanceState;
import org.lukos.model.rolesystem.Action;
import org.lukos.model.rolesystem.roles.mainroles.Clairvoyant;
import org.lukos.model.rolesystem.roles.mainroles.Townsperson;
import org.lukos.model.rolesystem.roles.mainroles.Werewolf;
import org.lukos.model.user.PlayerIdentifier;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.lukos.model.user.UserManager.getInstance;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test cases for {@code GameRoleUserController}.
 *
 * @author Marco Pleket (1295713)
 * @since 12-04-2022
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers={ GeneralController.class })
public class GameRoleActionControllerTests {
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
    /** @utp.description Tests success message for submitting an action */
    @DirtiesContext
    @Test
    public void submitActionShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subActionReturnSuccess1",
                this.mockMvc, this.getClass().getName());

        // Obtain their user ID
        String user = TestHelpers.getUserFromString("subActionReturnSuccess1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("subActionReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("subActionReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Get the player and force a certain role onto them
        Player player1 = UserManager.getInstance().getUser(Integer.parseInt(user)).getPlayer();

        // Obtain the other user and player
        user = TestHelpers.getUserFromString("subActionReturnSuccess2", this.mockMvc,
                this.getClass().getName(), this.objectMapper);
        Player player2 = UserManager.getInstance().getUser(Integer.parseInt(user)).getPlayer();

        // Force player 1 to be ClairVoyant and player 2 to be Werewolf (to prevent the winhandler from triggering)
        player1.setMainRole(new Clairvoyant());
        player2.setMainRole(new Werewolf());

        // Fix the state
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        state.setPhase(DayPhase.EXECUTION);
        if (state.getDay() != 1) {
            state.setDay(1);
        }
        instance.nextPhase();

        // Create the requestBody
        ActionSubmitRequest vars = new ActionSubmitRequest();
        ArrayList<Integer> player = new ArrayList<Integer>();
        player.add(Integer.parseInt(user));
        vars.setPlayerIDs(player);
        vars.setAction(Action.CLAIRVOYANT_SEE_ROLE);
        String requestJson = TestHelpers.convertJson(vars);

        this.mockMvc.perform(post("/api/game/" + gameId + "/purpose/submit").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subActionReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You have successfully performed the action."));
    }

    /** @utp.description Tests NoPermissionException if game not started */
    @DirtiesContext
    @Test
    public void submitActionShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subActionReturnFailure1",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        // Create the requestBody
        ActionSubmitRequest vars = new ActionSubmitRequest();
        vars.setAction(Action.CLAIRVOYANT_SEE_ROLE);
        String requestJson = TestHelpers.convertJson(vars);

        this.mockMvc.perform(post("/api/game/" + gameId + "/purpose/submit").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subActionReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The game has not yet started"));
    }

    /** @utp.description Tests NoPermissionException if user in wrong game */
    @DirtiesContext
    @Test
    @Disabled
    public void submitActionShouldReturnFailure2() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subActionReturnFailure2",
                this.mockMvc, this.getClass().getName());

        // Obtain their user ID
        String user = TestHelpers.getUserFromString("subActionReturnFailure2", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("subActionReturnFailure2Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("subActionReturnFailure2", this.mockMvc, this.getClass().getName());

        // Simple game creation
        game = TestHelpers.createGameOK("subActionReturnFailure2Extra",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId2 = json.get("gameId").asText();
        // Make gameId into an input
        map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId2);

        TestHelpers.joinGameOK("subActionReturnFailure3Extra", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("subActionReturnFailure2Extra", this.mockMvc, this.getClass().getName());

        // Create the requestBody
        ActionSubmitRequest vars = new ActionSubmitRequest();
        vars.setAction(Action.CLAIRVOYANT_SEE_ROLE);
        String requestJson = TestHelpers.convertJson(vars);

        this.mockMvc.perform(post("/api/game/" + gameId + "/purpose/submit").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subActionReturnFailure2Extra")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not in that game!"));
    }

    /** @utp.description Tests BridgeDoesNotExistException if bridge doesn't exist */
    @DirtiesContext
    @Test
    public void submitActionShouldReturnFailure3() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subActionReturnFailure3",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("subActionReturnFailure3Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("subActionReturnFailure3", this.mockMvc, this.getClass().getName());

        List<Integer> bridges = InstanceManager.getInstanceManager().getInstance(Integer.parseInt(gameId)).getBridges();
        // Force a random bridge that doesn't exist
        int randomInt = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        if (bridges.contains(randomInt)) {
            while (bridges.contains(randomInt)) {
                randomInt = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
            }
        }

        // Create the requestBody
        ActionSubmitRequest vars = new ActionSubmitRequest();
        vars.setAction(Action.CLAIRVOYANT_SEE_ROLE);
        ArrayList<Integer> subBridge = new ArrayList<Integer>();
        subBridge.add(randomInt);
        vars.setBridgeIDs(subBridge);
        String requestJson = TestHelpers.convertJson(vars);

        this.mockMvc.perform(post("/api/game/" + gameId + "/purpose/submit").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subActionReturnFailure3")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("That bridge does not exist!"));
    }

    /** @utp.description Tests HouseDoesNotExistException if house doesn't exist */
    @DirtiesContext
    @Test
    public void submitActionShouldReturnFailure4() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subActionReturnFailure4",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("subActionReturnFailure4Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("subActionReturnFailure4", this.mockMvc, this.getClass().getName());

        // Simple extra game creation
        TestHelpers.createGameOK("subActionReturnFailure4Extra", this.mockMvc, this.getClass().getName());

        // Obtain their user ID
        String user = TestHelpers.getUserFromString("subActionReturnFailure4Extra", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Create the requestBody
        ActionSubmitRequest vars = new ActionSubmitRequest();
        vars.setAction(Action.CLAIRVOYANT_SEE_ROLE);
        ArrayList<Integer> subHouse = new ArrayList<Integer>();
        subHouse.add(Integer.parseInt(user));
        vars.setHouseIDs(subHouse);
        // TODO: Uncomment when FIXME in actionSubmit() is resolved
//        // Added to avoid new test cases for missed lines of code in code coverage
//        List<Integer> bridges = InstanceManager.getInstanceManager().getInstance(Integer.parseInt(gameId)).getBridges();
//        List<Integer> bridgeInput = new ArrayList<Integer>();
//        bridgeInput.add(bridges.get(0));
//        System.out.println("The bridge ID is: " + bridgeInput.get(0));
//        vars.setBridgeIDs(bridgeInput);
        // Untill here
        String requestJson = TestHelpers.convertJson(vars);

        this.mockMvc.perform(post("/api/game/" + gameId + "/purpose/submit").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subActionReturnFailure4")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("That house does not exist!"));
    }

    /** @utp.description Tests NoSuchPlayerException if (submitted) player doesn't exist */
    @DirtiesContext
    @Test
    public void submitActionShouldReturnFailure5() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subActionReturnFailure5",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("subActionReturnFailure5Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("subActionReturnFailure5", this.mockMvc, this.getClass().getName());

        // Other user ID
        String otherUser = TestHelpers.getUserFromString("subActionReturnFailure5Dummy", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Simple extra game creation
        TestHelpers.createGameOK("subActionReturnFailure5Extra", this.mockMvc, this.getClass().getName());

        // Obtain their user ID
        String user = TestHelpers.getUserFromString("subActionReturnFailure5Extra", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Create the requestBody
        ActionSubmitRequest vars = new ActionSubmitRequest();
        vars.setAction(Action.CLAIRVOYANT_SEE_ROLE);
        ArrayList<Integer> subPlayer = new ArrayList<Integer>();
        subPlayer.add(Integer.parseInt(user));
        vars.setPlayerIDs(subPlayer);
        // Added to avoid new test cases for missed lines of code in code coverage
        ArrayList<Integer> subHouse = new ArrayList<Integer>();
        subHouse.add(Integer.parseInt(otherUser));
        vars.setHouseIDs(subHouse);
        // Untill here
        String requestJson = TestHelpers.convertJson(vars);

        this.mockMvc.perform(post("/api/game/" + gameId + "/purpose/submit").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subActionReturnFailure5")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("That player does not exist!"));
    }

    /** @utp.description Tests success message for obtaining action information */
    @DirtiesContext
    @Test
    public void actionInfoShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("infoReturnSuccess1",
                this.mockMvc, this.getClass().getName());

        // Obtain their user ID
        String user = TestHelpers.getUserFromString("infoReturnSuccess1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("infoReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("infoReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Get the player and force a certain role onto them
        Player player1 = UserManager.getInstance().getUser(Integer.parseInt(user)).getPlayer();

        // Obtain the other user and player
        user = TestHelpers.getUserFromString("infoReturnSuccess2", this.mockMvc,
                this.getClass().getName(), this.objectMapper);
        Player player2 = UserManager.getInstance().getUser(Integer.parseInt(user)).getPlayer();

        // Force player 1 to be ClairVoyant and player 2 to be Werewolf (to prevent the winhandler from triggering)
        player1.setMainRole(new Clairvoyant());
        player2.setMainRole(new Werewolf());

        // Fix the state
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        state.setPhase(DayPhase.EXECUTION);
        if (state.getDay() != 1) {
            state.setDay(1);
        }
        instance.nextPhase();

        this.mockMvc.perform(get("/api/game/" + gameId + "/purpose/action_information")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "infoReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isEmpty())
                .andExpect(jsonPath("$.actions[0].action").value(Action.CLAIRVOYANT_SEE_ROLE.toString()));

        this.mockMvc.perform(get("/api/game/" + gameId + "/purpose/action_information")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "infoReturnSuccess2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isEmpty())
                .andExpect(jsonPath("$.actions").isEmpty());
    }

    /** @utp.description Tests NoPermissionException if user not in correct game */
    @DirtiesContext
    @Test
    public void actionInfoShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("infoReturnFailure1",
                this.mockMvc, this.getClass().getName());


        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("infoReturnFailure1Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("infoReturnFailure1", this.mockMvc, this.getClass().getName());

        // Simple second game creation
       game = TestHelpers.createGameOK("infoReturnFailure1Extra",
                this.mockMvc, this.getClass().getName());


        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId2 = json.get("gameId").asText();
        // Make gameId into an input
        map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId2);

        TestHelpers.joinGameOK("infoReturnFailure2Extra", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("infoReturnFailure1Extra", this.mockMvc, this.getClass().getName());

        this.mockMvc.perform(get("/api/game/" + gameId2 + "/purpose/action_information")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "infoReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You cannot perform this command!"));
    }

    /** @utp.description Tests NoPermissionException if game has not started yet */
    @DirtiesContext
    @Test
    @Disabled
    public void actionInfoShouldReturnFailure2() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("infoReturnFailure2",
                this.mockMvc, this.getClass().getName());


        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        this.mockMvc.perform(get("/api/game/" + gameId + "/purpose/action_information")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "infoReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The game has not yet started"));
    }

    /** @utp.description Tests success message for obtaining action result */
    @DirtiesContext
    @Test
    public void actionResultShouldReturnSuccess() throws Exception {
        // TODO: FIX TEST CASE
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("resultReturnSuccess1",
                this.mockMvc, this.getClass().getName());

        // Obtain their user ID
        String user = TestHelpers.getUserFromString("resultReturnSuccess1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("resultReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("resultReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Get the player and force a certain role onto them
        Player player1 = UserManager.getInstance().getUser(Integer.parseInt(user)).getPlayer();

        // Obtain the other user and player
        user = TestHelpers.getUserFromString("resultReturnSuccess2", this.mockMvc,
                this.getClass().getName(), this.objectMapper);
        Player player2 = UserManager.getInstance().getUser(Integer.parseInt(user)).getPlayer();

        // Force player 1 to be ClairVoyant and player 2 to be Werewolf (to prevent the winhandler from triggering)
        player1.setMainRole(new Clairvoyant());
        player2.setMainRole(new Werewolf());

        // Fix the state
        IInstance instance = IM.getInstance(Integer.parseInt(gameId));
        InstanceState state = instance.getInstanceState();
        state.setPhase(DayPhase.EXECUTION);
        if (state.getDay() != 1) {
            state.setDay(1);
        }
        instance.nextPhase();

        this.mockMvc.perform(get("/api/game/" + gameId + "/purpose/action_result")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "resultReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Messages retrieved successfully"))
                .andExpect(jsonPath("$.results[0]").doesNotExist());

        ArrayList<PlayerIdentifier> player = new ArrayList<>();
        player.add(new PlayerIdentifier(Integer.parseInt(gameId), Integer.parseInt(user)));
        player1.performAction(new ActionEnc(new ArrayList<Integer>(), player), Action.CLAIRVOYANT_SEE_ROLE);

        instance.nextPhase();
        instance.nextPhase();
        instance.nextPhase();

        this.mockMvc.perform(get("/api/game/" + gameId + "/purpose/action_result")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "resultReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Messages retrieved successfully"))
                .andExpect(jsonPath("$.actions[0].messageType").value(ActionMessages.SEE_ROLE_MESSAGE.toString()))
                .andExpect(jsonPath("$.actions[0].data").isNotEmpty());
    }

    /** @utp.description Tests NoPermissionException if user not in the specified game */
    @DirtiesContext
    @Test
    public void actionResultShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("resultReturnFailure1",
                this.mockMvc, this.getClass().getName());


        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        TestHelpers.joinGameOK("resultReturnFailure1Dummy", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("resultReturnFailure1", this.mockMvc, this.getClass().getName());

        // Simple second game creation
        game = TestHelpers.createGameOK("resultReturnFailure1Extra",
                this.mockMvc, this.getClass().getName());


        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId2 = json.get("gameId").asText();
        // Make gameId into an input
        map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId2);

        TestHelpers.joinGameOK("resultReturnFailure2Extra", this.mockMvc, this.getClass().getName(), map);
        TestHelpers.startGameOK("resultReturnFailure1Extra", this.mockMvc, this.getClass().getName());

        this.mockMvc.perform(get("/api/game/" + gameId2 + "/purpose/action_result")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "resultReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("You cannot perform this command!"));
    }

    /** @utp.description Tests NoPermissionException if the game has not yet started */
    @DirtiesContext
    @Test
    public void actionResultShouldReturnFailure2() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("resultReturnFailure2",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        this.mockMvc.perform(get("/api/game/" + gameId + "/purpose/action_result")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "resultReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The game has not yet started"));
    }
}
