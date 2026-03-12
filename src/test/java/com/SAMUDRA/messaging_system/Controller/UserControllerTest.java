package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.DTO.UserProfileResponse;
import com.SAMUDRA.messaging_system.DTO.UserUpdateRequest;
import com.SAMUDRA.messaging_system.Mapper.UserMapper;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import com.SAMUDRA.messaging_system.Service.JwtService;
import com.SAMUDRA.messaging_system.Service.MyUserDetailsService;
import com.SAMUDRA.messaging_system.Service.UserService;
import com.SAMUDRA.messaging_system.enums.Role;

import com.SAMUDRA.messaging_system.filter.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;
    @MockBean
    private UserRepo userRepo;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    void getProfile_shouldReturnLoggedInUserProfile() throws Exception {
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("ram");
//        user.setEmail("ram@gmail.com");
//        user.setProfilePicUrl("pic.jpg");
//        user.setRole(Role.USER);
//
//        UserPrincipal principal = new UserPrincipal(user);
//
//        UserProfileResponse response =
//                new UserProfileResponse(1L,"ram","ram@gmail.com","pic.jpg",Role.USER.toString());
//
//        when(userMapper.mapToProfile(user)).thenReturn(response);
//
//        Authentication auth =
//                new UsernamePasswordAuthenticationToken(principal,null,principal.getAuthorities());
//
//        mockMvc.perform(get("/api/users/profile")
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("ram"))
//                .andExpect(jsonPath("$.email").value("ram@gmail.com"))
//                .andExpect(jsonPath("$.profilePicUrl").value("pic.jpg"));
//    }
//@Test
//void updateProfile_shouldUpdateUserProfile() throws Exception {
//
//    UserUpdateRequest request = new UserUpdateRequest();
//    request.setUsername("ram");
//    request.setEmail("ram@gmail.com");
//    request.setPassword("123");
//    request.setProfilePicUrl("pic.jpg");
//
//    User user = new User();
//    user.setId(1L);
//    user.setUsername("ram");
//    user.setEmail("ram@gmail.com");
//    user.setProfilePicUrl("pic.jpg");
//    user.setRole(Role.USER);
//
//    UserPrincipal principal = new UserPrincipal(user);
//
//    UserProfileResponse response =
//            new UserProfileResponse(1L,"ram","ram@gmail.com","pic.jpg","USER");
//
//    when(userService.updateUserById(eq(1L), any(User.class)))
//            .thenReturn(user);
//
//    when(userMapper.mapToProfile(user))
//            .thenReturn(response);
//
//    Authentication auth =
//            new UsernamePasswordAuthenticationToken(
//                    principal,
//                    null,
//                    principal.getAuthorities()
//            );
//
//    mockMvc.perform(put("/api/users/profile")
//                    .with(authentication(auth))
//                    .contentType("application/json")
//                    .characterEncoding("utf-8")
//                    .content(objectMapper.writeValueAsString(request)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.username").value("ram"))
//            .andExpect(jsonPath("$.email").value("ram@gmail.com"))
//            .andExpect(jsonPath("$.profilePicUrl").value("pic.jpg"));
//}


    @Test
    void searchUsers_shouldReturnMatchingUsers() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername("ram");
        user.setProfilePicUrl("pic.jpg");

        when(userService.searchUser("ram")).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/search")
                        .param("query","ram"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("ram"))
                .andExpect(jsonPath("$[0].profilePicUrl").value("pic.jpg"));
    }
}