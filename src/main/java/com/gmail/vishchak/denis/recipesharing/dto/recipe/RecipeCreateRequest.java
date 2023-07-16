package com.gmail.vishchak.denis.recipesharing.dto.recipe;

import com.gmail.vishchak.denis.recipesharing.model.Nutrition;
import com.gmail.vishchak.denis.recipesharing.model.enums.Difficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String image;

    @NotNull(message = "Cooking time is required")
    @Min(value = 1, message = "Cooking time must be at least 1 minute")
    private Integer cookingTime;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotNull(message = "User is required")
    private Long userId;

    @NotNull(message = "Nutrition information is required")
    private Nutrition nutrition;

    @NotEmpty(message = "At least one category ID is required")
    private List<Long> categoryIds;

    @NotEmpty(message = "At least one ingredient ID is required")
    private List<Long> ingredientIds;
}
