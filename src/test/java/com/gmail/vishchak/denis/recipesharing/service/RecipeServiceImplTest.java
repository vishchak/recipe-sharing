package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeCreateDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.exception.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.Rating;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.repository.RatingRepository;
import com.gmail.vishchak.denis.recipesharing.repository.RecipeRepository;
import com.gmail.vishchak.denis.recipesharing.repository.UserRepository;
import com.gmail.vishchak.denis.recipesharing.serviceImpl.RecipeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRecipe() {
        // Arrange
        RecipeCreateDTO recipeCreateDTO = new RecipeCreateDTO();
        recipeCreateDTO.setTitle("Recipe Title");
        recipeCreateDTO.setDescription("Recipe Description");
        recipeCreateDTO.setUser(new User());
        // Set other properties as needed

        Recipe savedRecipe = new Recipe();
        savedRecipe.setId(1L);
        savedRecipe.setTitle(recipeCreateDTO.getTitle());
        savedRecipe.setDescription(recipeCreateDTO.getDescription());
        savedRecipe.setUser(recipeCreateDTO.getUser());
        // Set other properties as needed

        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

        // Act
        Recipe createdRecipe = recipeService.createRecipe(recipeCreateDTO);

        // Assert
        assertNotNull(createdRecipe);
        assertEquals(savedRecipe.getId(), createdRecipe.getId());
        assertEquals(recipeCreateDTO.getTitle(), createdRecipe.getTitle());
        assertEquals(recipeCreateDTO.getDescription(), createdRecipe.getDescription());
        // Assert other properties as needed

        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void testCreateRecipe_InvalidUser() {
        // Arrange
        RecipeCreateDTO recipeCreateDTO = new RecipeCreateDTO();
        recipeCreateDTO.setTitle("Recipe Title");
        recipeCreateDTO.setDescription("Recipe Description");
        // Set other properties as needed

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> recipeService.createRecipe(recipeCreateDTO));

        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void testUpdateRecipe() {
        // Arrange
        Long recipeId = 1L;
        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(recipeId);
        existingRecipe.setTitle("Existing Title");
        existingRecipe.setDescription("Existing Description");
        // Set other properties as needed

        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Updated Title");
        fields.put("description", "Updated Description");
        // Add other fields as needed

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

        // Act
        Recipe updatedRecipe = recipeService.updateRecipe(recipeId, fields);

        // Assert
        assertNotNull(updatedRecipe);
        assertEquals(recipeId, updatedRecipe.getId());
        assertEquals("Updated Title", updatedRecipe.getTitle());
        assertEquals("Updated Description", updatedRecipe.getDescription());
        // Assert other updated fields

        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, times(1)).save(existingRecipe);
    }

    @Test
    void testUpdateRecipe_RecipeNotFound() {
        // Arrange
        Long recipeId = 1L;
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "Updated Title");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> recipeService.updateRecipe(recipeId, fields));

        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void testDeleteRecipe() {
        // Arrange
        Long recipeId = 1L;

        // Act
        recipeService.deleteRecipe(recipeId);

        // Assert
        verify(recipeRepository, times(1)).deleteById(recipeId);
    }

    @Test
    void testRateRecipe() {
        // Arrange
        Long recipeId = 1L;
        Long userId = 1L;
        int rating = 5;

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        User user = new User();
        user.setId(userId);

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ratingRepository.findByRecipeAndUser(recipe, user)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        recipeService.rateRecipe(recipeId, userId, rating);

        // Assert
        verify(recipeRepository, times(1)).findById(recipeId);
        verify(userRepository, times(1)).findById(userId);
        verify(ratingRepository, times(1)).findByRecipeAndUser(recipe, user);
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void testGetUserFavoriteRecipes() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(new Recipe());
        when(recipeRepository.findAllById(any())).thenReturn(Collections.emptyList());

        // Act
        List<RecipeThumbnailDTO> favoriteRecipes = recipeService.getUserFavoriteRecipes(userId);

        // Assert
        assertNotNull(favoriteRecipes);
        assertEquals(0, favoriteRecipes.size());

        verify(userRepository, times(1)).findById(userId);
    }
}
