package com.SAMUDRA.messaging_system.DAO;

import com.SAMUDRA.messaging_system.enums.ChatRole;
import com.SAMUDRA.messaging_system.enums.ParticipantChatStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_user",
                        columnNames = {"chat_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_chat_participant_chat", columnList = "chat_id"),
                @Index(name = "idx_chat_participant_user", columnList = "user_id"),
                @Index(name = "idx_chat_participant_status", columnList = "status")
        }
)
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Chat reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    // 🔗 User reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 👑 Role inside chat
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private ChatRole role;

    // 📌 Per-user chat state
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ParticipantChatStatus status = ParticipantChatStatus.ACTIVE;

    // ⏱ Audit field
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    // 🔐 Optimistic locking (prevents race conditions)
    @Version
    private Long version;

    // =============================
    // Lifecycle Hooks
    // =============================

    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }

    // =============================
    // Constructors
    // =============================

    public ChatParticipant() {}

    public ChatParticipant(Chat chat, User user, ChatRole role) {
        this.chat = chat;
        this.user = user;
        this.role = role;
        this.status = ParticipantChatStatus.ACTIVE;
    }

    // =============================
    // Business Methods
    // =============================

    public void leaveChat() {
        this.status = ParticipantChatStatus.LEFT;
    }

    public void banUser() {
        this.status = ParticipantChatStatus.BANNED;
    }

    public void makeAdmin() {
        this.role = ChatRole.ADMIN;
    }

    public void makeMember() {
        this.role = ChatRole.MEMBER;
    }

    public boolean isActive() {
        return this.status == ParticipantChatStatus.ACTIVE;
    }

    public boolean isAdmin() {
        return this.role == ChatRole.ADMIN;
    }

    // =============================
    // Getters & Setters
    // =============================

    public Long getId() {
        return id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatRole getRole() {
        return role;
    }

    public void setRole(ChatRole role) {
        this.role = role;
    }

    public ParticipantChatStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipantChatStatus status) {
        this.status = status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public Long getVersion() {
        return version;
    }
}