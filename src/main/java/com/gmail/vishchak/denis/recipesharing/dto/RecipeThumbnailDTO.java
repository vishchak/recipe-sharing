package com.gmail.vishchak.denis.recipesharing.dto;

import com.gmail.vishchak.denis.recipesharing.model.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeThumbnailDTO {
    private Long id;
    private String title;
    private String image;
    private Integer cookingTime;
    private Double rating;
    private Difficulty difficulty;
}
