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
import com.SAMUDRA.messaging_system.enums.ParticipantChatStatus;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Service
public class ChatServiceImplementation implements ChatService {


    private ChatResponse mapToChatResponse(Chat chat) {


        List<ParticipantResponse> participants =
                chatParticipantRepo.findByChatChatIdAndStatus(chat.getChatId(), ParticipantChatStatus.ACTIVE)
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
    public ChatResponse getChatById(Long chatId, Long userId) throws ChatException {
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatException(
                                "Chat not found with id: " + chatId,
                                HttpStatus.NOT_FOUND
                        ));

        // 2️⃣ Validate user access
        ChatParticipant participant = chatParticipantRepo
                .findByChatChatIdAndUserId(chatId, userId)
                .orElseThrow(() ->
                        new ChatException(
                                "Access denied",
                                HttpStatus.FORBIDDEN
                        ));

        // 3️⃣ Validate participant status
        if (participant.getStatus() != ParticipantChatStatus.ACTIVE) {
            throw new ChatException(
                    "Chat not available",
                    HttpStatus.GONE
            );
        }

        return mapToChatResponse(chat);
    }

    @Override
    public List<ChatResponse> getAllChatsByUserId(Long userId) throws UserException {
        // 1️⃣ Fetch only ACTIVE participations
        List<ChatParticipant> participations =
                chatParticipantRepo.findByUserIdAndStatus(
                        userId,
                        ParticipantChatStatus.ACTIVE
                );

        // 2️⃣ Map to ChatResponse
        return participations.stream()
                .map(ChatParticipant::getChat)
                .map(this::mapToChatResponse)
                .toList();
    }
    @Transactional
    @Override
    public ChatResponse createGroupChat(Long creatorId,
                                        List<Long> participantIds,
                                        String groupName,
                                        String groupProfilePicUrl ) throws UserException, ChatException {
        // 1️⃣ Validate group name
        if (groupName == null || groupName.isBlank()) {
            throw new ChatException(
                    "Group name cannot be empty",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 2️⃣ Validate creator
        User creator = userRepo.findById(creatorId)
                .orElseThrow(() ->
                        new ChatException(
                                "Creator not found",
                                HttpStatus.NOT_FOUND
                        ));

        // 3️⃣ Validate participant list
        if (participantIds == null || participantIds.isEmpty()) {
            throw new ChatException(
                    "At least one participant is required",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 4️⃣ Remove duplicates
        Set<Long> uniqueIds = new HashSet<>(participantIds);

        // 5️⃣ Add creator automatically
        uniqueIds.add(creatorId);

        // 6️⃣ Validate all users in one query
        List<User> users = userRepo.findAllById(uniqueIds);

        if (users.size() != uniqueIds.size()) {
            throw new ChatException(
                    "One or more participants not found",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 7️⃣ Create chat
        Chat chat = new Chat();
        chat.setChatType(ChatType.GROUP);
        chat.setTitle(groupName.trim());
        chat.setCreatedBy(creator);
        chat.setChatStatus(ChatStatus.ACTIVE);

        if (groupProfilePicUrl != null && !groupProfilePicUrl.isBlank()) {
            chat.setGroupProfilePicUrl(groupProfilePicUrl.trim());
        }

        Chat savedChat = chatRepo.save(chat);

        // 8️⃣ Create participants
        List<ChatParticipant> participants = users.stream()
                .map(user -> new ChatParticipant(
                        savedChat,
                        user,
                        user.getId().equals(creatorId)
                                ? ChatRole.ADMIN
                                : ChatRole.MEMBER
                ))
                .toList();

        chatParticipantRepo.saveAll(participants);

        return mapToChatResponse(savedChat);
    }

    @Override
    public ChatResponse addUserToGroup(Long chatId, Long userId, Long currentUserId) throws ChatException {
        // 1️⃣ Fetch chat properly
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new ChatException("Chat not found with id: " + chatId));

        // 2️⃣ Validate chat type
        if (chat.getChatType() != ChatType.GROUP) {
            throw new ChatException("Cannot add users to one-to-one chat");
        }

        // 3️⃣ Validate chat status
        if (chat.getChatStatus() != ChatStatus.ACTIVE) {
            throw new ChatException("Cannot modify inactive chat");
        }

        // 4️⃣ Validate user exists
        User user = userService.findById(userId);

        // 5️⃣ Check if already participant
        boolean exists = chatParticipantRepo
                .existsByChatChatIdAndUserId(chatId, userId);

        if (exists) {
            throw new ChatException("User is already in the group");
        }

        // 6️⃣ Add participant
        ChatParticipant participant =
                new ChatParticipant(chat, user, ChatRole.MEMBER);

        chatParticipantRepo.save(participant);

        return mapToChatResponse(chat);
    }

    @Override
    public ChatResponse removeUserFromGroup(Long chatId, Long userId,Long requesterId) throws ChatException {
        // 1️⃣ Fetch chat
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatException("Chat not found with id: " + chatId));

        // 2️⃣ Validate chat type
        if (chat.getChatType() != ChatType.GROUP) {
            throw new ChatException("Cannot remove users from a one-to-one chat");
        }

        // 3️⃣ Check requester is ADMIN
        ChatParticipant requester = chatParticipantRepo
                .findByChatChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() ->
                        new ChatException("Requester is not a member of this group"));

        if (requester.getRole() != ChatRole.ADMIN) {
            throw new ChatException("Only admins can remove members");
        }

        // 4️⃣ Fetch participant to remove
        ChatParticipant participant = chatParticipantRepo
                .findByChatChatIdAndUserId(chatId, userId)
                .orElseThrow(() ->
                        new ChatException("User is not a member of this group"));

        // 5️⃣ Prevent removing last admin
        if (participant.getRole() == ChatRole.ADMIN) {
            long adminCount = chatParticipantRepo.countByChatChatIdAndRoleAndStatus(chatId, ChatRole.ADMIN, ChatStatus.ACTIVE);

            if (adminCount <= 1) {
                throw new ChatException("Cannot remove the last admin from the group");
            }
        }

        // 6️⃣ Remove
        chatParticipantRepo.delete(participant);

        return mapToChatResponse(chat);
    }

    @Override
    public ChatResponse renameGroup(Long chatId, String newGroupName ,Long requesterId) throws ChatException {
        // 1️⃣ Fetch chat
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatException("Chat not found with id: " + chatId));

        // 2️⃣ Validate chat type
        if (chat.getChatType() != ChatType.GROUP) {
            throw new ChatException("Cannot rename a one-to-one chat");
        }

        // 3️⃣ Validate chat status
        if (chat.getChatStatus() != ChatStatus.ACTIVE) {
            throw new ChatException("Cannot modify an inactive chat");
        }

        // 4️⃣ Validate group name
        if (newGroupName == null || newGroupName.trim().isEmpty()) {
            throw new ChatException("Group name cannot be empty");
        }

        String trimmedName = newGroupName.trim();

        // 5️⃣ Prevent unnecessary update
        if (trimmedName.equals(chat.getTitle())) {
            throw new ChatException("Group name is already set to this value");
        }

        // 6️⃣ Validate requester is ADMIN
        ChatParticipant requester = chatParticipantRepo
                .findByChatChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() ->
                        new ChatException("Requester is not a member of this group"));

        if (requester.getRole() != ChatRole.ADMIN) {
            throw new ChatException("Only admins can rename the group");
        }

        // 7️⃣ Rename (no explicit save needed if inside @Transactional)
        chat.setTitle(trimmedName);
            return mapToChatResponse(chat);
    }

    @Override
    public ChatResponse updateGroupProfilePic(Long chatId, String profilePicUrl ,Long requesterId) throws ChatException {
        // 1️⃣ Fetch chat
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() ->
                        new ChatException("Chat not found with id: " + chatId));

        // 2️⃣ Validate chat type
        if (chat.getChatType() != ChatType.GROUP) {
            throw new ChatException("Cannot update profile picture of a one-to-one chat");
        }

        // 3️⃣ Validate chat status
        if (chat.getChatStatus() != ChatStatus.ACTIVE) {
            throw new ChatException("Cannot modify an inactive chat");
        }

        // 4️⃣ Validate requester membership
        ChatParticipant requester = chatParticipantRepo
                .findByChatChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() ->
                        new ChatException("Requester is not a member of this group"));

