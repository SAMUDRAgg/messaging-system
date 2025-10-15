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

@RestController

public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    // ------------------ User Registration ------------------
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        User savedUser = userService.addUser(user); // throws UserException if user exists

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        return ResponseEntity.ok(response);
    }

    // ------------------ User Login (username or email) ------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User user) {
        try {
            String loginIdentifier = (user.getEmail() != null && !user.getEmail().isBlank())
                    ? user.getEmail()
                    : user.getUsername();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginIdentifier,
                            user.getPassword()
                    )
            );

            String token = jwtService.generateToken(loginIdentifier);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }
}
