package org.lukos.controller.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.lukos.controller.*;
import org.lukos.controller.util.TestHelpers;
import org.lukos.controller.websocket.WebSocketConfig;
import org.lukos.model.instances.DayPhase;
import org.lukos.model.instances.InstanceManager;
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

/**
 * Test cases for {@code GamePhaseController}.
 *
 * @author Marco Pleket (1295713)
 * @since 12-03-2022
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers = { GeneralController.class })
public class GamePhaseControllerTests {
    // TODO: Method bodies for test cases.

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

    /**
     * @utp.description Tests success message for going to the next DayPhase
     */
    @Test
    @DirtiesContext
    public void nextPhaseShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK(">phaseReturnSuccess1",
                this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json =
                objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK(">phaseReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK(">phaseReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Obtain the DayPhase of this instance
        DayPhase phase = IM.getInstance(Integer.parseInt(gameId)).getInstanceState().getPhase();

        // Go to the next phase
        this.mockMvc.perform(get("/api/game/" + gameId + "/phase/next")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", ">phaseReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(""));

        // TODO: Test WebSocket response!
    }
}
