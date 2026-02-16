package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DTO.MessageRequest;
import com.SAMUDRA.messaging_system.DTO.MessageResponse;
import com.SAMUDRA.messaging_system.Exception.ChatException;
import com.SAMUDRA.messaging_system.Exception.MessageException;
import com.SAMUDRA.messaging_system.Exception.UserException;

import java.util.List;

public class MessageServiceIMplementation implements MessageService {
    @Override
    public MessageResponse sendMessage(Long chatId, Long senderId, MessageRequest request) throws UserException, ChatException, MessageException {
        return null;
    }

    @Override
    public List<MessageResponse> getMessages(Long chatId, Long userId, int page, int size) throws UserException, ChatException {
        return List.of();
    }

    @Override
    public MessageResponse editMessage(Long messageId, Long userId, String newContent) throws UserException, MessageException {
        return null;
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) throws UserException, MessageException {

    }

    @Override
    public void markAsRead(Long chatId, Long userId) throws UserException, ChatException {

    }

    @Override
    public void reactToMessage(Long messageId, Long userId, String reaction) throws UserException, MessageException {

    }

    @Override
    public boolean messageExists(Long messageId) {
        return false;
    }

    @Override
    public long getUnreadCount(Long chatId, Long userId) throws UserException, ChatException {
        return 0;
    }

    @Override
    public void forwardMessage(Long messageId, Long fromChatId, Long toChatId, Long userId) throws UserException, ChatException, MessageException {

    }
}
