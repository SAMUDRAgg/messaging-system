package com.SAMUDRA.messaging_system.DAO;

import com.SAMUDRA.messaging_system.enums.ChatStatus;
import com.SAMUDRA.messaging_system.enums.ChatType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @Column(nullable = false)
    private Long createdBy;

    // chat participants (user IDs)
    @ElementCollection
    @CollectionTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id")
    )
    @Column(name = "user_id", nullable = false)
    private List<Long> participantIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatType chatType;   // ONE_TO_ONE / GROUP

    // group name (null for one-to-one)
    private String title;

    // group profile pic (null for one-to-one)
    private String groupProfilePicUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus chatStatus = ChatStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastMessageAt = LocalDateTime.now();
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupProfilePicUrl() {
        return groupProfilePicUrl;
    }

    public void setGroupProfilePicUrl(String groupProfilePicUrl) {
        this.groupProfilePicUrl = groupProfilePicUrl;
    }

    public ChatStatus getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(ChatStatus chatStatus) {
        this.chatStatus = chatStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    // getters & setters
}