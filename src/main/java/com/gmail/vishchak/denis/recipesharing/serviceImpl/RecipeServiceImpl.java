package com.gmail.vishchak.denis.recipesharing.serviceImpl;

import com.gmail.vishchak.denis.recipesharing.dto.user.UserDTO;
import com.gmail.vishchak.denis.recipesharing.exception.custom.BadRequestException;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NoContentException;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.*;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeCreateRequest;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeCreateResponse;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeResponse;
import com.gmail.vishchak.denis.recipesharing.dto.recipe.RecipeThumbnailResponse;
import com.gmail.vishchak.denis.recipesharing.repository.*;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final RatingRepository ratingRepository;
    private final CategoryRepository categoryRepository;


    private List<RecipeThumbnailResponse> mapRecipeListToDTO(List<Recipe> recipes) {
        if (recipes == null) {
            return Collections.emptyList();
        }

        return recipes.stream()
                .map(recipe -> mapRecipeToDTO(recipe, RecipeThumbnailResponse.class))
                .collect(Collectors.toList());
    }

    private <T> T mapRecipeToDTO(Recipe recipe, Class<T> dtoClass) {
        ModelMapper modelMapper = new ModelMapper();
        T dto = modelMapper.map(recipe, dtoClass);

        double averageRating = calculateAverageRating(recipe.getRatings());

        if (dto instanceof RecipeThumbnailResponse) {
            ((RecipeThumbnailResponse) dto).setRating(averageRating);
        } else if (dto instanceof RecipeResponse) {
            ((RecipeResponse) dto).setRating(averageRating);
            ((RecipeResponse) dto).setUser(
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
    public RecipeCreateResponse createRecipe(RecipeCreateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid recipe data");
        }

        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        List<Ingredient> ingredients = ingredientRepository.findAllById(request.getIngredientIds());

        if (categories.isEmpty()) {
            throw new NotFoundException("Categories not found");
        }

        if (ingredients.isEmpty()) {
            throw new NotFoundException("Ingredients not found");
        }

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException("User not found"));

        Recipe recipe = Recipe.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .image(request.getImage())
                .cookingTime(request.getCookingTime())
                .date(new Date())
                .difficulty(request.getDifficulty())
                .nutrition(request.getNutrition())
                .user(user)
                .categories(categories)
                .ingredients(ingredients)
                .build();

        log.info("Saving new recipe {} to DB", recipe.getTitle());
        request.getNutrition().setRecipe(recipe);
        recipeRepository.save(recipe);

        return RecipeCreateResponse.builder()
                .title(recipe.getTitle())
                .recipeId(recipe.getId())
                .userId(recipe.getUser().getId())
                .date(recipe.getDate())
                .build();
    }

    @Override
    public List<RecipeThumbnailResponse> getRecipesByTitle(String title) {
        List<Recipe> recipes = recipeRepository.findByTitleContainingIgnoreCase(title);

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }

    @Override
    public List<RecipeThumbnailResponse> getRecipesByMaxCookingTime(int maxCookingTime) {
        List<Recipe> recipes = recipeRepository.findByCookingTimeLessThanEqual(maxCookingTime);

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }

    @Override
    public List<RecipeThumbnailResponse> getRecipesByCategories(List<Category> categories) {
        List<Recipe> recipes = recipeRepository.findByCategories(categories);

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }

    @Override
    public List<RecipeThumbnailResponse> getRecipesByIngredient(Long ingredientId) {
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
    public List<RecipeThumbnailResponse> getTopRatedRecipes(int limit) {
        List<Recipe> topRatedRecipes = recipeRepository.findTopRatedRecipesWithRatingGreaterThan(5, PageRequest.of(0, limit));

        return Optional.of(topRatedRecipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }


    @Override
    public RecipeResponse getRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        return mapRecipeToDTO(recipe, RecipeResponse.class);
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
    public void deleteRecipe(Long recipeId) {
        recipeRepository.deleteById(recipeId);
    }

    @Override
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
    public List<RecipeThumbnailResponse> getUserFavoriteRecipes(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        return Optional.of(user.getFavorites())
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No recipes found"));
    }

    @Override
    public List<RecipeThumbnailResponse> getUserSubmittedRecipes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Recipe> recipes = user.getRecipes();

        return Optional.of(recipes)
                .filter(list -> !list.isEmpty())
                .map(this::mapRecipeListToDTO)
                .orElseThrow(() -> new NoContentException("No submitted recipes found for the user"));
    }

    @Override
    public Page<RecipeThumbnailResponse> getAllRecipes(Pageable pageable) {
        Page<Recipe> recipesPage = recipeRepository.findAll(pageable);

        if (recipesPage.isEmpty()) {
            throw new NoContentException("No recipes found");
        }

        List<RecipeThumbnailResponse> recipeDTOs = recipesPage.getContent()
                .stream()
                .map(recipe -> mapRecipeToDTO(recipe, RecipeThumbnailResponse.class))
                .collect(Collectors.toList());

        return new PageImpl<>(recipeDTOs, pageable, recipesPage.getTotalElements());
    }
}
