package org.lukos.controller.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukos.controller.ControllerApplication;
import org.lukos.controller.GeneralController;
import org.lukos.controller.util.TestHelpers;
import org.lukos.controller.websocket.WebSocketConfig;
import org.lukos.model.chatsystem.*;
import org.lukos.model.instances.InstanceManager;
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
import java.time.Instant;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test cases for {@code GameChatController}.
 *
 * @author Marco Pleket (1295713)
 * @since 04-07-2022
 */
@ContextConfiguration(classes = {ControllerApplication.class, WebSocketConfig.class})
@WebMvcTest(controllers = { GeneralController.class })
public class GameChatControllerTests {
    /** Necessary for including contentType() and preventing 415 HTTP error */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

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

    /** @utp.description Tests success message for successfully submitting a chat message */
    @Test
    @DirtiesContext
    public void submitChatShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subChatReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Obtain their user id
        String user1 = TestHelpers.getUserFromString("subChatReturnSuccess1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK("subChatReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK("subChatReturnSuccess1", this.mockMvc, this.getClass().getName());

        List<ChatStatus> chats = ChatManager.getInstance().getPlayerChats(Integer.parseInt(user1));

        this.mockMvc.perform(post("/api/game/" + gameId + "/chat/" + chats.get(0).id() + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("This is a test message")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subChatReturnSuccess1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Chat was sent successfully"));
    }

    /** @utp.description Tests NoSuchPlayerException for submitting a chat */
    @Test
    @DirtiesContext
    public void submitChatShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subChatReturnFailure1", this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        this.mockMvc.perform(post("/api/game/" + gameId + "/chat/" + 124897 + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("This is a test message")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subChatReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Chats aren't accessible when the game hasn't started yet"));

        // Simple game creation
        game = TestHelpers.createGameOK("subChatReturnFailure1Dummy", this.mockMvc, this.getClass().getName());

        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        gameId = json.get("gameId").asText();

