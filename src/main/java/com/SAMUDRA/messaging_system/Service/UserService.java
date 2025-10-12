package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DTO.UpdateUserRequest;
import com.SAMUDRA.messaging_system.Exception.UserException;
import com.SAMUDRA.messaging_system.Repo.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ------------------ Register New User ------------------
    public User addUser(User user) {
        boolean userExists = userRepo.findByUsername(user.getUsername()).isPresent() ||
                userRepo.findByEmail(user.getEmail()).isPresent();

        if (userExists) {
            throw new UserException("Username or email already exists", HttpStatus.CONFLICT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    // ------------------ Find User ------------------
    public User findByUsernameOrEmail(String identifier) {
        return userRepo.findByEmail(identifier)
                .or(() -> userRepo.findByUsername(identifier))
                .orElseThrow(() -> new UserException(
                        "User not found with username or email: " + identifier,
                        HttpStatus.NOT_FOUND
                ));
    }

    // ------------------ Update User ------------------
    public User updateUser(String identifier, UpdateUserRequest request) {
        User user = findByUsernameOrEmail(identifier);

        boolean updated = false;

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
            updated = true;
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
            updated = true;
        }

        if (request.getProfilePicUrl() != null && !request.getProfilePicUrl().isBlank()) {
            user.setProfilePicUrl(request.getProfilePicUrl());
            updated = true;
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            updated = true;
        }

        if (!updated) {
            throw new UserException("No valid fields provided to update", HttpStatus.BAD_REQUEST);
        }

        return userRepo.save(user);
    }

    // ------------------ Search Users ------------------
    public List<User> searchUser(String query) {
        List<User> users = userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);

        if (users.isEmpty()) {
            throw new UserException("No users found matching query: " + query, HttpStatus.NOT_FOUND);
        }

        return users;
    }
}
