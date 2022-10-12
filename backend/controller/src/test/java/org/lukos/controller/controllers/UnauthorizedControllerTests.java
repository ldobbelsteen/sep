package org.lukos.controller.controllers;

import static org.lukos.model.user.UserManager.getInstance;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.lukos.controller.*;
import org.lukos.model.instances.InstanceManager;
import org.lukos.model.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


///**
// * Test cases for {@code GameGeneralController}.
// *
// * @author Marco Pleket (1295713)
// * @since 18-03-2022
// */
//@ContextConfiguration(classes = ControllerApplication.class)
//@WebMvcTest(controllers={GeneralController.class})
//public class UnauthorizedControllerTests {
//    /**
//     * The mock controller that mimics the server
//     */
//    @Autowired
//    private MockMvc mockMvc;
//
//    /**
//     * objectMapper used to interpret JSON strings
//     */
//    final private ObjectMapper objectMapper = new ObjectMapper();
//
//    /**
//     * UserManager object to edit user states
//     */
//    UserManager UM = getInstance();
//
//    /**
//     * The context of this particular app that the mock controller is setup with
//     */
//    @Autowired
//    WebApplicationContext webAppContext;
//
//    /**
//     * We can't avoid influencing the game directly here, so we use an InstanceManager.
//     */
//    private static final InstanceManager IM = InstanceManager.getInstanceManager();
//
//    /**
//     * Before each test, configure the mock controller with the app and security context
//     */
//    @BeforeEach
//    void setupPrincipal() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(webAppContext)
//                .apply(springSecurity())
//                .build();
//    }
//
//    // We mock the OAuth2User through a method explained here:
//    // https://github.com/spring-projects/spring-security/issues/8459
//
//    /** @utp.description Tests error-404 */
//    @Test
//    @Disabled
//    @DirtiesContext
//    public void error404() throws Exception {
//
//    }
//
//    /** @utp.description Tests error-401
//     * @FIXME: Can't recreate!
//     */
//    //@Test
//    @DirtiesContext
//    public void error401() throws Exception {
//
//    }
//
//    /** @utp.description Tests error-403 */
//    @Test
//    @Disabled
//    @DirtiesContext
//    public void error403() throws Exception {
//
//    }
//
//    /** @utp.description Tests error-500 */
//    @Test
//    @Disabled
//    @DirtiesContext
//    public void error500() throws Exception {
//
//    }
//}
