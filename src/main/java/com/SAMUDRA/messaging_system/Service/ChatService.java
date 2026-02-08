package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.Exception.ChatException;
import com.SAMUDRA.messaging_system.Exception.UserException;

import java.util.List;

public interface ChatService {

    /* -------------------- ONE-TO-ONE CHAT -------------------- */

    // Create or return existing one-to-one chat
    Chat createOneToOneChat(Long userId1, Long userId2)
            throws UserException, ChatException;

    // Get chat by chatId
    Chat getChatById(Long chatId)
            throws ChatException;

    // Get all chats for a user (chat list screen)
    List<Chat> getAllChatsByUserId(Long userId)
            throws UserException;


    /* -------------------- GROUP CHAT -------------------- */

    // Create group chat
    Chat createGroupChat(
            Long creatorId,
            List<Long> participantIds,
            String groupName,
            String groupProfilePicUrl
    ) throws UserException, ChatException;

    // Add user to group
    Chat addUserToGroup(Long chatId, Long userId)
            throws ChatException;

    // Remove user from group
    Chat removeUserFromGroup(Long chatId, Long userId)
            throws ChatException;

    // Rename group
    Chat renameGroup(Long chatId, String newGroupName)
            throws ChatException;

    // Update group profile picture
    Chat updateGroupProfilePic(Long chatId, String profilePicUrl)
            throws ChatException;


    /* -------------------- CHAT LIFECYCLE -------------------- */

    // Archive chat (user-level)
    void archiveChat(Long chatId, Long userId)
            throws ChatException;

    // Soft delete chat
    void deleteChat(Long chatId, Long userId)
            throws ChatException;


    /* -------------------- VALIDATION / UTILITY -------------------- */

    // Check if chat exists
    boolean chatExists(Long chatId);

    // Check if user is participant of chat
    boolean isUserInChat(Long chatId, Long userId);
}