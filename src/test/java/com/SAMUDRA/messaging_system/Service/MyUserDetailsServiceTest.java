package com.SAMUDRA.messaging_system.Service;

import com.SAMUDRA.messaging_system.DAO.User;
import com.SAMUDRA.messaging_system.Repo.UserRepo;
import com.SAMUDRA.messaging_system.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MyUserDetailsServiceTest {

    private MyUserDetailsService myUserDetailsService;

    @Mock
    private UserRepo userRepo;

    private User user;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        myUserDetailsService = new MyUserDetailsService(userRepo);

        user = new User();
        user.setId(1L);
        user.setUsername("ram");
        user.setEmail("ram@gmail.com");
        user.setPassword("123");
        user.setRole(Role.USER);
        user.setEnabled(true);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUsernameExists() {

        when(userRepo.findByUsername("ram"))
                .thenReturn(Optional.of(user));

        UserDetails result = myUserDetailsService.loadUserByUsername("ram");

        assertNotNull(result);
        assertEquals("ram", result.getUsername());

        verify(userRepo).findByUsername("ram");
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenEmailExists() {

        when(userRepo.findByUsername("ram@gmail.com"))
                .thenReturn(Optional.empty());

        when(userRepo.findByEmail("ram@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = myUserDetailsService.loadUserByUsername("ram@gmail.com");

        assertNotNull(result);
        assertEquals("ram", result.getUsername());

        verify(userRepo).findByUsername("ram@gmail.com");
        verify(userRepo).findByEmail("ram@gmail.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {

        when(userRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        when(userRepo.findByEmail("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                myUserDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDisabled() {

        user.setEnabled(false);

        when(userRepo.findByUsername("ram"))
                .thenReturn(Optional.of(user));

        assertThrows(UsernameNotFoundException.class, () ->
                myUserDetailsService.loadUserByUsername("ram"));
    }
}