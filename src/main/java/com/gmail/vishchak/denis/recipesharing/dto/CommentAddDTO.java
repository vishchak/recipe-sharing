package com.gmail.vishchak.denis.recipesharing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentAddDTO {
    private String content;

    private String image;

    private Long recipeId;

    private Long userId;

    private Date date;
}
