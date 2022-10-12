package org.lukos.controller.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.fail;
import static org.lukos.model.user.UserManager.getInstance;
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
import org.lukos.database.InstanceDB;
import org.lukos.database.UserDB;
import org.lukos.model.exceptions.GameException;
import org.lukos.model.instances.IInstance;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.rolesystem.DoubleRole;
import org.lukos.model.rolesystem.Job;
import org.lukos.model.user.PlayerIdentifier;
import org.lukos.model.user.UserManager;
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

/**
 * Test cases for {@code GameRoleController}.
 *
 * @author Marco Pleket (1295713)
 * @since 12-03-2022
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers={ GeneralController.class })
public class GameRoleControllerTests {
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

    /** @utp.description Tests success message for successful retrieval of role */
    @Test
    @DirtiesContext
    public void getRoleShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("getRoleReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Obtain their user id
        String user1 = TestHelpers.getUserFromString("getRoleReturnSuccess1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK("getRoleReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK("getRoleReturnSuccess1", this.mockMvc, this.getClass().getName());

        Player player = UM.getUser(Integer.parseInt(user1)).getPlayer();

        this.mockMvc.perform(get("/api/game/" + gameId + "/role")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "getRoleReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerRoles[0].group").value(player.getMainRole().getGroup().toString()))
                .andExpect(jsonPath("$.playerRoles[0].name").value(player.getMainRole().getClass().getSimpleName()));

        // Use i to keep track of the index
        int i = 1;
        // Loop over all the double roles
        for (DoubleRole role : player.getDoubleRoles()) {
            this.mockMvc.perform(get("/api/game/" + gameId + "/role")
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "getRoleReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.playerRoles[" + i + "].group").value(role.getGroup().toString()))
                    .andExpect(jsonPath("$.playerRoles[" + i + "].name").value(role.getClass().getSimpleName()));
            i++;
        }

        // Loop over all the jobs
        for (Job job : player.getJobs()) {
            this.mockMvc.perform(get("/api/game/" + gameId + "/role")
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "getRoleReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.playerRoles[" + i + "].group").doesNotExist())
                    .andExpect(jsonPath("$.playerRoles[" + i + "].name").value(job.getClass().getSimpleName()));
            i++;
        }
    }

    /** @utp.description Tests success message if player not in started game */
    @Test
    @DirtiesContext
    public void getRoleShouldReturnSuccess2() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("getRoleReturnSuccess3", this.mockMvc, this.getClass().getName());

        // Obtain their user id
        String user1 = TestHelpers.getUserFromString("getRoleReturnSuccess3", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        // Try to get roles
        this.mockMvc.perform(get("/api/game/" + gameId + "/role")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "getRoleReturnSuccess3")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.playerRoles[0].id").doesNotExist())
                .andExpect(jsonPath("$.playerRoles[0].group").doesNotExist());
    }

    /** @utp.description Tests failure message if player not in game */
    @Test
    @DirtiesContext
    public void getRoleShouldReturnFailure() throws Exception {
        MvcResult game = TestHelpers.createGameOK("getRoleReturnFailure1", this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK("getRoleReturnFailure2", this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK("getRoleReturnFailure1", this.mockMvc, this.getClass().getName());

        this.mockMvc.perform(get("/api/game/" + 50000 + "/role")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "getRoleReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("User attempted to access DayPhase for a game they are not participating in."));
    }
}
