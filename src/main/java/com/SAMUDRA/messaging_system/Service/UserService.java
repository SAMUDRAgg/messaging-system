package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // use bean from AppConfig

    /**
     * Register a new user after validating username & email.
     * Password is always encrypted before saving.
     */
    public Optional<User> addUser(User user) {
        boolean userExists = userRepo.findByUsername(user.getUsername()).isPresent() ||
                userRepo.findByEmail(user.getEmail()).isPresent();

        if (userExists) {
            return Optional.empty(); // user already exists
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);
        return Optional.of(savedUser);
    }

    /**
     * Find a user by either username or email.
     */
    public Optional<User> findByUsernameOrEmail(String identifier) {
        Optional<User> byEmail = userRepo.findByEmail(identifier);
        if (byEmail.isPresent()) return byEmail;
        return userRepo.findByUsername(identifier);
    }
}
