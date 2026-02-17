package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.Chat;
import com.SAMUDRA.messaging_system.DAO.ChatParticipant;
import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.DTO.ChatResponse;
import com.SAMUDRA.messaging_system.Exception.ChatException;
import com.SAMUDRA.messaging_system.Mapper.ChatMapper;
import com.SAMUDRA.messaging_system.Repo.ChatParticipantRepo;
import com.SAMUDRA.messaging_system.Repo.ChatRepo;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import com.SAMUDRA.messaging_system.enums.ChatRole;
import com.SAMUDRA.messaging_system.enums.ChatStatus;
import com.SAMUDRA.messaging_system.enums.ChatType;
import com.SAMUDRA.messaging_system.enums.ParticipantChatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ChatServiceImplementationTest {

    @Mock
    private ChatRepo chatRepo;

    @Mock
    private UserService userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ChatParticipantRepo chatParticipantRepo;

    @Mock
    private ChatMapper chatMapper;

    @InjectMocks
    private ChatServiceImplementation chatService;

    @Captor
    private ArgumentCaptor<Chat> chatCaptor;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("alice");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("bob");
    }

    @Test
    void createOneToOneChat_createsChatAndParticipants_whenUsersValid() throws Exception {
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(user2);
        when(chatRepo.findOneToOneChat(1L, 2L, ChatType.ONE_TO_ONE)).thenReturn(null);

        Chat saved = new Chat();
        saved.setChatType(ChatType.ONE_TO_ONE);
        saved.setChatStatus(ChatStatus.ACTIVE);

        when(chatRepo.save(any(Chat.class))).thenReturn(saved);

        ChatResponse response = new ChatResponse(100L, null, null, null, null, null, null);
        when(chatMapper.mapToChatResponse(saved)).thenReturn(response);

        ChatResponse result = chatService.createOneToOneChat(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(result.getChatId()).isEqualTo(100L);

        verify(chatRepo).findOneToOneChat(1L, 2L, ChatType.ONE_TO_ONE);
        verify(chatRepo).save(chatCaptor.capture());
        verify(chatParticipantRepo).saveAll(any());
    }

    @Test
    void createOneToOneChat_throwsChatException_whenSameUser() {
        assertThrows(ChatException.class, () -> chatService.createOneToOneChat(1L, 1L));
    }

    @Test
    void getChatById_returnsChatResponse_whenParticipantActive() throws Exception {
        Chat chat = new Chat();

        when(chatRepo.findById(10L)).thenReturn(Optional.of(chat));

        ChatParticipant participant = new ChatParticipant();
        participant.setStatus(ParticipantChatStatus.ACTIVE);

        when(chatParticipantRepo.findByChatChatIdAndUserId(10L, 1L)).thenReturn(Optional.of(participant));

        ChatResponse response = new ChatResponse(10L, null, null, null, null, null, null);
        when(chatMapper.mapToChatResponse(chat)).thenReturn(response);

        ChatResponse res = chatService.getChatById(10L, 1L);

        assertThat(res).isNotNull();
        assertThat(res.getChatId()).isEqualTo(10L);
    }

    @Test
    void addUserToGroup_addsParticipant_whenValid() throws Exception {
        Chat chat = new Chat();
        chat.setChatType(ChatType.GROUP);
        chat.setChatStatus(ChatStatus.ACTIVE);

        when(chatRepo.findById(50L)).thenReturn(Optional.of(chat));
        when(userService.findById(3L)).thenReturn(user2);
        when(chatParticipantRepo.existsByChatChatIdAndUserId(50L, 3L)).thenReturn(false);

        ChatResponse response = new ChatResponse(50L, null, null, null, null, null, null);
        when(chatMapper.mapToChatResponse(chat)).thenReturn(response);

        ChatResponse res = chatService.addUserToGroup(50L, 3L, 1L);

        assertThat(res).isNotNull();
        assertThat(res.getChatId()).isEqualTo(50L);
        verify(chatParticipantRepo).save(any(ChatParticipant.class));
    }

    @Test
    void removeUserFromGroup_throws_whenRemovingLastAdmin() {
        Chat chat = new Chat();
        chat.setChatType(ChatType.GROUP);

        when(chatRepo.findById(60L)).thenReturn(Optional.of(chat));

        ChatParticipant requester = new ChatParticipant();
        requester.setRole(ChatRole.ADMIN);
        when(chatParticipantRepo.findByChatChatIdAndUserId(60L, 1L)).thenReturn(Optional.of(requester));

        ChatParticipant participant = new ChatParticipant();
        participant.setRole(ChatRole.ADMIN);
        when(chatParticipantRepo.findByChatChatIdAndUserId(60L, 2L)).thenReturn(Optional.of(participant));

        when(chatParticipantRepo.countByChatChatIdAndRoleAndStatus(60L, ChatRole.ADMIN, ChatStatus.ACTIVE)).thenReturn(1L);

        assertThrows(ChatException.class, () -> chatService.removeUserFromGroup(60L, 2L, 1L));
    }

    @Test
    void renameGroup_updatesTitle_whenAdminAndNewNameDifferent() throws Exception {
        Chat chat = new Chat();
        chat.setChatType(ChatType.GROUP);
        chat.setChatStatus(ChatStatus.ACTIVE);
        chat.setTitle("OldName");

        when(chatRepo.findById(70L)).thenReturn(Optional.of(chat));

        ChatParticipant requester = new ChatParticipant();
        requester.setRole(ChatRole.ADMIN);
        when(chatParticipantRepo.findByChatChatIdAndUserId(70L, 1L)).thenReturn(Optional.of(requester));

        ChatResponse response = new ChatResponse(70L, null, null, null, null, null, null);
        when(chatMapper.mapToChatResponse(chat)).thenReturn(response);

        ChatResponse res = chatService.renameGroup(70L, "NewName", 1L);

        assertThat(chat.getTitle()).isEqualTo("NewName");
        assertThat(res.getChatId()).isEqualTo(70L);
    }

    @Test
    void archiveChat_setsArchived_whenActive() throws Exception {
        ChatParticipant participant = new ChatParticipant();
        participant.setStatus(ParticipantChatStatus.ACTIVE);

        when(chatParticipantRepo.findByChatChatIdAndUserId(80L, 1L)).thenReturn(Optional.of(participant));

        chatService.archiveChat(80L, 1L);

        assertThat(participant.getStatus()).isEqualTo(ParticipantChatStatus.ARCHIVED);
    }

}
