package com.gmail.vishchak.denis.recipesharing.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.gmail.vishchak.denis.recipesharing.dto.CommentAddDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeCreateDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.exception.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.Comment;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import com.gmail.vishchak.denis.recipesharing.service.CommentService;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

class RecipeControllerTest {
    @Mock
    private RecipeService recipeService;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private RecipeController recipeController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllRecipes_Successful() {
        int limit = 10;
        List<RecipeThumbnailDTO> recipes = new ArrayList<>();
        recipes.add(new RecipeThumbnailDTO(/* recipe data */));
        recipes.add(new RecipeThumbnailDTO(/* recipe data */));
        when(recipeService.getAllRecipes(limit)).thenReturn(recipes);

        ResponseEntity<List<RecipeThumbnailDTO>> response = recipeController.getAllRecipes(limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipes, response.getBody());
    }

    @Test
    public void testGetAllRecipes_NoContent() {
        int limit = 10;
        List<RecipeThumbnailDTO> recipes = new ArrayList<>();
        when(recipeService.getAllRecipes(limit)).thenReturn(recipes);

        ResponseEntity<List<RecipeThumbnailDTO>> response = recipeController.getAllRecipes(limit);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetAllRecipes_InternalServerError() {
        int limit = 10;
        when(recipeService.getAllRecipes(limit)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<List<RecipeThumbnailDTO>> response = recipeController.getAllRecipes(limit);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testCreateRecipe_Successful() {
        // Prepare test data
        RecipeCreateDTO recipeCreateDTO = new RecipeCreateDTO();
        // Set the necessary fields for creating a recipe

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Recipe createdRecipe = new Recipe();
        // Set the necessary fields for the created recipe

        when(recipeService.createRecipe(recipeCreateDTO)).thenReturn(createdRecipe);

        ResponseEntity<?> response = recipeController.createRecipe(recipeCreateDTO, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdRecipe, response.getBody());
    }


    @Test
    public void testCreateRecipe_InvalidRequest() {
        // Prepare test data
        RecipeCreateDTO recipeCreateDTO = new RecipeCreateDTO();
        // Set the necessary fields for creating a recipe

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = recipeController.createRecipe(recipeCreateDTO, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request data", response.getBody());
    }

    @Test
    public void testGetRecipeById_Success() {
        // Arrange
        Long recipeId = 1L;
        RecipeDTO expectedRecipe = new RecipeDTO();
        expectedRecipe.setId(recipeId);
        when(recipeService.getRecipeDtoById(recipeId)).thenReturn(expectedRecipe);

        // Act
        ResponseEntity<?> response = recipeController.getRecipeById(recipeId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRecipe, response.getBody());
    }

    @Test
    public void testGetRecipeById_NotFound() {
        // Arrange
        Long recipeId = 1L;
        when(recipeService.getRecipeDtoById(recipeId)).thenThrow(new NotFoundException("Recipe not found"));

        // Act
        ResponseEntity<?> response = recipeController.getRecipeById(recipeId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recipe not found", response.getBody());
    }

    @Test
    void addRatingToRecipe_Success() {
        // Arrange
        Long recipeId = 1L;
        Long userId = 2L;
        int rating = 4;

        // Act
        ResponseEntity<?> response = recipeController.addRatingToRecipe(recipeId, userId, rating);

        // Assert
        verify(recipeService).rateRecipe(eq(recipeId), eq(userId), eq(rating));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rating added successfully", response.getBody());
    }

    @Test
    void addRatingToRecipe_NotFound() {
        // Arrange
        Long recipeId = 1L;
        Long userId = 2L;
        int rating = 4;
        doThrow(NotFoundException.class).when(recipeService).rateRecipe(anyLong(), anyLong(), anyInt());

        // Act
        ResponseEntity<?> response = recipeController.addRatingToRecipe(recipeId, userId, rating);

        // Assert
        verify(recipeService).rateRecipe(eq(recipeId), eq(userId), eq(rating));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No recipe found", response.getBody());
    }

    @Test
    void testAddCommentToRecipe_Success() {
        // Arrange
        Long recipeId = 1L;
        CommentAddDTO commentDTO = new CommentAddDTO();
        commentDTO.setContent("Great recipe!");

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent(commentDTO.getContent());

        when(commentService.saveComment(any(CommentAddDTO.class))).thenReturn(savedComment);

        // Act
        ResponseEntity<?> response = recipeController.addCommentToRecipe(recipeId, commentDTO);

        // Assert
        verify(commentService, times(1)).saveComment(any(CommentAddDTO.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedComment, response.getBody());
    }

    @Test
    void testAddCommentToRecipe_NotFound() {
        // Arrange
        Long recipeId = 1L;
        CommentAddDTO commentDTO = new CommentAddDTO();
        commentDTO.setContent("Great recipe!");

        when(commentService.saveComment(any(CommentAddDTO.class))).thenThrow(new NotFoundException("Recipe not found"));

        // Act
        ResponseEntity<?> response = recipeController.addCommentToRecipe(recipeId, commentDTO);

        // Assert
        verify(commentService, times(1)).saveComment(any(CommentAddDTO.class));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recipe not found", response.getBody());
    }
}



