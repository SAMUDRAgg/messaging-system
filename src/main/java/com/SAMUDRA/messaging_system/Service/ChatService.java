package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DTO.ChatResponse;
import com.SAMUDRA.messaging_system.DTO.CreateGroupChatRequest;
import com.SAMUDRA.messaging_system.Exception.ChatException;
import com.SAMUDRA.messaging_system.Exception.UserException;

import java.util.List;

public interface ChatService {

    /* -------------------- ONE-TO-ONE CHAT -------------------- */

    // Create or return existing one-to-one chat
    ChatResponse createOneToOneChat(Long userId1, Long userId2)
            throws UserException, ChatException;

    // Get chat by chatId
    ChatResponse getChatById(Long chatId)
            throws ChatException;

    // Get all chats for a user (chat list screen)
    List<ChatResponse> getAllChatsByUserId(Long userId)
            throws UserException;


    /* -------------------- GROUP CHAT -------------------- */

    // Create group chat
    ChatResponse createGroupChat(CreateGroupChatRequest request)
            throws UserException, ChatException;

    // Add user to group
    ChatResponse addUserToGroup(Long chatId, Long userId)
            throws ChatException;

    // Remove user from group
    ChatResponse removeUserFromGroup(Long chatId, Long userId)
            throws ChatException;

    // Rename group
    ChatResponse renameGroup(Long chatId, String newGroupName)
            throws ChatException;

    // Update group profile picture
    ChatResponse updateGroupProfilePic(Long chatId, String profilePicUrl)
            throws ChatException;


    /* -------------------- CHAT LIFECYCLE -------------------- */

    // Archive chat (user-level)
    void archiveChat(Long chatId, Long userId)
            throws ChatException;

    // Soft delete chat
    void deleteChat(Long chatId, Long userId)
            throws ChatException;


    /* -------------------- VALIDATION / UTILITY -------------------- */

    boolean chatExists(Long chatId);

    boolean isUserInChat(Long chatId, Long userId);
}