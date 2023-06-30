package com.gmail.vishchak.denis.recipesharing.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeCreateDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
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
        Mockito.when(recipeService.getAllRecipes(limit)).thenReturn(recipes);

        ResponseEntity<List<RecipeThumbnailDTO>> response = recipeController.getAllRecipes(limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipes, response.getBody());
    }

    @Test
    public void testGetAllRecipes_NoContent() {
        int limit = 10;
        List<RecipeThumbnailDTO> recipes = new ArrayList<>();
        Mockito.when(recipeService.getAllRecipes(limit)).thenReturn(recipes);

        ResponseEntity<List<RecipeThumbnailDTO>> response = recipeController.getAllRecipes(limit);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetAllRecipes_InternalServerError() {
        int limit = 10;
        Mockito.when(recipeService.getAllRecipes(limit)).thenThrow(new RuntimeException("Internal server error"));

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
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        Recipe createdRecipe = new Recipe();
        // Set the necessary fields for the created recipe

        Mockito.when(recipeService.createRecipe(recipeCreateDTO)).thenReturn(createdRecipe);

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
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = recipeController.createRecipe(recipeCreateDTO, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request data", response.getBody());
    }

}


