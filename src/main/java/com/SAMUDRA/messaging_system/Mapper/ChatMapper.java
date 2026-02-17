package com.SAMUDRA.messaging_system.Mapper;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.DTO.ChatResponse;
import com.SAMUDRA.messaging_system.DTO.ParticipantResponse;
import com.SAMUDRA.messaging_system.Repo.ChatParticipantRepo;
import com.SAMUDRA.messaging_system.enums.ParticipantChatStatus;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ChatMapper {


    private ChatParticipantRepo chatParticipantRepo;


    public ChatResponse mapToChatResponse(Chat chat) {


        List<ParticipantResponse> participants =
                chatParticipantRepo.findByChatChatIdAndStatus(chat.getChatId(), ParticipantChatStatus.ACTIVE)
                        .stream()
                        .map(cp -> new ParticipantResponse(
                                cp.getUser().getId(),
                                cp.getRole()
                        ))
                        .toList();

        return new ChatResponse(
                chat.getChatId(),
                chat.getChatType(),
                chat.getTitle(),
                chat.getGroupProfilePicUrl(),
                participants.stream().map(ParticipantResponse::getUserId).toList(),
                chat.getChatStatus(),
                chat.getLastMessageAt()
        );
    }
}
