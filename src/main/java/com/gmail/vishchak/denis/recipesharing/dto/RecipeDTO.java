package com.gmail.vishchak.denis.recipesharing.dto;

import com.gmail.vishchak.denis.recipesharing.model.*;
import com.gmail.vishchak.denis.recipesharing.model.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    private String description;
    private String image;
    private Integer cookingTime;
    private Double rating;
    private Date date;
    private Difficulty difficulty;
    private User user;
    private Nutrition nutrition;
    private Set<Category> categories;
    private List<Ingredient> ingredients;
    private List<Comment> comments;
}
