package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.DTO.UserProfileResponse;
import com.SAMUDRA.messaging_system.DTO.UserUpdateRequest;
import com.SAMUDRA.messaging_system.Mapper.UserMapper;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import com.SAMUDRA.messaging_system.Service.MyUserDetailsService;
import com.SAMUDRA.messaging_system.Service.UserService;
import com.SAMUDRA.messaging_system.Service.JwtService; // ✅ IMPORTANT
import com.SAMUDRA.messaging_system.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)

class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private MyUserDetailsService myUserDetailsService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtService jwtService; // ✅ FIX

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ GET PROFILE TEST
    @Test
    void getProfile_shouldReturnUserProfile() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("ram");
        user.setEmail("ram@gmail.com");
        user.setRole(Role.USER); // ✅ FIX (MANDATORY)

        UserProfileResponse response =
                new UserProfileResponse(1L, "ram", "ram@gmail.com", "pic.jpg", "USER");

        when(userMapper.mapToProfile(any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/users/profile")
                        .with(user(new UserPrincipal(user)))) // ✅ now safe
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ram"))
                .andExpect(jsonPath("$.email").value("ram@gmail.com"));
    }

    // ✅ UPDATE PROFILE TEST
    @Test
    void updateProfile_shouldUpdateUserProfile() throws Exception {


        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER); // ✅ important

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("newRam");
        request.setEmail("new@gmail.com");
        request.setPassword("123");
        request.setProfilePicUrl("pic.jpg");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("newRam");
        updatedUser.setEmail("new@gmail.com");
        updatedUser.setRole(Role.USER);

        UserProfileResponse response =
                new UserProfileResponse(1L, "newRam", "new@gmail.com", "pic.jpg", "USER");

        when(userService.updateUserById(eq(1L), any(User.class)))
                .thenReturn(updatedUser);

        when(userMapper.mapToProfile(updatedUser)).thenReturn(response);

        mockMvc.perform(put("/api/users/profile")
                        .with(user(new UserPrincipal(user))) // ✅ auth
                        .with(csrf())                        // ✅ FIX HERE
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newRam"))
                .andExpect(jsonPath("$.email").value("new@gmail.com"));
    }

    // ✅ SEARCH TEST
    @Test
    void searchUsers_shouldReturnUsers() throws Exception {


        User user = new User();
        user.setId(1L);
        user.setUsername("ram");
        user.setProfilePicUrl("pic.jpg");

        when(userService.searchUser("ram")).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/search")
                        .param("query", "ram")
                        .with(user("testUser"))) // ✅ IMPORTANT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("ram"))
                .andExpect(jsonPath("$[0].profilePicUrl").value("pic.jpg"));
    }
}