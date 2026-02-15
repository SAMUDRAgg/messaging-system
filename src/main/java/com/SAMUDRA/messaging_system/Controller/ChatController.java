package com.SAMUDRA.messaging_system.Controller;

import com.SAMUDRA.messaging_system.DAO.UserPrincipal;
import com.SAMUDRA.messaging_system.DTO.ChatResponse;
import com.SAMUDRA.messaging_system.DTO.CreateGroupChatRequest;
import com.SAMUDRA.messaging_system.Service.ChatService;
import com.SAMUDRA.messaging_system.Service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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

        // 🔐 Get logged-in user ID from the security context
        Long currentUserId = principal.getId();

        ChatResponse response = chatService
                .createOneToOneChat(currentUserId, targetUserId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatById(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        // 🔐 Get logged-in user ID from the security context
        Long currentUserId = principal.getId();

        ChatResponse response = chatService
                .getChatById(chatId , currentUserId);

        return ResponseEntity
                .ok(response);
    }
    @GetMapping("/my-chats")
    public ResponseEntity<List<ChatResponse>> getAllChatsByUserId(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        // 🔐 Get logged-in user ID from the security context
        Long currentUserId = principal.getId();

        List<ChatResponse> response = chatService
                .getAllChatsByUserId(currentUserId);

        return ResponseEntity
                .ok(response);
    }
    @PostMapping("/group")
    public ResponseEntity<ChatResponse> createGroupChat(
            @RequestBody CreateGroupChatRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        // 🔐 Always take creator from JWT (never from request body)
        Long currentUserId = principal.getId();

        ChatResponse response = chatService.createGroupChat(
                currentUserId,
                request.getParticipantIds(),
                request.getGroupName(),
                request.getGroupProfilePicUrl()
        );

        // 201 Created + Location header (industry standard)
        return ResponseEntity
                .created(URI.create("/api/v1/chats/" + response.getChatId()))
                .body(response);

    }
    @PostMapping("/{chatId}/add/{userId}")
    public ResponseEntity<ChatResponse> addUserToGroup(
            @PathVariable Long chatId,
            @PathVariable Long userId
            ,@AuthenticationPrincipal UserPrincipal principal){
        Long currentUserId = principal.getId();

        ChatResponse response = chatService.addUserToGroup(
                chatId,
                userId,
                currentUserId
        );

        return ResponseEntity.ok(response);

    }
    @PostMapping("/{chatId}/remove/{userId}")
    public ResponseEntity<ChatResponse> removeUserFromGroup(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal principal){
        Long currentUserId = principal.getId();
        ChatResponse response = chatService.removeUserFromGroup(
                chatId,
                userId,
                currentUserId
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/group/{chatId}/rename")
    public ResponseEntity<ChatResponse> renameGroup(
            @PathVariable Long chatId,
            @RequestParam String newGroupName,
            @AuthenticationPrincipal UserPrincipal principal){
        Long currentUserId = principal.getId();
        ChatResponse response = chatService.renameGroup(
                chatId,
                newGroupName,
                currentUserId
        );
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{chatId}/update-profile-pic")
    public ResponseEntity<ChatResponse> updateGroupProfilePic(
            @PathVariable Long chatId,
            @RequestParam String profilePicUrl,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long currentUserId = principal.getId();
        ChatResponse response = chatService.updateGroupProfilePic(
                chatId,
                profilePicUrl,
                currentUserId
        );
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{chatId}/archive")
    public ResponseEntity<Void> archiveChat(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long currentUserId = principal.getId();
        chatService.archiveChat(chatId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatId}/unarchive")
    public ResponseEntity<Void> unarchiveChat(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long currentUserId = principal.getId();
        chatService.unarchiveChat(chatId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long currentUserId = principal.getId();
        chatService.deleteChat(chatId, currentUserId);
        return ResponseEntity.noContent().build();
    }


}
