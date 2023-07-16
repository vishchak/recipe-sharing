package com.gmail.vishchak.denis.recipesharing.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateResponse {
    private String title;
    private Long recipeId;
    private Long userId;
    private Date date;
}
