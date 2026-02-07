package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Combined search method for username or email
    default Optional<User> findByUsernameOrEmail(String identifier) {
        Optional<User> byEmail = findByEmail(identifier);
        if (byEmail.isPresent()) return byEmail;
        return findByUsername(identifier);
    }

    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
