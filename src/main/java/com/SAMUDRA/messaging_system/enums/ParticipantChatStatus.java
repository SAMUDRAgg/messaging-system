package com.SAMUDRA.messaging_system.enums;


    public enum ParticipantChatStatus {
        ACTIVE, // User is actively participating in the chat
        LEFT,   // User has left the chat (soft delete)
        BANNED,
        ARCHIVED // User is banned from the chat (optional for future use)
    }

