package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeCreateRequest;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeCreateResponse;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeResponse;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeThumbnailResponse;
import com.gmail.vishchak.denis.recipesharing.model.Category;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    RecipeCreateResponse createRecipe(RecipeCreateRequest recipeCreateRequest, BindingResult bindingResult);

    RecipeResponse getRecipe(Long recipeId);

    void rateRecipe(Long recipeId, Long userId, int rating);

    List<RecipeThumbnailResponse> getAllRecipes(int limit);

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
