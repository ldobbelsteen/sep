package org.lukos.controller.controllers;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.lukos.controller.*;
import org.lukos.controller.util.TestHelpers;
import org.lukos.controller.websocket.WebSocketConfig;
import org.lukos.database.InstanceDB;
import org.lukos.database.UserDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.location.Bridge;
import org.lukos.model.user.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;
import java.util.List;

/**
 * Test cases for {@code GameGeneralController}.
 *
 * @author Marco Pleket (1295713)
 * @since 18-03-2022
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers = { GeneralController.class })
public class GameGeneralControllerTests {
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
     * The context of this particular app that the mock controller is setup with
     */
    @Autowired
    WebApplicationContext webAppContext;

    /**
     * We can't avoid influencing the game directly here, so we use an
     * InstanceManager.
     */
    private static final InstanceManager IM = InstanceManager.getInstanceManager();

    /**
     * Before each test, configure the mock controller with the app and security
     * context
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

    /** @utp.description Tests NoSuchInstanceException if gameId is invalid */
     @Test
     @DirtiesContext
     public void gameIDFailure() throws Exception {
        // Check whether the list is correct when the game has not yet started
        this.mockMvc.perform(get("/api/game/" + 1093257 + "/status")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "gameIDFailure")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("The instance with the given ID does not exist."));
     }

     /** @utp.description Tests success message for successful retrieval game
     status */
     @Test
     @DirtiesContext
     public void getStatusShouldReturnSuccess() throws Exception {
         // Simple game creation
         MvcResult game = TestHelpers.createGameOK("statusReturnSuccess1", this.mockMvc, this.getClass().getName());

         // Obtain their user id
         String user1 = TestHelpers.getUserFromString("statusReturnSuccess1", this.mockMvc,
                 this.getClass().getName(), this.objectMapper);

         // Extract gameId
         JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
         String gameId = json.get("gameId").asText();
         // Make gameId into an input
         MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
         map.add("gameId", gameId);

         // Test the response
         this.mockMvc.perform(get("/api/game/" + gameId + "/status")
                         .with(oidcLogin()
                                 .idToken(token -> token.claim("sub", "statusReturnSuccess1")
                                         .claim("iss", this.getClass().getName())
                                         .claim("name", "testUser")
                                 )))
                 .andDo(print())
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.gameId").value(gameId))
                 .andExpect(jsonPath("$.gameMaster").value(user1))
                 .andExpect(jsonPath("$.started").value("false"))
                 .andExpect(jsonPath("$.gameEntry.dayPhase").doesNotExist())
                 .andExpect(jsonPath("$.gameEntry.day").doesNotExist());

         // Join so that the game can be started
         TestHelpers.joinGameOK("statusReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
         // Start the game
         TestHelpers.startGameOK("statusReturnSuccess1", this.mockMvc, this.getClass().getName());

         // Test the response again
         this.mockMvc.perform(get("/api/game/" + gameId + "/status")
                         .with(oidcLogin()
                                 .idToken(token -> token.claim("sub", "statusReturnSuccess1")
                                         .claim("iss", this.getClass().getName())
                                         .claim("name", "testUser")
                                 )))
                 .andDo(print())
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.gameId").value(gameId))
                 .andExpect(jsonPath("$.gameMaster").value(user1))
                 .andExpect(jsonPath("$.started").value("true"))
                 .andExpect(jsonPath("$.gameEntry.day").isNumber());
     }

    /** @utp.description Tests success message for successful retrieval of bridges
    status */
    @Test
    @DirtiesContext
    public void getBridgesShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("bridgesReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK("bridgesReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK("bridgesReturnSuccess1", this.mockMvc, this.getClass().getName());

        List<Integer> bridges = InstanceManager.getInstanceManager().getInstance(Integer.parseInt(gameId)).getBridges();

        int i = 0;
        for (Integer bridge : bridges) {
            Bridge actualBridge = new Bridge(bridge);
            // Test the response again
            this.mockMvc.perform(get("/api/game/" + gameId + "/bridge")
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "bridgesReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bridgeNames[" + i + "].id").value(bridge))
                    .andExpect(jsonPath("$.bridgeNames[" + i + "].name").value(actualBridge.getName()));
            i++;
        }
    }

    /** @utp.description Tests failure message if player not in the (specified) game or game has not started */
    @Test
    @DirtiesContext
    public void getBridgesShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("bridgesReturnFailure1", this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        // Test the response
        this.mockMvc.perform(get("/api/game/" + gameId + "/status")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "bridgesReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameEntry.dayPhase").doesNotExist())
                .andExpect(jsonPath("$.gameEntry.day").doesNotExist());

        // Simple game creation
        game = TestHelpers.createGameOK("bridgesReturnFailure1Dummy", this.mockMvc, this.getClass().getName());

        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        gameId = json.get("gameId").asText();

        // Test the response
        this.mockMvc.perform(get("/api/game/" + gameId + "/bridge")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "bridgesReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("User is not in a game!"));
    }
}
