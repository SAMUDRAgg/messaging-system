package com.SAMUDRA.messaging_system.DTO;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.enums.ChatStatus;
import com.SAMUDRA.messaging_system.enums.ChatType;

import java.time.LocalDateTime;
import java.util.List;

public class ChatResponse {

    private Long chatId;
    private ChatType chatType;

    // GROUP only (null for one-to-one)
    private String title;
    private String groupProfilePicUrl;

    // participants
    private List<Long> participantIds;

    private ChatStatus chatStatus;

    // for chat list sorting
    private LocalDateTime lastMessageAt;

    // constructors
    public ChatResponse() {}

    public ChatResponse(
            Long chatId,
            ChatType chatType,
            String title,
            String groupProfilePicUrl,
            List<Long> participantIds,
            ChatStatus chatStatus,
            LocalDateTime lastMessageAt
    ) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.title = title;
        this.groupProfilePicUrl = groupProfilePicUrl;
        this.participantIds = participantIds;
        this.chatStatus = chatStatus;
        this.lastMessageAt = lastMessageAt;
    }

    public ChatResponse(Chat ischatExist) {
    }

    // getters & setters

    public Long getChatId() {
        return chatId;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public String getTitle() {
        return title;
    }

    public String getGroupProfilePicUrl() {
        return groupProfilePicUrl;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public ChatStatus getChatStatus() {
        return chatStatus;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }
}