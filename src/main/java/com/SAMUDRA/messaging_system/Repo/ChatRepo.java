package com.SAMUDRA.messaging_system.Repo;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long> {
    @Query("""
        SELECT c FROM Chat c
        WHERE c.chatType = com.SAMUDRA.messaging_system.enums.ChatType.ONE_TO_ONE
          AND c.chatId IN (
              SELECT cp.chat.chatId
              FROM ChatParticipant cp
              WHERE cp.user.id IN (:userId1, :userId2)
              GROUP BY cp.chat.chatId
              HAVING COUNT(DISTINCT cp.user.id) = 2
          )
    """)
    Chat findOneToOneChat(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("chatType") ChatType chatType
    );
    @Query("""
        SELECT DISTINCT c
        FROM Chat c
        JOIN ChatParticipant cp ON cp.chat = c
        WHERE cp.user.id = :userId
          AND c.chatStatus = com.SAMUDRA.messaging_system.enums.ChatStatus.ACTIVE
        ORDER BY c.lastMessageAt DESC
    """)
    List<Chat> findAllByUserId(@Param("userId") Long userId);


}
