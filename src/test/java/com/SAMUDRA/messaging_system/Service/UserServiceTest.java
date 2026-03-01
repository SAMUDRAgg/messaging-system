package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.Exception.UserException;
import com.SAMUDRA.messaging_system.Repo.UserRepo;


import com.SAMUDRA.messaging_system.enums.Role;
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

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder; // ✔ depend on interface

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("ram");
        user.setEmail("ram@gmail.com");
        user.setPassword("123");
        user.setProfilePicUrl("oldPic.jpg");

    }

    // ✅ SUCCESS CASE
    @Test
    void addUser_whenUsernameAndEmailAreUnique_shouldSaveUser() {

        when(userRepo.existsByUsername("ram")).thenReturn(false);
        when(userRepo.existsByEmail("ram@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.addUser(user);

        assertNotNull(savedUser);
        assertEquals("ram", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertTrue(savedUser.isEnabled());

        verify(userRepo).existsByUsername("ram");
        verify(userRepo).existsByEmail("ram@gmail.com");
        verify(passwordEncoder).encode("123");

        verify(userRepo).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertTrue(capturedUser.isEnabled());
    }

    // ✅ USERNAME EXISTS
    @Test
    void addUser_whenUsernameAlreadyExists_shouldThrowUserException() {

        when(userRepo.existsByUsername("ram")).thenReturn(true);

        UserException exception =
                assertThrows(UserException.class,
                        () -> userService.addUser(user));

        assertEquals("Username already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(userRepo).existsByUsername("ram");
        verifyNoInteractions(passwordEncoder);
        verify(userRepo, never()).save(any());
    }

    // ✅ EMAIL EXISTS
    @Test
    void addUser_whenEmailAlreadyExists_shouldThrowUserException() {

        when(userRepo.existsByUsername("ram")).thenReturn(false);
        when(userRepo.existsByEmail("ram@gmail.com")).thenReturn(true);

        UserException exception =
                assertThrows(UserException.class,
                        () -> userService.addUser(user));

        assertEquals("Email already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(userRepo).existsByUsername("ram");
        verify(userRepo).existsByEmail("ram@gmail.com");
        verifyNoInteractions(passwordEncoder);
        verify(userRepo, never()).save(any());
    }

    @Test
    void findById_whenUserExists_shouldReturnUser() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(1L);

        assertNotNull(foundUser);
        assertEquals("ram", foundUser.getUsername());

        verify(userRepo).findById(1L);
    }
    @Test
    void findById_whenUserDoesNotExist_shouldThrowUserException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        UserException exception =
                assertThrows(UserException.class,
                        () -> userService.findById(1L));

        assertEquals("User not found with id: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(userRepo).findById(1L);
    }
    @Test
    void findByUsernameOrEmail_whenUserExists_shouldReturnUser() {
        when(userRepo.findByUsernameOrEmail("ram")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsernameOrEmail("ram");

        assertNotNull(foundUser);
        assertEquals("ram", foundUser.getUsername());

        verify(userRepo).findByUsernameOrEmail("ram");
    }
    @Test
    void findByUsernameOrEmail_whenUserDoesNotExist_shouldThrowUserException() {
        when(userRepo.findByUsernameOrEmail("ram")).thenReturn(Optional.empty());

        UserException exception =
                assertThrows(UserException.class,
                        () -> userService.findByUsernameOrEmail("ram"));

        assertEquals("User not found with username/email: ram", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            verify(userRepo).findByUsernameOrEmail("ram");
    }

    @Test
    void updateUserById_whenAllFieldsValid_shouldUpdateSuccessfully() {

        User updated = new User();
        updated.setUsername("newRam");
        updated.setEmail("newram@gmail.com");
        updated.setPassword("newPass");
        updated.setProfilePicUrl("newPic.jpg");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        when(userRepo.existsByUsername("newRam")).thenReturn(false);
        when(userRepo.existsByEmail("newram@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUserById(1L, updated);

        assertEquals("newRam", result.getUsername());
        assertEquals("newram@gmail.com", result.getEmail());
        assertEquals("encodedPass", result.getPassword());
        assertEquals("newPic.jpg", result.getProfilePicUrl());

        verify(passwordEncoder).encode("newPass");
        verify(userRepo).save(any(User.class));
    }
    @Test
    void updateUserById_whenUsernameAlreadyTaken_shouldThrowException() {

        User updated = new User();
        updated.setUsername("newRam");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.existsByUsername("newRam")).thenReturn(true);

        UserException ex = assertThrows(UserException.class,
                () -> userService.updateUserById(1L, updated));

        assertEquals("Username already taken", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());

        verify(userRepo, never()).save(any());
    }
    @Test
    void updateUserById_whenEmailAlreadyTaken_shouldThrowException() {

        User updated = new User();
        updated.setEmail("newram@gmail.com");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail("newram@gmail.com")).thenReturn(true);

        UserException ex = assertThrows(UserException.class,
                () -> userService.updateUserById(1L, updated));

        assertEquals("Email already taken", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());

        verify(userRepo, never()).save(any());
    }

    @Test
    void updateUserById_whenPasswordProvided_shouldEncodePassword() {

        User updated = new User();
        updated.setPassword("newPass");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUserById(1L, updated);

        assertEquals("encodedPass", result.getPassword());
        verify(passwordEncoder).encode("newPass");
    }

    @Test
    void updateUserById_whenProfilePicProvided_shouldUpdatePic() {

        User updated = new User();
        updated.setProfilePicUrl("newPic.jpg");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUserById(1L, updated);

        assertEquals("newPic.jpg", result.getProfilePicUrl());
    }

    @Test
    void updateUserById_whenUsernameSame_shouldNotCheckExists() {

        User updated = new User();
        updated.setUsername("ram"); // same as existing

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.updateUserById(1L, updated);

        verify(userRepo, never()).existsByUsername(any());
    }

    @Test
    void updateUserById_whenFieldsAreNullOrBlank_shouldIgnoreThem() {

        User updated = new User();
        updated.setUsername("   ");  // blank

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUserById(1L, updated);

        assertEquals("ram", result.getUsername());
    }

    @Test
    void searchUser_whenUsersFound_shouldReturnUserList() {

        String query = "ram";

        List<User> mockUsers = List.of(
                new User( "ram", "123", "ram@gmail.com", "pic.jpg", Role.USER),
                new User("raman", "123", "raman@gmail.com", "pic.jpg", Role.USER)
        );

        when(userRepo
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query))
                .thenReturn(mockUsers);

        List<User> result = userService.searchUser(query);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ram", result.get(0).getUsername());

        verify(userRepo)
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);

        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void searchUser_whenNoUsersFound_shouldThrowUserException() {

        String query = "unknown";

        when(userRepo
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query))
                .thenReturn(List.of());

        UserException exception = assertThrows(
                UserException.class,
                () -> userService.searchUser(query)
        );

        assertEquals(
                "No users found matching query: " + query,
                exception.getMessage()
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(userRepo)
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

}