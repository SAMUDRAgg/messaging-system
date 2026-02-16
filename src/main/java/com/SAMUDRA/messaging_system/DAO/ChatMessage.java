package com.SAMUDRA.messaging_system.DAO;

import com.SAMUDRA.messaging_system.enums.MessageStatus;
import com.SAMUDRA.messaging_system.enums.MessageType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(name = "idx_chat_id", columnList = "chat_id"),
                @Index(name = "idx_sender_id", columnList = "sender_id"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    /* ================= CHAT RELATION ================= */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    /* ================= SENDER ================= */

    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    // Keeping senderId for performance (no join needed)
    @Column(name = "sender_username", nullable = false)
    private String senderUsername;
    /* ================= CONTENT ================= */

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus messageStatus;

    /* ================= FLAGS ================= */

    @Column(nullable = false)
    private boolean edited = false;

    @Column(nullable = false)
    private boolean deleted = false;

    /* ================= TIMESTAMPS ================= */

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ================= LIFECYCLE ================= */

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.messageStatus = MessageStatus.SENT;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ================= GETTERS & SETTERS ================= */

    public Long getMessageId() {
        return messageId;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return deleted ? "This message was deleted" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setSender(User sender) {
        
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.updatedAt = now;
    }
}