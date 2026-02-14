package com.SAMUDRA.messaging_system.DAO;

import com.SAMUDRA.messaging_system.enums.ChatStatus;
import com.SAMUDRA.messaging_system.enums.ChatType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "chats",
        indexes = {
                @Index(name = "idx_chat_type", columnList = "chat_type"),
                @Index(name = "idx_chat_status", columnList = "chat_status"),
                @Index(name = "idx_chat_last_message", columnList = "last_message_at")
        }
)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    // 👤 Creator of the chat
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // 🔹 Chat Type (ONE_TO_ONE / GROUP)
    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false, length = 30)
    private ChatType chatType;

    // 🔹 Group Name (nullable for ONE_TO_ONE)
    @Column(length = 100)
    private String title;

    // 🔹 Group Profile Picture URL
    @Column(name = "group_profile_pic_url", length = 500)
    private String groupProfilePicUrl;

    // 🔹 Global Chat Status
    @Enumerated(EnumType.STRING)
    @Column(name = "chat_status", nullable = false, length = 30)
    private ChatStatus chatStatus = ChatStatus.ACTIVE;

    // 🔹 Participants (Mapped by ChatParticipant)
    @OneToMany(
            mappedBy = "chat",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ChatParticipant> participants = new HashSet<>();

    // 🔹 Auditing Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    // 🔹 Optimistic Locking
    @Version
    private Long version;

    // =============================
    // Lifecycle Hooks
    // =============================

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.lastMessageAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =============================
    // Utility Methods
    // =============================

    public void addParticipant(ChatParticipant participant) {
        participants.add(participant);
        participant.setChat(this);
    }

    public void removeParticipant(ChatParticipant participant) {
        participants.remove(participant);
        participant.setChat(null);
    }

    public void updateLastMessageTime() {
        this.lastMessageAt = LocalDateTime.now();
    }

    // =============================
    // Getters & Setters
    // =============================

    public Long getChatId() {
        return chatId;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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

    public Set<ChatParticipant> getParticipants() {
        return participants;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public Long getVersion() {
        return version;
    }
}