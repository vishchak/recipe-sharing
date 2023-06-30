package com.gmail.vishchak.denis.recipesharing.serviceImpl;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeCreateDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeDTO;
import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.exception.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.*;
import com.gmail.vishchak.denis.recipesharing.repository.IngredientRepository;
import com.gmail.vishchak.denis.recipesharing.repository.RatingRepository;
import com.gmail.vishchak.denis.recipesharing.repository.RecipeRepository;
import com.gmail.vishchak.denis.recipesharing.repository.UserRepository;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
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

    private <T> List<T> mapRecipeListToDTO(List<Recipe> recipes, Class<T> dtoClass) {
        if (recipes == null) {
            return Collections.emptyList();
        }

        return recipes.stream()
                .map(recipe -> mapRecipeToDTO(recipe, dtoClass))
                .collect(Collectors.toList());
    }

    private <T> T mapRecipeToDTO(Recipe recipe, Class<T> dtoClass) {
        ModelMapper modelMapper = new ModelMapper();
        T dto = modelMapper.map(recipe, dtoClass);

        double averageRating = calculateAverageRating(recipe.getRatings());

        if (dto instanceof RecipeThumbnailDTO) {
            ((RecipeThumbnailDTO) dto).setRating(averageRating);
        } else if (dto instanceof RecipeDTO) {
            ((RecipeDTO) dto).setRating(averageRating);
        }

        return dto;
    }

    private double calculateAverageRating(List<Rating> ratings) {
        return ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getRecipesByTitle(String title) {
        List<Recipe> recipes = recipeRepository.findByTitleContainingIgnoreCase(title);

        return mapRecipeListToDTO(recipes, RecipeThumbnailDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getRecipesByMaxCookingTime(int maxCookingTime) {
        List<Recipe> recipes = recipeRepository.findByCookingTimeLessThanEqual(maxCookingTime);

        return mapRecipeListToDTO(recipes, RecipeThumbnailDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getRecipesByCategories(List<Category> categories) {
        List<Recipe> recipes = recipeRepository.findByCategories(categories);

        return mapRecipeListToDTO(recipes, RecipeThumbnailDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getRecipesByIngredient(Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .map(Ingredient::getRecipes)
                .map(recipes -> mapRecipeListToDTO(recipes, RecipeThumbnailDTO.class))
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getTopRatedRecipes(int limit) {
        List<Recipe> topRatedRecipes = recipeRepository.findTopRatedRecipesWithRatingGreaterThan(5, PageRequest.of(0, limit));
        return mapRecipeListToDTO(topRatedRecipes, RecipeThumbnailDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeDTO getRecipeById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        return mapRecipeToDTO(recipe, RecipeDTO.class);
    }

    @Override
    @Transactional
    public Recipe createRecipe(RecipeCreateDTO recipeCreateDTO) {
        if (recipeCreateDTO.getUser() == null) {
            throw new NotFoundException("User cannot be null");
        }

        Recipe recipe = new Recipe(
                recipeCreateDTO.getTitle(),
                recipeCreateDTO.getDescription(),
                recipeCreateDTO.getImage(),
                recipeCreateDTO.getCookingTime(),
                new Date(),
                recipeCreateDTO.getDifficulty(),
                recipeCreateDTO.getNutrition(),
                recipeCreateDTO.getUser(),
                recipeCreateDTO.getCategories(),
                recipeCreateDTO.getIngredients());

        return recipeRepository.save(recipe);
    }

    @Override
    @Transactional
    public Recipe updateRecipe(Long recipeId, Map<String, Object> fields) {
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        fields.forEach((fieldName, value) -> {
            if (Objects.nonNull(value)) {
                try {
                    Field field = Recipe.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(existingRecipe, value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException("Invalid field name: " + fieldName);
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
    public List<RecipeThumbnailDTO> getUserFavoriteRecipes(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        return mapRecipeListToDTO(user.getFavorites(), RecipeThumbnailDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getUserSubmittedRecipes(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        return mapRecipeListToDTO(user.getRecipes(), RecipeThumbnailDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getAllRecipes(int limit) {
        List<Recipe> recipes = recipeRepository.findAll(PageRequest.of(0, limit)).getContent();

        return mapRecipeListToDTO(recipes, RecipeThumbnailDTO.class);
    }
}
