package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeDTO;
import com.gmail.vishchak.denis.recipesharing.model.Category;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    List<RecipeDTO> getRecipesByTitle(String title);

    List<RecipeDTO> getRecipesByMaxCookingTime(int maxCookingTime);

    List<RecipeDTO> getRecipesByCategories(List<Category> categories);

    List<RecipeDTO> getRecipesByIngredient(Long ingredientID);

    List<RecipeDTO> getTopRatedRecipes(int limit);

    Recipe getRecipeById(Long recipeId);

    Recipe createRecipe(Recipe recipe);

    Recipe updateRecipe(Long recipeId, Map<String, Object> fields);

    void deleteRecipe(Long recipeId);

    void rateRecipe(Long recipeId, Long userId, int rating);

    List<RecipeDTO> getUserFavoriteRecipes(Long userId);

    List<RecipeDTO> getUserSubmittedRecipes(Long userId);

    List<RecipeDTO> getAllRecipes(int limit);
}
