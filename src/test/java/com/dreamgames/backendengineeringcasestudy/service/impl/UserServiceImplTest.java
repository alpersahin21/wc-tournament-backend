package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.exception.ApiBusinessException;
import com.dreamgames.backendengineeringcasestudy.model.request.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TournamentService tournamentService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        // Arrange
        CreateUserRequest requestDTO = new CreateUserRequest();
        requestDTO.setUsername("JohnDoe");

        doAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            userArg.setId("123"); // Simulate database assigning an ID
            return userArg;
        }).when(userRepository).save(any(User.class));

        // Act
        User result = userService.createUser(requestDTO);

        // Assert
        assertNotNull(result.getId(), "User ID should not be null.");
        assertEquals("JohnDoe", result.getUsername(), "Username should match the input.");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateLevel() {
        // Arrange
        String userId = "123";
        User user = new User();
        user.setId(userId);
        user.setLevel(1);
        user.setCoins(100);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.updateLevel(userId);

        // Assert
        assertEquals(2, result.getLevel(), "Level should be incremented by 1.");
        assertEquals(125, result.getCoins(), "Coins should be incremented by 25.");
        verify(tournamentService).updateUserScore(user);
        verify(userRepository).save(user);
    }

    @Test
    void testRetrieveUserById() {
        // Arrange
        String userId = "123";
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.retrieveUserById(userId);

        // Assert
        assertEquals(user, result, "Retrieved user should match the expected user.");
        verify(userRepository).findById(userId);
    }

    @Test
    void testRetrieveUserByIdNotFound() {
        // Arrange
        String userId = "123";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ApiBusinessException exception = assertThrows(ApiBusinessException.class, () -> userService.retrieveUserById(userId));
        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }
}
