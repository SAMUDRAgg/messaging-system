package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.ChatMessage;
import com.SAMUDRA.messaging_system.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends JpaRepository<ChatMessage, Long> {

    /* =========================================================
       1️⃣ GET MESSAGES (Paginated)
    ========================================================= */

    Page<ChatMessage> findByChat_ChatIdOrderByCreatedAtDesc(
            Long chatId,
            Pageable pageable
    );

    /* =========================================================
       2️⃣ COUNT UNREAD MESSAGES
       (Assuming UNREAD = status != READ AND not deleted)
    ========================================================= */

    @Query("""
           SELECT COUNT(m)
           FROM ChatMessage m
           WHERE m.chat.chatId = :chatId
           AND m.senderId <> :userId
           AND m.messageStatus <> com.SAMUDRA.messaging_system.enums.MessageStatus.READ
           AND m.deleted = false
           """)
    long countUnreadMessages(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );

    /* =========================================================
       3️⃣ MARK MESSAGES AS READ
    ========================================================= */

    @Modifying
    @Query("""
           UPDATE ChatMessage m
           SET m.messageStatus = com.SAMUDRA.messaging_system.enums.MessageStatus.READ
           WHERE m.chat.chatId = :chatId
           AND m.senderId <> :userId
           AND m.messageStatus <> com.SAMUDRA.messaging_system.enums.MessageStatus.READ
           """)
    void markMessagesAsRead(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );

    /* =========================================================
       4️⃣ EXISTS CHECK (Already from JpaRepository)
    ========================================================= */

    // existsById(Long id) → already available
}