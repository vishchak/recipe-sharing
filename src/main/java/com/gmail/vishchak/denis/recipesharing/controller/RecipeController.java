package com.gmail.vishchak.denis.recipesharing.controller;

import com.gmail.vishchak.denis.recipesharing.dto.comment.CommentAddDTO;
import com.gmail.vishchak.denis.recipesharing.model.Comment;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeCreateRequest;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeCreateResponse;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeResponse;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeThumbnailResponse;
import com.gmail.vishchak.denis.recipesharing.service.CommentService;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final CommentService commentService;

    public RecipeController(RecipeService recipeService, CommentService commentService) {
        this.recipeService = recipeService;
        this.commentService = commentService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<RecipeThumbnailResponse>> getAllRecipes(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeThumbnailResponse> recipesPage = recipeService.getAllRecipes(pageable);
        return ResponseEntity.ok(recipesPage);
    }

    @PostMapping("/add")
    public ResponseEntity<RecipeCreateResponse> createRecipe(@Valid @RequestBody RecipeCreateRequest recipeCreateRequest, BindingResult bindingResult) {
        RecipeCreateResponse createdRecipe = recipeService.createRecipe(recipeCreateRequest, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long recipeId) {
        RecipeResponse recipe = recipeService.getRecipe(recipeId);
        return ResponseEntity.ok(recipe);
    }

    @PostMapping("/{recipeId}/rate")
    public ResponseEntity<?> addRatingToRecipe(@PathVariable("recipeId") Long recipeId, @RequestParam Long userId, @RequestParam int rating) {
        recipeService.rateRecipe(recipeId, userId, rating);
        return ResponseEntity.ok("Rating added successfully");
    }

    @PostMapping("/{recipeId}/comment")
    public ResponseEntity<?> addCommentToRecipe(@PathVariable("recipeId") Long recipeId, @RequestBody CommentAddDTO commentAddDTO) {
        Comment comment = commentService.saveComment(commentAddDTO, recipeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}
