package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.DTO.UserProfileResponse;
import com.SAMUDRA.messaging_system.DTO.UserSearchResponse;
import com.SAMUDRA.messaging_system.DTO.UserUpdateRequest;
import com.SAMUDRA.messaging_system.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // -------- Get Logged-in User Profile --------
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User user = principal.getUser();
        return ResponseEntity.ok(mapToProfile(user));
    }

    // -------- Update Logged-in User Profile --------
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UserUpdateRequest request
    ) {

        User updatedUser = new User();
        updatedUser.setUsername(request.getUsername());
        updatedUser.setEmail(request.getEmail());
        updatedUser.setPassword(request.getPassword());
        updatedUser.setProfilePicUrl(request.getProfilePicUrl());

        User user = userService.updateUserById(
                principal.getUser().getId(),
                updatedUser
        );

        return ResponseEntity.ok(mapToProfile(user));
    }

    // -------- Search Users --------
    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(
            @RequestParam String query
    ) {

        List<UserSearchResponse> response =
                userService.searchUser(query)
                        .stream()
                        .map(u -> new UserSearchResponse(
                                u.getId(),
                                u.getUsername(),
                                u.getProfilePicUrl()
                        ))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // -------- Mapper --------
    private UserProfileResponse mapToProfile(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePicUrl()
        );
    }
}