package com.gmail.vishchak.denis.recipesharing.controller;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeDTO;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<RecipeDTO>> getAllRecipes(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<RecipeDTO> recipes = recipeService.getAllRecipes(limit);
            if (recipes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.ok(recipes);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
