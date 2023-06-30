package com.gmail.vishchak.denis.recipesharing.dto;

import com.gmail.vishchak.denis.recipesharing.model.Category;
import com.gmail.vishchak.denis.recipesharing.model.Ingredient;
import com.gmail.vishchak.denis.recipesharing.model.Nutrition;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.model.enums.Difficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDTO {
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
    private User user;

    @NotNull(message = "Nutrition information is required")
    private Nutrition nutrition;

    @NotEmpty(message = "At least one category is required")
    private Set<Category> categories;

    @NotEmpty(message = "At least one ingredient is required")
    private List<Ingredient> ingredients;
}
