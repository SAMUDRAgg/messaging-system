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

    // ------------------ Register ------------------
    public User addUser(User user) {
        boolean exists = userRepo.findByUsername(user.getUsername()).isPresent() ||
                userRepo.findByEmail(user.getEmail()).isPresent();

        if (exists) {
            throw new UserException("Username or email already exists", HttpStatus.CONFLICT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        return userRepo.save(user);
    }

    // ------------------ Find by username or email ------------------
    public User findByUsernameOrEmail(String identifier) {
        return userRepo.findByUsername(identifier)
                .or(() -> userRepo.findByEmail(identifier))
                .orElseThrow(() -> new UserException(
                        "User not found with username/email: " + identifier,
                        HttpStatus.NOT_FOUND
                ));
    }

    // ------------------ Login and Generate JWT ------------------
    public String login(String identifier, String password, JwtService jwtService) {
        User user = findByUsernameOrEmail(identifier);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException("Invalid password", HttpStatus.UNAUTHORIZED);
        }

        if (!user.isEnabled()) {
            throw new UserException("User account is disabled", HttpStatus.UNAUTHORIZED);
        }

        // ✅ Call your updated generateToken()
        return jwtService.generateToken(user.getId(), user.getUsername(), user.getEmail());
    }

    // ------------------ Update User ------------------
    public User updateUser(String identifier, User updatedUser) {
        User user = findByUsernameOrEmail(identifier);

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isBlank())
            user.setUsername(updatedUser.getUsername());

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank())
            user.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        if (updatedUser.getProfilePicUrl() != null && !updatedUser.getProfilePicUrl().isBlank())
            user.setProfilePicUrl(updatedUser.getProfilePicUrl());

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
