package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepo extends JpaRepository<Chat, Long> {
}
