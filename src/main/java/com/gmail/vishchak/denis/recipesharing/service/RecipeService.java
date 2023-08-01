package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeCreateRequest;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeCreateResponse;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeGetResponse;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeThumbnailResponse;
import com.gmail.vishchak.denis.recipesharing.model.Category;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    RecipeCreateResponse createRecipe(RecipeCreateRequest recipeCreateRequest, BindingResult bindingResult);

    RecipeGetResponse getRecipe(Long recipeId);

    boolean rateRecipe(Long recipeId, Long userId, int rating);

    Page<RecipeThumbnailResponse> getAllRecipes(Pageable pageable);

    List<RecipeThumbnailResponse> getRecipesByTitle(String title);

    List<RecipeThumbnailResponse> getRecipesByMaxCookingTime(int maxCookingTime);

    List<RecipeThumbnailResponse> getRecipesByCategories(List<Category> categories);

    List<RecipeThumbnailResponse> getRecipesByIngredient(Long ingredientID);

    List<RecipeThumbnailResponse> getTopRatedRecipes(int limit);

    Recipe updateRecipe(Long recipeId, Map<String, Object> fields);

    void deleteRecipe(Long recipeId);

    List<RecipeThumbnailResponse> getUserFavoriteRecipes(Long userId);

    List<RecipeThumbnailResponse> getUserSubmittedRecipes(Long userId);
}
