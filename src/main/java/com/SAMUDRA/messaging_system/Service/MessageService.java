package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DTO.MessageRequest;
import com.SAMUDRA.messaging_system.DTO.MessageResponse;
import com.SAMUDRA.messaging_system.Exception.ChatException;
import com.SAMUDRA.messaging_system.Exception.UserException;
import com.SAMUDRA.messaging_system.Exception.MessageException;

import java.util.List;

public interface MessageService {

    // 1️⃣ Send a message
    MessageResponse sendMessage(
            Long chatId,
            Long senderId,
            MessageRequest request
    ) throws UserException, ChatException, MessageException;


    // 2️⃣ Get chat messages (paginated)
    List<MessageResponse> getMessages(
            Long chatId,
            Long userId,
            int page,
            int size
    ) throws UserException, ChatException;


    // 3️⃣ Edit message
    MessageResponse editMessage(
            Long messageId,
            Long userId,
            String newContent
    ) throws UserException, MessageException;


    // 4️⃣ Delete message (soft delete)
    void deleteMessage(
            Long messageId,
            Long userId
    ) throws UserException, MessageException;


    // 5️⃣ Mark messages as read
    void markAsRead(
            Long chatId,
            Long userId
    ) throws UserException, ChatException;


    // 6️⃣ React to a message
    void reactToMessage(
            Long messageId,
            Long userId,
            String reaction
    ) throws UserException, MessageException;


    // 7️⃣ Check message exists
    boolean messageExists(Long messageId);


    // 8️⃣ Get unread count
    long getUnreadCount(
            Long chatId,
            Long userId
    ) throws UserException, ChatException;


    // 9️⃣ Forward message
    void forwardMessage(
            Long messageId,
            Long fromChatId,
            Long toChatId,
            Long userId
    ) throws UserException, ChatException, MessageException;
}