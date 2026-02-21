package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.DAO.ChatMessage;
import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DTO.MessageRequest;
import com.SAMUDRA.messaging_system.DTO.MessageResponse;


import com.SAMUDRA.messaging_system.Exception.ChatException;
import com.SAMUDRA.messaging_system.Exception.MessageException;
import com.SAMUDRA.messaging_system.Exception.UserException;
import com.SAMUDRA.messaging_system.Mapper.MessageMapper;
import com.SAMUDRA.messaging_system.Repo.ChatParticipantRepo;
import com.SAMUDRA.messaging_system.Repo.ChatRepo;
import com.SAMUDRA.messaging_system.Repo.MessageRepo;
import com.SAMUDRA.messaging_system.Repo.UserRepo;

import com.SAMUDRA.messaging_system.enums.MessageStatus;
import com.SAMUDRA.messaging_system.enums.MessageType;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;



@Service

@Transactional
public class MessageServiceImplementation implements MessageService {

    private final MessageRepo messageRepo;
    private final ChatRepo chatRepo;
    private final UserRepo userRepo;
    private final ChatParticipantRepo chatParticipantRepo;
    private final MessageMapper messageMapper;

    public MessageServiceImplementation(MessageRepo messageRepo, ChatRepo chatRepo, UserRepo userRepo, ChatParticipantRepo chatParticipantRepo, MessageMapper messageMapper) {
        this.messageRepo = messageRepo;
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
        this.chatParticipantRepo = chatParticipantRepo;
        this.messageMapper = messageMapper;
    }

    /* =======================================================
       🔐 PRIVATE HELPERS
    ======================================================= */

    private Chat getChatOrThrow(Long chatId) {
        return chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatException("Chat not found", HttpStatus.NOT_FOUND));
    }

    private User getUserOrThrow(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserException("User not found"));
    }

    private ChatMessage getMessageOrThrow(Long messageId) {
        return messageRepo.findById(messageId)
                .orElseThrow(() ->
                        new MessageException("Message not found", HttpStatus.NOT_FOUND));
    }

    private void validateParticipant(Long chatId, Long userId) {
        chatParticipantRepo.findByChatChatIdAndUserId(chatId, userId)
                .orElseThrow(() ->
                        new ChatException("Access denied", HttpStatus.FORBIDDEN));
    }

    /* =======================================================
       1️⃣ SEND MESSAGE
    ======================================================= */
    @Transactional
    @Override
    public MessageResponse sendMessage(Long chatId, Long senderId, MessageRequest request)
            throws UserException, ChatException, MessageException {

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new MessageException("Message content cannot be empty", HttpStatus.BAD_REQUEST);
        }

        Chat chat = getChatOrThrow(chatId);
        User sender = getUserOrThrow(senderId);

        validateParticipant(chatId, senderId);

        ChatMessage message = new ChatMessage();

        message.setChat(chat);
        message.setSenderId(senderId);
        message.setSenderUsername(sender.getUsername());

        message.setContent(request.getContent().trim());

        message.setMessageType(MessageType.TEXT);
        message.setMessageStatus(MessageStatus.SENT);

        message.setCreatedAt(LocalDateTime.now());
        message.setEdited(false);
        message.setDeleted(false);
        message.setUpdatedAt(null);

        ChatMessage saved = messageRepo.save(message);

        return messageMapper.mapToResponse(saved);
    }

    /* =======================================================
       2️⃣ GET MESSAGES (Paginated)
    ======================================================= */

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long chatId, Long userId, int page, int size)
            throws UserException, ChatException {

        getChatOrThrow(chatId);
        validateParticipant(chatId, userId);

        Pageable pageable = PageRequest.of(page, size);

        return messageRepo.findByChat_ChatIdOrderByCreatedAtDesc(chatId, pageable)
                .stream()
                .map(messageMapper::mapToResponse)
                .toList();
    }

    /* =======================================================
       3️⃣ EDIT MESSAGE
    ======================================================= */
    @Transactional
    @Override
    public MessageResponse editMessage(
            Long messageId,
            Long userId,
            String newContent
    ) throws UserException, MessageException {

        if (newContent == null || newContent.isBlank()) {
            throw new MessageException(
                    "Message content cannot be empty",
                    HttpStatus.BAD_REQUEST
            );
        }

        ChatMessage message = getMessageOrThrow(messageId);

        // 🔐 Only sender can edit
        if (!message.getSenderId().equals(userId)) {
            throw new MessageException(
                    "You can only edit your own message",
                    HttpStatus.FORBIDDEN
            );
        }

        // 🚫 Cannot edit deleted message
        if (message.isDeleted()) {
            throw new MessageException(
                    "Message already deleted",
                    HttpStatus.GONE
            );
        }

        message.setContent(newContent.trim());
        message.setEdited(true);
        message.setUpdatedAt(LocalDateTime.now());

        return messageMapper.mapToResponse(message);
    }

    /* =======================================================
       4️⃣ DELETE MESSAGE (Soft Delete)
    ======================================================= */
    @Transactional
    @Override
    public void deleteMessage(Long messageId, Long userId)
            throws UserException, MessageException {

        ChatMessage message = getMessageOrThrow(messageId);

        if (!message.getSenderId().equals(userId)) {
            throw new MessageException(
                    "You can only delete your own message",
                    HttpStatus.FORBIDDEN
            );
        }

        if (message.isDeleted()) {
            throw new MessageException(
                    "Message already deleted",
                    HttpStatus.GONE
            );
        }

        message.setDeleted(true);
        message.setContent("This message was deleted");
    }

    /* =======================================================
       5️⃣ MARK AS READ
    ======================================================= */
    @Transactional
    @Override
    public void markAsRead(Long chatId, Long userId)
            throws UserException, ChatException {

        getChatOrThrow(chatId);
        validateParticipant(chatId, userId);

        messageRepo.markMessagesAsRead(chatId, userId);
    }



    /* =======================================================
       7️⃣ EXISTS
    ======================================================= */

    @Override
    @Transactional(readOnly = true)
    public boolean messageExists(Long messageId) {
        return messageRepo.existsById(messageId);
    }

    /* =======================================================
       8️⃣ UNREAD COUNT
    ======================================================= */

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long chatId, Long userId)
            throws UserException, ChatException {

        getChatOrThrow(chatId);
        validateParticipant(chatId, userId);

        return messageRepo.countUnreadMessages(chatId, userId);
    }

    /* =======================================================
       9️⃣ FORWARD MESSAGE
    ======================================================= */

    @Override
    public void forwardMessage(Long messageId, Long fromChatId, Long toChatId, Long userId)
            throws UserException, ChatException, MessageException {

        ChatMessage original = getMessageOrThrow(messageId);

        validateParticipant(fromChatId, userId);
        validateParticipant(toChatId, userId);

        Chat targetChat = getChatOrThrow(toChatId);
        User sender = getUserOrThrow(userId);
        ChatMessage forwarded = new ChatMessage();
        forwarded.setChat(targetChat);

        forwarded.setSenderId(userId);
        forwarded.setSenderUsername(sender.getUsername());   

        forwarded.setContent(original.getContent());
        forwarded.setMessageType(original.getMessageType());
        forwarded.setMessageStatus(MessageStatus.SENT);

        forwarded.setCreatedAt(LocalDateTime.now());
        forwarded.setDeleted(false);
        forwarded.setEdited(false);

        messageRepo.save(forwarded);

        messageRepo.save(forwarded);
    }




}