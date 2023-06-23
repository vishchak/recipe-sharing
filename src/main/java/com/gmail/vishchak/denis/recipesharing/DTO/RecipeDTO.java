package com.gmail.vishchak.denis.recipesharing.DTO;

import com.gmail.vishchak.denis.recipesharing.model.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Difficulty difficulty;
}