        this.mockMvc.perform(post("/api/game/" + gameId + "/chat/" + 124897 + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("This is a test message")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subChatReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Player is not in this game, and cannot submit chats."));
    }

    /** @utp.description Tests NoPermissionException for submitting a chat */
    @Test
    @DirtiesContext
    public void submitChatShouldReturnFailure2() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("subChatReturnFailure2", this.mockMvc, this.getClass().getName());

        // Obtain their user id
        String user1 = TestHelpers.getUserFromString("subChatReturnFailure2", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK("subChatReturnFailure2Dummy", this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK("subChatReturnFailure2", this.mockMvc, this.getClass().getName());

        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        gameId = json.get("gameId").asText();

        List<ChatIdentifier> chats = ChatManager.getInstance().getChatIDs(Integer.parseInt(user1));
        int chatID = 0;
        for (ChatIdentifier chat : chats) {
            if (chat.type() == ChatType.DECEASED) {
                chatID = chat.id();
                break;
            }
        }

        this.mockMvc.perform(post("/api/game/" + gameId + "/chat/" + chatID + "/submit")
                        .with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("This is a test message")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "subChatReturnFailure2")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("The player is not allowed to write messages to this chat"));
    }

    /** @utp.description Tests success message for successfully retrieving all chats */
    @Test
    @DirtiesContext
    public void listChatsShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("lChatReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Obtain their user id
        String user1 = TestHelpers.getUserFromString("lChatReturnSuccess1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK("lChatReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK("lChatReturnSuccess1", this.mockMvc, this.getClass().getName());

        List<ChatStatus> chats = ChatManager.getInstance().getPlayerChats(Integer.parseInt(user1));

        for (int i = 0; i < chats.size(); i++) {
            this.mockMvc.perform(get("/api/game/" + gameId + "/chat/list")
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "lChatReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.chats[" + i + "].id").value(chats.get(i).id()))
                    .andExpect(jsonPath("$.chats[" + i + "].type").value(chats.get(i).type().toString()))
                    .andExpect(jsonPath("$.chats[" + i + "].isOpen").value(chats.get(i).isOpen()));
        }
    }

    /** @utp.description Tests NoSuchPlayerException message if user not in game */
    @Test
    @DirtiesContext
    public void listChatsShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("lChatReturnFailure1", this.mockMvc, this.getClass().getName());

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        this.mockMvc.perform(get("/api/game/" + gameId + "/chat/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "lChatReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Chats aren't accessible when the game hasn't started yet"));

        // Simple game creation
        game = TestHelpers.createGameOK("lChatReturnFailure1Dummy", this.mockMvc, this.getClass().getName());

        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        gameId = json.get("gameId").asText();

        this.mockMvc.perform(get("/api/game/" + gameId + "/chat/list")
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "lChatReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Player is not in this game"));

    }

    /** @utp.description Tests success message for successfully retrieving chat history */
    @Test
    @DirtiesContext
    public void historyChatShouldReturnSuccess() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("historyReturnSuccess1", this.mockMvc, this.getClass().getName());

        // Obtain their user id
        String user1 = TestHelpers.getUserFromString("historyReturnSuccess1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();
        // Make gameId into an input
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("gameId", gameId);

        // Join so that the game can be started
        TestHelpers.joinGameOK("historyReturnSuccess2", this.mockMvc, this.getClass().getName(), map);
        // Start the game
        TestHelpers.startGameOK("historyReturnSuccess1", this.mockMvc, this.getClass().getName());

        List<ChatStatus> chats = ChatManager.getInstance().getPlayerChats(Integer.parseInt(user1));

        int id = chats.get(0).id();
        Chat.submitChat(Integer.parseInt(user1), id, "This", Instant.now());
        Thread.sleep(40000);
        Chat.submitChat(Integer.parseInt(user1), id, "is", Instant.now());
        Thread.sleep(10000);
        Chat.submitChat(Integer.parseInt(user1), id, "a", Instant.now());
        Thread.sleep(10000);
        Chat.submitChat(Integer.parseInt(user1), id, "test", Instant.now());

        MultiValueMap<String, String> vars = new LinkedMultiValueMap<>();
        vars.add("delta", "1000");
        vars.add("amount", "10");

        // TODO: When ChatDB history function is fixed, reverse this array
        String[] mess = {"This", "is", "a", "test"};

        for (int i = 0; i < mess.length; i++) {
            this.mockMvc.perform(get("/api/game/" + gameId + "/chat/" + chats.get(0).id() + "/history")
                            .params(vars)
                            .with(oidcLogin()
                                    .idToken(token -> token.claim("sub", "historyReturnSuccess1")
                                            .claim("iss", this.getClass().getName())
                                            .claim("name", "testUser")
                                    )))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.history[" + i + "].chatId").value(id))
                    .andExpect(jsonPath("$.history[" + i + "].message.content").value(mess[i]));
        }
    }

    /** @utp.description Tests NoSuchPlayerException when retrieving chat history */
    @Test
    @DirtiesContext
    public void historyChatShouldReturnFailure1() throws Exception {
        // Simple game creation
        MvcResult game = TestHelpers.createGameOK("historyReturnFailure1", this.mockMvc, this.getClass().getName());

        // Obtain their user id
        String user1 = TestHelpers.getUserFromString("historyReturnFailure1", this.mockMvc,
                this.getClass().getName(), this.objectMapper);

        // Extract gameId
        JsonNode json = objectMapper.readTree(game.getResponse().getContentAsString());
        String gameId = json.get("gameId").asText();

        MultiValueMap<String, String> vars = new LinkedMultiValueMap<>();
        vars.add("delta", "1000");
        vars.add("amount", "10");

        this.mockMvc.perform(get("/api/game/" + gameId + "/chat/" + 431522 + "/history")
                        .params(vars)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "historyReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Chats aren't accessible when the game hasn't started yet"));

        // Simple game creation
        game = TestHelpers.createGameOK("listReturnFailure1Dummy", this.mockMvc, this.getClass().getName());

        // Extract gameId
        json = objectMapper.readTree(game.getResponse().getContentAsString());
        gameId = json.get("gameId").asText();

        this.mockMvc.perform(get("/api/game/" + gameId + "/chat/" + 431523 + "/history")
                        .params(vars)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", "historyReturnFailure1")
                                        .claim("iss", this.getClass().getName())
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Player is not in this game, thus cannot access chat history."));
    }
}
