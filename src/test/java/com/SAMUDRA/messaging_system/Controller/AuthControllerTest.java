package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.DTO.LoginRequest;
import com.SAMUDRA.messaging_system.DTO.RegisterRequest;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import com.SAMUDRA.messaging_system.Service.JwtService;
import com.SAMUDRA.messaging_system.Service.MyUserDetailsService;
import com.SAMUDRA.messaging_system.Service.UserService;
import com.SAMUDRA.messaging_system.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;


import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private UserRepo userRepo;

    // ---------------- REGISTER TEST ----------------

    @Test
    void register_shouldReturnSuccess_whenUserCreated() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("ram");
        request.setEmail("ram@gmail.com");
        request.setPassword("123");
        request.setProfilePicUrl("pic.jpg");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("ram");
        savedUser.setEmail("ram@gmail.com");
        savedUser.setRole(Role.USER);

        when(userService.addUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("ram"))
                .andExpect(jsonPath("$.email").value("ram@gmail.com"));
    }

    // ---------------- LOGIN SUCCESS ----------------

    @Test
    void login_shouldReturnToken_whenCredentialsValid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUsername("ram");
        request.setPassword("123");

        User user = new User();
        user.setId(1L);
        user.setUsername("ram");
        user.setEmail("ram@gmail.com");
        user.setRole(Role.USER);

        UserPrincipal principal = new UserPrincipal(user);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(principal);

        when(jwtService.generateToken(any(), any(), any(), any()))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("ram"))
                .andExpect(jsonPath("$.email").value("ram@gmail.com"));
    }

    // ---------------- LOGIN FAILURE ----------------

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsInvalid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUsername("ram");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void register_shouldFail_whenUsernameExists() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("ram");
        request.setEmail("ram@gmail.com");
        request.setPassword("123");
        request.setProfilePicUrl("pic.jpg");

        when(userService.addUser(any(User.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void login_shouldWorkWithEmail() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("ram@gmail.com");
        request.setPassword("123");

        User user = new User();
        user.setId(1L);
        user.setUsername("ram");
        user.setEmail("ram@gmail.com");
        user.setRole(Role.USER);

        UserPrincipal principal = new UserPrincipal(user);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(principal);

        when(jwtService.generateToken(any(), any(), any(), any()))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.email").value("ram@gmail.com"));
    }
}