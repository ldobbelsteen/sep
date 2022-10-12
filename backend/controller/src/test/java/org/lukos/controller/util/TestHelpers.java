package org.lukos.controller.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.lukos.controller.request.GameCreateRequest;
import org.lukos.controller.request.GameJoinRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;

import java.nio.charset.Charset;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestHelpers {
    /** Necessary for including contentType() and preventing 415 HTTP error */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /** OK HTTP request for creating a game */
    public static MvcResult createGameOK(String sub, MockMvc mockMvc, String className)
            throws Exception {
        GameCreateRequest vars = new GameCreateRequest();
        vars.setGameName(sub.substring(Math.max(sub.length() - 24, 0)));
        vars.setMaxAmountOfPlayers(2);
        vars.setSEED(2);

        // https://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
        String requestJson = convertJson(vars);

        return mockMvc.perform(post("/api/lobby/create").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", sub)
                                        .claim("iss", className)
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andReturn();
    }

    /** OK HTTP request for joining a game */
    public static MvcResult joinGameOK(String sub, MockMvc mockMvc, String className,
                                       MultiValueMap<String, String> vars) throws Exception {
        GameJoinRequest cont = new GameJoinRequest();
        cont.setJoinCode(Integer.valueOf(vars.getFirst("gameId")));

        // https://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(cont);

        return mockMvc.perform(post("/api/lobby/join").with(csrf())
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", sub)
                                        .claim("iss", className)
                                        .claim("name", "testUser")
                                )))
                .andExpect(status().isOk())
                .andReturn();
    }

    /** OK HTTP request for starting a game */
    public static MvcResult startGameOK(String sub, MockMvc mockMvc, String className) throws Exception {
        return mockMvc.perform(post("/api/lobby/start").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", sub)
                                        .claim("iss", className)
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    /** OK HTTP request for getting the current user */
    public static MvcResult getCurrentUser(String sub, MockMvc mockMvc, String className) throws Exception {
        return mockMvc.perform(get("/api/user/current").with(csrf())
                        .with(oidcLogin()
                                .idToken(token -> token.claim("sub", sub)
                                        .claim("iss", className)
                                        .claim("name", "testUser")
                                )))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    /** Get user from sub attribute */
    public static String getUserFromString(String sub, MockMvc mockMvc, String className, ObjectMapper om) throws Exception {
        MvcResult user = getCurrentUser(sub, mockMvc, className);
        return om.readTree(user.getResponse().getContentAsString()).get("id").asText();
    }

    public static String convertJson(Object vars) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(vars);
    }
}
