package com.SAMUDRA.messaging_system.DTO;

import com.SAMUDRA.messaging_system.enums.ChatRole;

public class ParticipantResponse {

    private Long userId;
    private ChatRole role;

    public ParticipantResponse(Long userId, ChatRole role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public ChatRole getRole() {
        return role;
    }
}