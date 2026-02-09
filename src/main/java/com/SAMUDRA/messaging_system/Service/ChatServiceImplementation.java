package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DTO.ChatResponse;
import com.SAMUDRA.messaging_system.DTO.CreateGroupChatRequest;
import com.SAMUDRA.messaging_system.Exception.ChatException;
import com.SAMUDRA.messaging_system.Exception.UserException;
import com.SAMUDRA.messaging_system.Repo.ChatRepo;
import com.SAMUDRA.messaging_system.enums.ChatStatus;
import com.SAMUDRA.messaging_system.enums.ChatType;

import java.util.List;

public class ChatServiceImplementation implements ChatService {


    private ChatResponse mapToChatResponse(Chat chat) {
        return new ChatResponse(
                chat.getChatId(),
                chat.getChatType(),
                chat.getTitle(),
                chat.getGroupProfilePicUrl(),
                chat.getParticipantIds(),
                chat.getChatStatus(),
                chat.getLastMessageAt()
        );
    }

    private ChatRepo chatRepo;
    private UserService userService;

    public ChatServiceImplementation(ChatRepo chatRepo , UserService userService) {
        this.chatRepo = chatRepo;
       this.userService = userService;
    }

    @Override
    public ChatResponse createOneToOneChat(Long userId1, Long userId2) throws UserException, ChatException {
        // 1️⃣ Validation: same user check
        if (userId1.equals(userId2)) {
            throw new ChatException("Cannot create chat with yourself");
        }

        // 2️⃣ Validate both users exist
        userService.findById(userId1);
        userService.findById(userId2);

        // 3️⃣ Check if one-to-one chat already exists
        Chat existingChat = chatRepo.findOneToOneChat(userId1, userId2,ChatType.ONE_TO_ONE );
        if (existingChat != null) {
            return mapToChatResponse(existingChat);
        }

        // 4️⃣ Create new one-to-one chat
        Chat chat = new Chat();
        chat.setCreatedBy(userId1);
        chat.setChatType(ChatType.ONE_TO_ONE);
        chat.setParticipantIds(List.of(userId1, userId2));
        chat.setChatStatus(ChatStatus.ACTIVE);

        // title & groupProfilePicUrl stay NULL for one-to-one

        // 5️⃣ Save chat
        Chat savedChat = chatRepo.save(chat);

        // 6️⃣ Return response DTO
        return mapToChatResponse(savedChat);

    }

    @Override
    public ChatResponse getChatById(Long chatId) throws ChatException {
        return null;
    }

    @Override
    public List<ChatResponse> getAllChatsByUserId(Long userId) throws UserException {
        return List.of();
    }

    @Override
    public ChatResponse createGroupChat(CreateGroupChatRequest request) throws UserException, ChatException {
        return null;
    }

    @Override
    public ChatResponse addUserToGroup(Long chatId, Long userId) throws ChatException {
        return null;
    }

    @Override
    public ChatResponse removeUserFromGroup(Long chatId, Long userId) throws ChatException {
        return null;
    }

    @Override
    public ChatResponse renameGroup(Long chatId, String newGroupName) throws ChatException {
        return null;
    }

    @Override
    public ChatResponse updateGroupProfilePic(Long chatId, String profilePicUrl) throws ChatException {
        return null;
    }

    @Override
    public void archiveChat(Long chatId, Long userId) throws ChatException {

    }

    @Override
    public void deleteChat(Long chatId, Long userId) throws ChatException {

    }

    @Override
    public boolean chatExists(Long chatId) {
        return false;
    }

    @Override
    public boolean isUserInChat(Long chatId, Long userId) {
        return false;
    }
}

