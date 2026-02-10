package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.DAO.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface ChatParticipantRepo extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChat(Chat chat);
}
