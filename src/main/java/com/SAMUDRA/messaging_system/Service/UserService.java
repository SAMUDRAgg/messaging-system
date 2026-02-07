package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.Exception.UserException;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ------------------ REGISTER ------------------
    public User addUser(User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new UserException("Username already exists", HttpStatus.CONFLICT);
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            throw new UserException("Email already exists", HttpStatus.CONFLICT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        return userRepo.save(user);
    }

    // ------------------ FIND BY ID (JWT FLOW) ------------------
    public User findById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserException("User not found with id: " + userId, HttpStatus.NOT_FOUND)
                );
    }

    // ------------------ FIND BY USERNAME OR EMAIL (LOGIN) ------------------
    public User findByUsernameOrEmail(String identifier) {
        return userRepo.findByUsernameOrEmail(identifier)
                .orElseThrow(() ->
                        new UserException(
                                "User not found with username/email: " + identifier,
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    // ------------------ UPDATE USER BY ID ------------------
    public User updateUserById(Long userId, User updatedUser) {

        User user = findById(userId);

        // Update username
        if (updatedUser.getUsername() != null &&
                !updatedUser.getUsername().isBlank() &&
                !updatedUser.getUsername().equals(user.getUsername())) {

            if (userRepo.existsByUsername(updatedUser.getUsername())) {
                throw new UserException("Username already taken", HttpStatus.CONFLICT);
            }
            user.setUsername(updatedUser.getUsername());
        }

        // Update email
        if (updatedUser.getEmail() != null &&
                !updatedUser.getEmail().isBlank() &&
                !updatedUser.getEmail().equals(user.getEmail())) {

            if (userRepo.existsByEmail(updatedUser.getEmail())) {
                throw new UserException("Email already taken", HttpStatus.CONFLICT);
            }
            user.setEmail(updatedUser.getEmail());
        }

        // Update password
        if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().isBlank()) {

            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Update profile picture
        if (updatedUser.getProfilePicUrl() != null &&
                !updatedUser.getProfilePicUrl().isBlank()) {

            user.setProfilePicUrl(updatedUser.getProfilePicUrl());
        }

        return userRepo.save(user);
    }

    // ------------------ SEARCH USERS ------------------
    public List<User> searchUser(String query) {

        List<User> users =
                userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);

        if (users.isEmpty()) {
            throw new UserException(
                    "No users found matching query: " + query,
                    HttpStatus.NOT_FOUND
            );
        }

        return users;
    }
}