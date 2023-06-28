package com.gmail.vishchak.denis.recipesharing.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gmail.vishchak.denis.recipesharing.dto.UserAuthDTO;
import com.gmail.vishchak.denis.recipesharing.dto.UserDTO;
import com.gmail.vishchak.denis.recipesharing.exception.BadRequestException;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import com.gmail.vishchak.denis.recipesharing.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserControllerTest {
    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Mock user registration request
        UserAuthDTO registrationDTO = new UserAuthDTO("testuser", "test@example.com", "password123");

        // Mock the UserService's registerUser method
        when(userService.registerUser(any(UserAuthDTO.class)))
                .thenReturn(new UserDTO(1L, "testuser", "test@example.com", null, UserRole.USER));

        // Call the registerUser method in the UserController
        ResponseEntity<UserDTO> response = userController.registerUser(registrationDTO);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());

        // Verify that the UserService's registerUser method was called
        verify(userService, times(1)).registerUser(registrationDTO);
    }

    @Test
    void testRegisterUser_Failure() {
        // Mock user registration request
        UserAuthDTO registrationDTO = new UserAuthDTO("testuser", "test@example.com", "password123");

        // Mock the UserService's registerUser method to throw an exception
        when(userService.registerUser(any(UserAuthDTO.class)))
                .thenThrow(new BadRequestException("Registration failed"));

        // Call the registerUser method in the UserController
        ResponseEntity<UserDTO> response = userController.registerUser(registrationDTO);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        // Verify that the UserService's registerUser method was called
        verify(userService, times(1)).registerUser(registrationDTO);
    }
}

