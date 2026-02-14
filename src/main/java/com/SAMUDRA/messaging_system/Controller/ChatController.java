package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.DTO.ChatResponse;
import com.SAMUDRA.messaging_system.Service.ChatService;
import com.SAMUDRA.messaging_system.Service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private ChatService chatService;
    private UserService  userService;
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }
    @PostMapping("/one-to-one/{targetUserId}")
    public ResponseEntity<ChatResponse> createOneToOneChat(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        // 🔐 Get logged-in user (never trust request body for creator)
        Long currentUserId = principal.getId();

        ChatResponse response = chatService
                .createOneToOneChat(currentUserId, targetUserId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
