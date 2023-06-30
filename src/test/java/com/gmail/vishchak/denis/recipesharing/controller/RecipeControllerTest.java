package com.gmail.vishchak.denis.recipesharing.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeDTO;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        List<RecipeDTO> recipes = new ArrayList<>();
        recipes.add(new RecipeDTO(/* recipe data */));
        recipes.add(new RecipeDTO(/* recipe data */));
        Mockito.when(recipeService.getAllRecipes(limit)).thenReturn(recipes);

        ResponseEntity<List<RecipeDTO>> response = recipeController.getAllRecipes(limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipes, response.getBody());
    }

    @Test
    public void testGetAllRecipes_NoContent() {
        int limit = 10;
        List<RecipeDTO> recipes = new ArrayList<>();
        Mockito.when(recipeService.getAllRecipes(limit)).thenReturn(recipes);

        ResponseEntity<List<RecipeDTO>> response = recipeController.getAllRecipes(limit);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetAllRecipes_InternalServerError() {
        int limit = 10;
        Mockito.when(recipeService.getAllRecipes(limit)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<List<RecipeDTO>> response = recipeController.getAllRecipes(limit);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}


