package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.DTO.LoginRequest;
import com.SAMUDRA.messaging_system.DTO.LoginResponse;
import com.SAMUDRA.messaging_system.DTO.RegisterRequest;
import com.SAMUDRA.messaging_system.DTO.RegisterResponse;
import com.SAMUDRA.messaging_system.Service.JwtService;
import com.SAMUDRA.messaging_system.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    // ------------------ REGISTER ------------------
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // BCrypt should be applied in service
        user.setProfilePicUrl(request.getProfilePicUrl());

        User savedUser = userService.addUser(user);

        RegisterResponse response = new RegisterResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    // ------------------ LOGIN ------------------
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        try {
            String loginIdentifier =
                    (request.getEmail() != null && !request.getEmail().isBlank())
                            ? request.getEmail()
                            : request.getUsername();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginIdentifier,
                            request.getPassword()
                    )
            );

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            User user = principal.getUser();

            String token = jwtService.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );

            LoginResponse response = new LoginResponse(
                    "Login successful",
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            LoginResponse error = new LoginResponse();
            error.setMessage("Invalid credentials");
            return ResponseEntity.status(401).body(error);
        }
    }
}