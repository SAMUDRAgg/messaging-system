package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepo extends JpaRepository<Chat, Long> {
    @Query("""
        SELECT c FROM Chat c
        WHERE c.chatType = :chatType
          AND :userId1 MEMBER OF c.participantIds
          AND :userId2 MEMBER OF c.participantIds
    """)
    Chat findOneToOneChat(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("chatType") ChatType chatType
    );
}
