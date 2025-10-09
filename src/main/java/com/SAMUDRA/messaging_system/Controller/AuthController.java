package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.Service.JwtService;
import com.SAMUDRA.messaging_system.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController

public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    /**
     * User Registration Endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Optional<User> savedUser = userService.addUser(user);

        Map<String, String> response = new HashMap<>();
        if (savedUser.isPresent()) {
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "User already exists");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * User Login Endpoint (supports email or username)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User user) {
        try {
            // ✅ Allow login using username OR email
            String loginIdentifier = (user.getEmail() != null && !user.getEmail().isEmpty())
                    ? user.getEmail()
                    : user.getUsername();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginIdentifier,
                            user.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(loginIdentifier);

                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}
