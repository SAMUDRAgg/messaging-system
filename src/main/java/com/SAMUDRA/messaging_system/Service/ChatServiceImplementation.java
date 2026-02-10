package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.DAO.ChatParticipant;
import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DTO.ChatResponse;
import com.SAMUDRA.messaging_system.DTO.CreateGroupChatRequest;
import com.SAMUDRA.messaging_system.DTO.ParticipantResponse;
import com.SAMUDRA.messaging_system.Exception.ChatException;
import com.SAMUDRA.messaging_system.Exception.UserException;
import com.SAMUDRA.messaging_system.Repo.ChatParticipantRepo;
import com.SAMUDRA.messaging_system.Repo.ChatRepo;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import com.SAMUDRA.messaging_system.enums.ChatRole;
import com.SAMUDRA.messaging_system.enums.ChatStatus;
import com.SAMUDRA.messaging_system.enums.ChatType;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatServiceImplementation implements ChatService {


    private ChatResponse mapToChatResponse(Chat chat) {


        List<ParticipantResponse> participants =
                chatParticipantRepo.findByChat(chat)
                        .stream()
                        .map(cp -> new ParticipantResponse(
                                cp.getUser().getId(),
                                cp.getRole()
                        ))
                        .toList();

        return new ChatResponse(
                chat.getChatId(),
                chat.getChatType(),
                chat.getTitle(),
                chat.getGroupProfilePicUrl(),
                participants.stream().map(ParticipantResponse::getUserId).toList(),
                chat.getChatStatus(),
                chat.getLastMessageAt()
        );
    }

    private ChatRepo chatRepo;
    private UserService userService;
    private UserRepo userRepo;
    private ChatParticipantRepo chatParticipantRepo;
    public ChatServiceImplementation(ChatRepo chatRepo , UserService userService , UserRepo userRepo, ChatParticipantRepo chatParticipantRepo) {
        this.chatRepo = chatRepo;
       this.userService = userService;
       this.userRepo = userRepo;
       this.chatParticipantRepo = chatParticipantRepo;
    }

    @Override
    public ChatResponse createOneToOneChat(Long userId1, Long userId2) throws UserException, ChatException {
        // 1️⃣ Same user validation
        if (userId1.equals(userId2)) {
            throw new ChatException("Cannot create chat with yourself");
        }

        // 2️⃣ Validate users
        User user1 = userService.findById(userId1);
        User user2 = userService.findById(userId2);

        // 3️⃣ Check if one-to-one chat already exists
        Chat existingChat =
                chatRepo.findOneToOneChat(userId1, userId2,ChatType.ONE_TO_ONE);

        if (existingChat != null) {
            return mapToChatResponse(existingChat);
        }

        // 4️⃣ Create chat
        Chat chat = new Chat();
        chat.setCreatedBy(user1); // either user is fine
        chat.setChatType(ChatType.ONE_TO_ONE);
        chat.setChatStatus(ChatStatus.ACTIVE);

        Chat savedChat = chatRepo.save(chat);

        // 5️⃣ Create participants
        ChatParticipant p1 = new ChatParticipant(
                savedChat,
                user1,
                ChatRole.MEMBER
        );

        ChatParticipant p2 = new ChatParticipant(
                savedChat,
                user2,
                ChatRole.MEMBER
        );

        chatParticipantRepo.saveAll(List.of(p1, p2));

        // 6️⃣ Return response
        return mapToChatResponse(savedChat);

    }

    @Override
    public ChatResponse getChatById(Long chatId) throws ChatException {
        Chat chat = chatRepo.getById(chatId);

        if(chat== null){
            throw new ChatException("Chat not found with id: " + chatId);
        }
        else {
            return mapToChatResponse(chat);
        }
    }

    @Override
    public List<ChatResponse> getAllChatsByUserId(Long userId) throws UserException {
       if(userService.findById(userId) == null){
           throw new UserException("User not found with id: " + userId);
       }
       else {
           List<Chat> chats = chatRepo.findAllByUserId(userId);

           return chats.stream()
                   .map(this::mapToChatResponse)
                   .toList();
       }
    }
    @Transactional
    @Override
    public ChatResponse createGroupChat(CreateGroupChatRequest request) throws UserException, ChatException {
        // 1️⃣ Validate group name
        if (request.getGroupName() == null || request.getGroupName().isBlank()) {
            throw new ChatException("Group name cannot be empty");
        }

        // 2️⃣ Validate creator
        User creator = userService.findById(request.getCreatorId());
        Long creatorId = creator.getId();

        // 3️⃣ Validate participant list
        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new ChatException("At least one participant is required");
        }

        // 4️⃣ Ensure creator is always included
        Set<Long> participantIds = new HashSet<>(request.getParticipantIds());
        participantIds.add(creator.getId());

        // 5️⃣ Validate all participants in ONE DB call
        List<User> users = userRepo.findAllById(participantIds);
        if (users.size() != participantIds.size()) {
            throw new ChatException("One or more participants not found");
        }

        // 6️⃣ Create chat
        Chat chat = new Chat();
        chat.setChatType(ChatType.GROUP);
        chat.setTitle(request.getGroupName());
        chat.setCreatedBy(creator);
        chat.setChatStatus(ChatStatus.ACTIVE);

        Chat savedChat = chatRepo.save(chat);

        // 7️⃣ Create participants with roles
        List<ChatParticipant> participants = users.stream()
                .map(user -> new ChatParticipant(
                        savedChat,
                        user,
                        user.getId().equals(creator.getId())
                                ? ChatRole.ADMIN
                                : ChatRole.MEMBER
                ))
                .toList();

        chatParticipantRepo.saveAll(participants);

        // 8️⃣ Return response
        return mapToChatResponse(savedChat);
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

