package com.gmail.vishchak.denis.recipesharing.serviceImpl;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeDTO;
import com.gmail.vishchak.denis.recipesharing.exception.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.*;
import com.gmail.vishchak.denis.recipesharing.model.enums.Difficulty;
import com.gmail.vishchak.denis.recipesharing.repository.IngredientRepository;
import com.gmail.vishchak.denis.recipesharing.repository.RatingRepository;
import com.gmail.vishchak.denis.recipesharing.repository.RecipeRepository;
import com.gmail.vishchak.denis.recipesharing.repository.UserRepository;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final RatingRepository ratingRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository, IngredientRepository ingredientRepository, RatingRepository ratingRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.ratingRepository = ratingRepository;
    }

    private List<RecipeDTO> mapRecipeListToDTO(List<Recipe> recipes) {
        return recipes.stream()
                .map(this::mapRecipeToDTO)
                .collect(Collectors.toList());
    }

    private RecipeDTO mapRecipeToDTO(Recipe recipe) {
        ModelMapper modelMapper = new ModelMapper();
        RecipeDTO recipeDTO = modelMapper.map(recipe, RecipeDTO.class);

        double averageRating = calculateAverageRating(recipe.getRatings());

        recipeDTO.setRating(averageRating);

        return recipeDTO;
    }

    private double calculateAverageRating(List<Rating> ratings) {
        return ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTO> getRecipesByTitle(String title) {
        List<Recipe> recipes = recipeRepository.findByTitleContainingIgnoreCase(title);

        return mapRecipeListToDTO(recipes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTO> getRecipesByMaxCookingTime(int maxCookingTime) {
        List<Recipe> recipes = recipeRepository.findByCookingTimeLessThanEqual(maxCookingTime);

        return mapRecipeListToDTO(recipes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTO> getRecipesByCategories(List<Category> categories) {
        List<Recipe> recipes = recipeRepository.findByCategories(categories);

        return mapRecipeListToDTO(recipes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTO> getRecipesByIngredient(Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .map(Ingredient::getRecipes)
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTO> getTopRatedRecipes(int limit) {
        List<Recipe> topRatedRecipes = recipeRepository.findTopRatedRecipesWithRatingGreaterThan(5, PageRequest.of(0, limit));
        return mapRecipeListToDTO(topRatedRecipes);
    }

    @Override
    @Transactional(readOnly = true)
    public Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));
    }

    @Override
    @Transactional
    public Recipe createRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    @Transactional
    public Recipe updateRecipe(Long recipeId, Map<String, Object> fields) {
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        fields.forEach((fieldName, value) -> {
            if (Objects.nonNull(value)) {
                switch (fieldName) {
                    case "title" -> existingRecipe.setTitle((String) value);
                    case "description" -> existingRecipe.setDescription((String) value);
                    case "image" -> existingRecipe.setImage((String) value);
                    case "cookingTime" -> existingRecipe.setCookingTime((Integer) value);
                    case "difficulty" -> existingRecipe.setDifficulty((Difficulty) value);
                    case "nutrition" -> existingRecipe.setNutrition((Nutrition) value);
                    case "categories" -> existingRecipe.setCategories((Set<Category>) value);
                    case "ingredients" -> existingRecipe.setIngredients((List<Ingredient>) value);
                    default -> throw new IllegalArgumentException("Invalid field name: " + fieldName);
                }
            }
        });

        return recipeRepository.save(existingRecipe);
    }


    @Override
    @Transactional
    public void deleteRecipe(Long recipeId) {
        recipeRepository.deleteById(recipeId);
    }

    @Override
    @Transactional
    public void rateRecipe(Long recipeId, Long userId, int rating) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found with id: " + recipeId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Optional<Rating> existingRating = ratingRepository.findByRecipeAndUser(recipe, user);

        if (existingRating.isPresent()) {
            Rating ratingEntity = existingRating.get();
            ratingEntity.setValue(rating);
            ratingRepository.save(ratingEntity);
        } else {
            ratingRepository.save(new Rating(rating, recipe, user));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTO> getUserFavoriteRecipes(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        return mapRecipeListToDTO(user.getFavorites());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTO> getUserSubmittedRecipes(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        return mapRecipeListToDTO(user.getRecipes());
    }
}
