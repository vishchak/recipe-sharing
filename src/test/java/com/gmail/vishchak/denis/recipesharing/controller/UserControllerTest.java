package com.gmail.vishchak.denis.recipesharing.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.dto.UserAuthDTO;
import com.gmail.vishchak.denis.recipesharing.dto.UserDTO;
import com.gmail.vishchak.denis.recipesharing.exception.BadRequestException;
import com.gmail.vishchak.denis.recipesharing.exception.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import com.gmail.vishchak.denis.recipesharing.serviceImpl.RecipeServiceImpl;
import com.gmail.vishchak.denis.recipesharing.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;


class UserControllerTest {
    @Mock
    private UserServiceImpl userService;
    @Mock
    private RecipeServiceImpl recipeService;
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

    @Test
    public void testGetUserProfile_Success() {
        // Mock user and profile data
        User user = new User();
        user.setId(1L);
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");
        user.setRole(UserRole.USER);
        user.setImage("/images/user.png");
        // other user properties...

        UserDTO userProfile = new UserDTO(user.getId(),user.getUsername(),user.getEmail(),user.getImage(), user.getRole());

        when(userService.getUserDtoById(anyLong())).thenReturn(userProfile);

        ResponseEntity<?> response = userController.getUserProfile(user.getId());

        verify(userService, times(1)).getUserDtoById(eq(user.getId()));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userProfile, response.getBody());
    }

    @Test
    public void testGetUserProfile_UserNotFound() {
        long userId = 1L;
        when(userService.getUserDtoById(userId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.getUserDtoById(userId));
    }

    @Test
    void testGetUserSubmittedRecipes_Success() {
        // Mock user ID
        Long userId = 1L;

        // Mock recipe data
        RecipeThumbnailDTO recipe = new RecipeThumbnailDTO();
        recipe.setId(1L);
        List<RecipeThumbnailDTO> recipes = Collections.singletonList(recipe);

        // Mock service behavior
        when(recipeService.getUserSubmittedRecipes(userId)).thenReturn(recipes);

        // Invoke the endpoint
        ResponseEntity<?> response = userController.getUserSubmittedRecipes(userId);

        // Verify the service method invocation
        verify(recipeService, times(1)).getUserSubmittedRecipes(userId);

        // Assert the response status code and body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipes, response.getBody());
    }

    @Test
    void testGetUserSubmittedRecipes_UserNotFound() {
        // Mock user ID
        Long userId = 1L;

        // Mock service behavior
        when(recipeService.getUserSubmittedRecipes(userId)).thenThrow(NotFoundException.class);

        // Invoke the endpoint
        ResponseEntity<?> response = userController.getUserSubmittedRecipes(userId);

        // Verify the service method invocation
        verify(recipeService, times(1)).getUserSubmittedRecipes(userId);

        // Assert the response status code
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}


