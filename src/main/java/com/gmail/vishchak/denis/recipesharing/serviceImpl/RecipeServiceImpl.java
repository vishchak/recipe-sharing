package com.gmail.vishchak.denis.recipesharing.serviceImpl;

import com.gmail.vishchak.denis.recipesharing.dto.*;
import com.gmail.vishchak.denis.recipesharing.exception.custom.BadRequestException;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NoContentException;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.*;
import com.gmail.vishchak.denis.recipesharing.repository.*;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final RatingRepository ratingRepository;

    private final CategoryRepository categoryRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository, IngredientRepository ingredientRepository, RatingRepository ratingRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.ratingRepository = ratingRepository;
        this.categoryRepository = categoryRepository;
    }

    private List<RecipeThumbnailDTO> mapRecipeListToDTO(List<Recipe> recipes) {
        if (recipes == null) {
            return Collections.emptyList();
        }

        return recipes.stream()
                .map(recipe -> mapRecipeToDTO(recipe, RecipeThumbnailDTO.class))
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
            ((RecipeDTO) dto).setUser(
                    new UserDTO(
                            recipe.getUser().getId(),
                            recipe.getUser().getUsername(),
                            recipe.getUser().getImage()
                    ));
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

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getRecipesByMaxCookingTime(int maxCookingTime) {
        List<Recipe> recipes = recipeRepository.findByCookingTimeLessThanEqual(maxCookingTime);

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getRecipesByCategories(List<Category> categories) {
        List<Recipe> recipes = recipeRepository.findByCategories(categories);

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getRecipesByIngredient(Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .map(Ingredient::getRecipes)
                .map(recipes -> {
                    if (!recipes.isEmpty()) {
                        return mapRecipeListToDTO(recipes);
                    } else {
                        throw new NoContentException("No recipes found for the ingredient");
                    }
                })
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getTopRatedRecipes(int limit) {
        List<Recipe> topRatedRecipes = recipeRepository.findTopRatedRecipesWithRatingGreaterThan(5, PageRequest.of(0, limit));

        return Optional.of(topRatedRecipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }


    @Override
    @Transactional(readOnly = true)
    public RecipeDTO getRecipeDtoById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        return mapRecipeToDTO(recipe, RecipeDTO.class);
    }


    @Override
    @Transactional
    public Recipe createRecipe(RecipeCreateDTO recipeCreateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid recipe data");
        }

        List<Category> categories = categoryRepository.findAllById(recipeCreateDTO.getCategoryIds());
        List<Ingredient> ingredients = ingredientRepository.findAllById(recipeCreateDTO.getIngredientIds());

        if (categories.isEmpty()) {
            throw new NotFoundException("Categories not found");
        }

        if (ingredients.isEmpty()) {
            throw new NotFoundException("Ingredients not found");
        }

        User user = userRepository.findById(recipeCreateDTO.getUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        Recipe recipe = new Recipe(
                recipeCreateDTO.getTitle(),
                recipeCreateDTO.getDescription(),
                recipeCreateDTO.getImage(),
                recipeCreateDTO.getCookingTime(),
                new Date(),
                recipeCreateDTO.getDifficulty(),
                recipeCreateDTO.getNutrition(),
                user,
                categories,
                ingredients);

        recipeCreateDTO.getNutrition().setRecipe(recipe);

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
                    throw new BadRequestException("Invalid field name: " + fieldName);
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

        return Optional.of(user.getFavorites())
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getUserSubmittedRecipes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Recipe> recipes = user.getRecipes();

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No submitted recipes found for the user"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeThumbnailDTO> getAllRecipes(int limit) {
        List<Recipe> recipes = recipeRepository.findAll(PageRequest.of(0, limit)).getContent();

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }
}
