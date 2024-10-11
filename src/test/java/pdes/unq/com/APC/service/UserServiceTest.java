package pdes.unq.com.APC.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.exceptions.UserNotFoundException;
import pdes.unq.com.APC.interfaces.user.UserRequest;
import pdes.unq.com.APC.interfaces.user.UserResponse;
import pdes.unq.com.APC.repositories.UserRepository;
import pdes.unq.com.APC.services.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Test User");
        user.setEmail("email@gmail.com");
        user.setRoleType("common");
        user.setCreatedAt(LocalDateTime.now());

        userRequest = new UserRequest();
        userRequest.setEmail("email@gmail.com");
        userRequest.setUsername("Test User");
        userRequest.setPassword("password");
        userRequest.setRoleType("common");
    }

    @Test
    public void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse createdUser = userService.validateAndSaveUser(userRequest);

        assertNotNull(createdUser);
        assertEquals(user.getId().toString(), createdUser.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUserThrowsExceptionOnSaveError() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.validateAndSaveUser(userRequest);
        });

        assertTrue(exception.getMessage().contains("Database error"));
    }

     @Test
    public void testUpdateUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(user.getEmail(), userRequest);

        assertNotNull(updatedUser);
        assertEquals(user.getEmail(), updatedUser.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserNotFound() {
        when(userRepository.findByEmail("nonexistent@gmail.com")).thenReturn(null);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser("nonexistent@gmail.com", userRequest);
        });

        assertEquals("User with email nonexistent@gmail.com not found.", exception.getMessage());
    }

    @Test
    public void testGetUserByEmail() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(user);

        User foundUser = userService.getUserByEmail(user.getEmail());

        assertEquals(user.getUsername(), foundUser.getUsername());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("nonexistent@gmail.com")).thenReturn(null);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByEmail("nonexistent@gmail.com");
        });

        assertEquals("User with email nonexistent@gmail.com not found.", exception.getMessage());
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        userService.deleteUserByEmail(user.getEmail());

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.findByEmail("nonexistent@gmail.com")).thenReturn(null);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUserByEmail("nonexistent@gmail.com");
        });

        assertEquals("User with email nonexistent@gmail.com not found.", exception.getMessage());
    }
}
