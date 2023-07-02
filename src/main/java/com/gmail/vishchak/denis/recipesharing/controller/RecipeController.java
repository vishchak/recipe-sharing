package com.gmail.vishchak.denis.recipesharing.controller;

import com.gmail.vishchak.denis.recipesharing.dto.CommentAddDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeCreateDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.exception.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.Comment;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import com.gmail.vishchak.denis.recipesharing.service.CommentService;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final CommentService commentService;

    public RecipeController(RecipeService recipeService, CommentService commentService) {
        this.recipeService = recipeService;
        this.commentService = commentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<RecipeThumbnailDTO>> getAllRecipes(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<RecipeThumbnailDTO> recipes = recipeService.getAllRecipes(limit);
            if (recipes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.ok(recipes);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> createRecipe(@Valid @RequestBody RecipeCreateDTO recipeCreateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Return validation error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
        }

        Recipe createdRecipe = recipeService.createRecipe(recipeCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long recipeId) {
        try {
            RecipeDTO recipe = recipeService.getRecipeDtoById(recipeId);
            return ResponseEntity.ok(recipe);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{recipeId}/rate")
    public ResponseEntity<?> addRatingToRecipe(@PathVariable Long recipeId, @RequestParam Long userId, @RequestParam int rating) {
        try {
            // Add the rating to the recipe
            recipeService.rateRecipe(recipeId, userId, rating);
            // Return a success response
            return ResponseEntity.ok("Rating added successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No recipe found");
        }
    }

    @PostMapping("/{recipeId}/comments")
    public ResponseEntity<?> addCommentToRecipe(@PathVariable Long recipeId, @RequestBody CommentAddDTO commentAddDTO) {
        try {
            // Save the comment
            commentAddDTO.setRecipeId(recipeId);
            Comment comment = commentService.saveComment(commentAddDTO);

            // Return a success response with the saved comment
            return ResponseEntity.ok(comment);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
