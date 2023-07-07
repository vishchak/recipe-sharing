package com.gmail.vishchak.denis.recipesharing.controller;

import com.gmail.vishchak.denis.recipesharing.dto.*;
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
        List<RecipeThumbnailDTO> recipes = recipeService.getAllRecipes(limit);
        return ResponseEntity.ok(recipes);
    }

    @PostMapping("/add")
    public ResponseEntity<?> createRecipe(@Valid @RequestBody RecipeCreateDTO recipeCreateDTO, BindingResult bindingResult) {
        Recipe createdRecipe = recipeService.createRecipe(recipeCreateDTO, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long recipeId) {
        RecipeDTO recipe = recipeService.getRecipeDtoById(recipeId);
        return ResponseEntity.ok(recipe);
    }

    @PostMapping("/{recipeId}/rate")
    public ResponseEntity<?> addRatingToRecipe(@PathVariable("recipeId") Long recipeId, @RequestParam Long userId, @RequestParam int rating) {
        recipeService.rateRecipe(recipeId, userId, rating);
        return ResponseEntity.ok("Rating added successfully");
    }

    @PostMapping("/{recipeId}/comment")
    public ResponseEntity<?> addCommentToRecipe(@PathVariable("recipeId") Long recipeId, @RequestBody CommentAddDTO commentAddDTO) {
        commentAddDTO.setRecipeId(recipeId);
        Comment comment = commentService.saveComment(commentAddDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}
