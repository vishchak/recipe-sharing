package com.gmail.vishchak.denis.recipesharing.dto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gmail.vishchak.denis.recipesharing.model.*;
import com.gmail.vishchak.denis.recipesharing.model.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    private String description;
    private String image;
    private Date date;
    private Difficulty difficulty;
    private Integer cookingTime;
    private Double rating;

    private UserThumbnailDTO user;

    @JsonManagedReference
    private Nutrition nutrition;

    @JsonManagedReference
    @JsonIdentityReference(alwaysAsId = true)
    private List<Category> categories;

    @JsonManagedReference
    @JsonIdentityReference(alwaysAsId = true)
    private List<Ingredient> ingredients;

    @JsonManagedReference
    private List<Comment> comments;
}
