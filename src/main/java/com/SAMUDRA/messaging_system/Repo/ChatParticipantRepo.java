package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.ChatParticipant;
import com.SAMUDRA.messaging_system.enums.ChatRole;
import com.SAMUDRA.messaging_system.enums.ChatStatus;
import com.SAMUDRA.messaging_system.enums.ParticipantChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepo extends JpaRepository<ChatParticipant, Long> {

    // 🔹 Get ACTIVE participants of a chat (exclude LEFT users)
    List<ChatParticipant> findByChatChatIdAndStatus(
            Long chatId,
            ParticipantChatStatus status
    );

    // 🔹 Get all chat memberships of a user
    List<ChatParticipant> findByUserId(Long userId);

    // 🔹 Get only ACTIVE chats of a user (important for chat list)
    List<ChatParticipant> findByUserIdAndStatus(
            Long userId,
            ParticipantChatStatus status
    );

    // 🔹 Check membership existence
    boolean existsByChatChatIdAndUserId(Long chatId, Long userId);

    // 🔹 Fetch specific participant
    Optional<ChatParticipant> findByChatChatIdAndUserId(
            Long chatId,
            Long userId
    );

    // 🔹 Count admins (only ACTIVE admins)
    long countByChatChatIdAndRoleAndStatus(
            Long chatId,
            ChatRole role,
            ChatStatus status
    );

    // 🔹 Direct delete (rarely used in production; prefer status update)
    void deleteByChatChatIdAndUserId(Long chatId, Long userId);
}