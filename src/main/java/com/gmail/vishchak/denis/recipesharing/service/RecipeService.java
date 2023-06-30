package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeCreateDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.model.Category;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    List<RecipeThumbnailDTO> getRecipesByTitle(String title);

    List<RecipeThumbnailDTO> getRecipesByMaxCookingTime(int maxCookingTime);

    List<RecipeThumbnailDTO> getRecipesByCategories(List<Category> categories);

    List<RecipeThumbnailDTO> getRecipesByIngredient(Long ingredientID);

    List<RecipeThumbnailDTO> getTopRatedRecipes(int limit);

    RecipeDTO getRecipeById(Long recipeId);

    Recipe createRecipe(RecipeCreateDTO recipeCreateDTO);

    Recipe updateRecipe(Long recipeId, Map<String, Object> fields);

    void deleteRecipe(Long recipeId);

    void rateRecipe(Long recipeId, Long userId, int rating);

    List<RecipeThumbnailDTO> getUserFavoriteRecipes(Long userId);

    List<RecipeThumbnailDTO> getUserSubmittedRecipes(Long userId);

    List<RecipeThumbnailDTO> getAllRecipes(int limit);
}
