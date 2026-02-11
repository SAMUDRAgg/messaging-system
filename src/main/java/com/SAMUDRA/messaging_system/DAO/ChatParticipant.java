package com.SAMUDRA.messaging_system.DAO;

import com.SAMUDRA.messaging_system.enums.ChatRole;
import com.SAMUDRA.messaging_system.enums.ParticipantChatStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_participants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "user_id"})
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

    // 👑 ADMIN / MEMBER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRole role;

    // 📌 Participant-specific chat state
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantChatStatus status = ParticipantChatStatus.ACTIVE;

    private LocalDateTime joinedAt;

    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }

    // Constructors
    public ChatParticipant() {}

    public ChatParticipant(Chat chat, User user, ChatRole role) {
        this.chat = chat;
        this.user = user;
        this.role = role;
    }

    // Getters & Setters

    public Long getId() { return id; }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ChatRole getRole() { return role; }
    public void setRole(ChatRole role) { this.role = role; }

    public ParticipantChatStatus getStatus() { return status; }
    public void setStatus(ParticipantChatStatus status) { this.status = status; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
}