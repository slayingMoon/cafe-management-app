package com.example.cafebackend.rest;

import com.example.cafebackend.jwt.JwtFilter;
import com.example.cafebackend.jwt.JwtUtil;
import com.example.cafebackend.model.binding.user.*;
import com.example.cafebackend.model.entity.Role;
import com.example.cafebackend.model.entity.User;
import com.example.cafebackend.repository.UserRepository;
import com.example.cafebackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerIT {
    private static final String TEST_NAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_NUMBER = "0888777666";

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost:";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testSignUp() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName(TEST_NAME);
        registrationRequest.setPassword(TEST_PASSWORD);
        registrationRequest.setEmail(TEST_EMAIL);
        registrationRequest.setContactNumber(TEST_NUMBER);

        mockMvc.perform(post(baseUrl + port + "/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("{\"message\":\"Successful Registration\"}"));

        Optional<User> userOptional = userRepository.findByEmail(TEST_EMAIL);
        assertTrue(userOptional.isPresent());

        User user = userOptional.get();
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        User user = new User();
        user.setName(TEST_NAME);
        user.setPassword(new BCryptPasswordEncoder().encode(TEST_PASSWORD));
        user.setEmail(TEST_EMAIL);
        user.setContactNumber(TEST_NUMBER);
        user.setIsVerified("false");
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        user.setRole(role);
        userRepository.save(user);

        mockMvc.perform(post(baseUrl + port + "/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("{\"message\":\"Wait for admin approval\"}"));

    }

    @Test
    void testLoginAfterAdminApproval() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        User user = new User();
        user.setName(TEST_NAME);
        user.setPassword(new BCryptPasswordEncoder().encode(TEST_PASSWORD));
        user.setEmail(TEST_EMAIL);
        user.setContactNumber(TEST_NUMBER);
        user.setIsVerified("true");
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        user.setRole(role);
        userRepository.save(user);

        mockMvc.perform(post(baseUrl + port + "/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setName("user1");
        user1.setPassword(new BCryptPasswordEncoder().encode("password1"));
        user1.setEmail("user1@example.com");
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        user1.setRole(role);
        user1.setContactNumber("0888222333");
        user1.setIsVerified("false");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("user2");
        user2.setPassword(new BCryptPasswordEncoder().encode("password2"));
        user2.setEmail("user2@example.com");
        user2.setRole(role);
        user2.setContactNumber("0888222111");
        user2.setIsVerified("false");
        userRepository.save(user2);

        mockMvc.perform(get(baseUrl + port + "/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", equalTo(user1.getName())))
                .andExpect(jsonPath("$[0].email", equalTo(user1.getEmail())))
                .andExpect(jsonPath("$[1].name", equalTo(user2.getName())))
                .andExpect(jsonPath("$[1].email", equalTo(user2.getEmail())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCheckToken() throws Exception {
        mockMvc.perform(get(baseUrl + port + "/user/checkToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"message\":\"true\"}"));
    }

    @Test
    void testCheckTokenFailsWhenNoUser() throws Exception {
        mockMvc.perform(get(baseUrl + port + "/user/checkToken"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void when_getOneUser_returnsFirst() throws Exception {
        mockMvc.perform(get("/user/"));
    }

//    @Test
//    @WithMockUser(username = "admin@admin.com", password = "admin", roles = "ADMIN")
//    void testUpdateUser() throws Exception {
//        User admin = new User();
//        admin.setName("admin");
//        admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
//        admin.setEmail("admin@example.com");
//        admin.setRole("ROLE_ADMIN");
//        admin.setContactNumber("0888222111");
//        admin.setIsVerified("true");
//        userRepository.save(admin);
//
//        User user1 = new User();
//        user1.setName("user1");
//        user1.setPassword(new BCryptPasswordEncoder().encode("password1"));
//        user1.setEmail("user1@example.com");
//        user1.setRole("ROLE_USER");
//        user1.setContactNumber("0888222333");
//        user1.setIsVerified("false");
//        userRepository.save(user1);
//
//        Optional<User> found = userRepository.findByEmail("user1@example.com");
//
//        UserUpdateModel userUpdateModel = new UserUpdateModel();
//        userUpdateModel.setId(found.get().getId());
//        userUpdateModel.setStatus("true");
//
//        mockMvc.perform(patch(baseUrl + port + "/user/update")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(userUpdateModel)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User " + user1.getEmail() + " updated successfully"));
//
//
//    }

}
