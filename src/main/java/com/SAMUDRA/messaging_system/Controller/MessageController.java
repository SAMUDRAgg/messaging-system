package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.DTO.MessageRequest;
import com.SAMUDRA.messaging_system.DTO.MessageResponse;

import com.SAMUDRA.messaging_system.Service.MessageService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")

public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // ===============================
    // 1️⃣ Send Message
    // ===============================

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long chatId,
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long senderId = principal.getId();

        MessageResponse response = messageService.sendMessage(
                chatId,
                senderId,
                request
        );

        return ResponseEntity
                .created(URI.create("/api/v1/chats/" + chatId + "/messages/" + response.getMessageId()))
                .body(response);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long currentUserId = principal.getId();

        List<MessageResponse> messages = messageService.getMessages(
                chatId,
                currentUserId,
                page,
                size
        );

        return ResponseEntity.ok(messages);
    }

    @PatchMapping("/{chatId}/messages/{messageId}")
    public ResponseEntity<MessageResponse> editMessage(
            @PathVariable Long chatId,
            @PathVariable Long messageId,
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long senderId = principal.getId();

        MessageResponse response = messageService.editMessage(
                messageId,
                senderId,
                request.getContent()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{chatId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long userId = principal.getId();

        messageService.deleteMessage(messageId, userId);

        return ResponseEntity.noContent().build(); // 204 No Content
    }
    @PatchMapping("/{chatId}/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long userId = principal.getId();

        messageService.markAsRead(chatId, userId);

        return ResponseEntity.noContent().build(); // 204
    }
    @GetMapping("/{chatId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long userId = principal.getId();

        long count = messageService.getUnreadCount(chatId, userId);

        return ResponseEntity.ok(count);
    }
    @PostMapping("/{fromChatId}/messages/{messageId}/forward/{toChatId}")
    public ResponseEntity<Void> forwardMessage(
            @PathVariable Long fromChatId,
            @PathVariable Long messageId,
            @PathVariable Long toChatId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long currentUserId = principal.getId();

        messageService.forwardMessage(
                messageId,
                fromChatId,
                toChatId,
                currentUserId
        );

        return ResponseEntity.ok().build();
    }
}