        // 5️⃣ Validate ADMIN permission
        if (requester.getRole() != ChatRole.ADMIN) {
            throw new ChatException("Only admins can update the group profile picture");
        }

        // 6️⃣ Validate URL
        if (profilePicUrl == null || profilePicUrl.trim().isEmpty()) {
            throw new ChatException("Profile picture URL cannot be empty");
        }

        String trimmedUrl = profilePicUrl.trim();

        // 7️⃣ Prevent redundant update
        if (trimmedUrl.equals(chat.getGroupProfilePicUrl())) {
            throw new ChatException("Profile picture is already set to this value");
        }

        // Optional: basic URL validation (simple safeguard)
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            throw new ChatException("Invalid profile picture URL");
        }

        // 8️⃣ Update (dirty checking will persist)
        chat.setGroupProfilePicUrl(trimmedUrl);

        return mapToChatResponse(chat);
    }
    @Transactional
    @Override
    public void archiveChat(Long chatId, Long userId) throws ChatException {
        ChatParticipant participant = chatParticipantRepo
                .findByChatChatIdAndUserId(chatId, userId)
                .orElseThrow(() ->
                        new ChatException("User is not a participant of this chat"));

        if (participant.getStatus() == ParticipantChatStatus.ARCHIVED) {
            throw new ChatException("Chat is already archived");
        }

        participant.setStatus(ParticipantChatStatus.ARCHIVED);
    }

    @Transactional
    @Override
    public void unarchiveChat(Long chatId, Long userId) {

        ChatParticipant participant = chatParticipantRepo
                .findByChatChatIdAndUserId(chatId, userId)
                .orElseThrow(() ->
                        new ChatException("User is not a participant of this chat"));

        if (participant.getStatus() != ParticipantChatStatus.ARCHIVED) {
            throw new ChatException("Chat is not archived");
        }

        participant.setStatus(ParticipantChatStatus.ACTIVE);
    }
    @Transactional
    @Override
    public void deleteChat(Long chatId, Long userId) throws ChatException {
        // 1️⃣ Fetch participant record
        ChatParticipant participant = chatParticipantRepo
                .findByChatChatIdAndUserId(chatId, userId)
                .orElseThrow(() ->
                        new ChatException("User is not a participant of this chat"));

        // 2️⃣ Prevent duplicate delete
        if (participant.getStatus() == ParticipantChatStatus.LEFT) {
            throw new ChatException("Chat is already deleted for this user");
        }

        // 3️⃣ Mark as LEFT (soft delete for user)
        participant.setStatus(ParticipantChatStatus.LEFT);
    }



}

