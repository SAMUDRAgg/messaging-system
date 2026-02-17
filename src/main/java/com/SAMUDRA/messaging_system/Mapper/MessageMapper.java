package com.SAMUDRA.messaging_system.Mapper;

import com.SAMUDRA.messaging_system.DAO.ChatMessage;
import com.SAMUDRA.messaging_system.DTO.MessageResponse;
import com.SAMUDRA.messaging_system.DTO.SenderInfo;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponse mapToResponse(ChatMessage message) {
        return new MessageResponse(
                message.getMessageId(),
                message.getChat().getChatId(),
                new SenderInfo(
                        message.getSenderId(),
                        message.getSenderUsername(),
                        null
                ),
                message.getContent(),
                null,
                null,
                message.isEdited(),
                message.isDeleted(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}