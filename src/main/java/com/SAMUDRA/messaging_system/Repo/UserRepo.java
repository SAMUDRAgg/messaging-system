package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,String> {
    Optional<User> findByUsername(String username);
}
