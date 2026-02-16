package com.SAMUDRA.messaging_system.DTO;

import java.time.LocalDateTime;
import java.util.Map;

public class MessageResponse {

    private Long messageId;
    private Long chatId;

    private SenderInfo sender;

    private String content;
    private String attachmentUrl;
    private String attachmentType;

    private boolean edited;
    private boolean deleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Map<String, Integer> reactions;
    // Example: { "❤️": 3, "🔥": 1 }

    public MessageResponse() {}

    public MessageResponse(
            Long messageId,
            Long chatId,
            SenderInfo sender,
            String content,
            String attachmentUrl,
            String attachmentType,
            boolean edited,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Map<String, Integer> reactions
    ) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.sender = sender;
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.attachmentType = attachmentType;
        this.edited = edited;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reactions = reactions;
    }

    // getters only (immutable response style)

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public SenderInfo getSender() {
        return sender;
    }

    public void setSender(SenderInfo sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Integer> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, Integer> reactions) {
        this.reactions = reactions;
    }
}