package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<ChatMessage , Long> {
    public List<ChatMessage> findByChat_ChatId(Long chatId);

}
