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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.lukos.controller.ControllerApplication;
import org.lukos.controller.GeneralController;
import org.lukos.controller.request.ProfileData;
import org.lukos.controller.request.SubmitProfileUpdate;
import org.lukos.controller.util.TestHelpers;
import org.lukos.controller.websocket.WebSocketConfig;
import org.lukos.model.user.IssuerSub;
import org.lukos.model.user.User;
import org.lukos.model.user.UserManager;
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
 * Test cases for {@code BasicUserController}.
 *
 * @author Marco Pleket (1295713)
 * @since 12-03-2022
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers = {GeneralController.class})
public class GameUserControllerTests {
    /**
     * Necessary for including contentType() and preventing 415 HTTP error
     */
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
     * The context of this particular app that the mock controller is setup with
     */
    @Autowired
    WebApplicationContext webAppContext;

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

    // TODO: fix test cases

    /**
     * @utp.description Tests success message on successful user-info request
     */
    @Test
    @DirtiesContext
    public void currentShouldReturnSuccess1() throws Exception {
        MvcResult game = TestHelpers.createGameOK("currentReturnSuccess1",
                this.mockMvc, this.getClass().getName());
        // Obtain the response to extract the gameID
        JsonNode json =
                objectMapper.readTree(game.getResponse().getContentAsString());

        this.mockMvc.perform(get("/api/user/current")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "currentReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuer").value(this.getClass().getName()))
                .andExpect(jsonPath("$.sub").value("currentReturnSuccess1"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.gameId").value(Integer.parseInt(json.get("gameId").asText())))
                .andExpect(jsonPath("$.name").value("testUser"));
    }

    /**
     * @utp.description Tests success message if user not in a game
     */
    @Test
    @DirtiesContext
    public void currentShouldReturnSuccess2() throws Exception {
        this.mockMvc.perform(get("/api/user/current")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "currentReturnSuccess2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuer").value(this.getClass().getName()))
                .andExpect(jsonPath("$.sub").value("currentReturnSuccess2"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("testUser"));
    }

    /**
     * @utp.description Tests success message on account deletion
     */
    @Test
    @DirtiesContext
    public void deleteAccountShouldReturnSuccess() throws Exception {
        User newUser = UserManager.getInstance()
                .createUser(new IssuerSub(this.getClass().getName(), "deleteReturnSuccess"), "testUser");

        this.mockMvc.perform(post("/api/user/gdpr/delete_account").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "deleteReturnSuccess")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User removed successfully"));
    }

    // /** @utp.description Tests failure message if logout unsuccessful */
    // @Test
    // public void deleteAccountShouldReturnFailure3() throws Exception {
    //
    // }
    //

    /**
     * @utp.description Tests success message on profile data update
     */
    @Test
    @DirtiesContext
    public void updateUserShouldReturnSuccess1() throws Exception {
        User newUser = UserManager.getInstance()
                .createUser(new IssuerSub(this.getClass().getName(), "updateReturnSuccess1"), "testUser");

        SubmitProfileUpdate update = new SubmitProfileUpdate();
        update.setDataKey(ProfileData.USERNAME);
        update.setData("Harry");

        String requestJson = TestHelpers.convertJson(update);

        this.mockMvc.perform(post("/api/user/gdpr/update_user_info").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "updateReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Username was successfully updated, but user is not in a game."));
    }

    /**
     * @utp.description Tests success message on profile data update
     */
    @Test
    @DirtiesContext
    public void updateUserShouldReturnSuccess2() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("updateReturnSuccess2", this.mockMvc, this.getClass().getName());

        // Obtain the response to extract the gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());

        // Create input for the call to the join endpoint, specifying the game to join
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
        vars.add("gameId", json.get("gameId").asText());

        // Join the game as a separate user and start it
        TestHelpers.joinGameOK("updateReturnSuccess2Dummy", this.mockMvc, this.getClass().getName(), vars);
        TestHelpers.startGameOK("updateReturnSuccess2", this.mockMvc, this.getClass().getName());

        SubmitProfileUpdate update = new SubmitProfileUpdate();
        update.setDataKey(ProfileData.USERNAME);
        update.setData("Harry");

        String requestJson = TestHelpers.convertJson(update);

        this.mockMvc.perform(post("/api/user/gdpr/update_user_info").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "updateReturnSuccess2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Username was successfully updated"));

        // If no data key provided, we also require a successful response
        update = new SubmitProfileUpdate();
        update.setData("Harry");

        requestJson = TestHelpers.convertJson(update);

        this.mockMvc.perform(post("/api/user/gdpr/update_user_info").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "updateReturnSuccess2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Username was successfully updated"));
    }

    /**
     * @utp.description Tests failure message if user not found
     */
    //@Test
    @DirtiesContext
    public void updateUserShouldReturnFailure1() throws Exception {

    }

    /**
     * @utp.description Tests success message for download user info request
     */
    //@Test
    public void downloadUserInfoShouldReturnSuccess() throws Exception {

    }

    /**
     * @utp.description Tests failure message if user not found
     */
    //@Test
    public void downloadUserInfoShouldReturnFailure1() throws Exception {

    }
}
