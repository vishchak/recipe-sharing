package com.gmail.vishchak.denis.recipesharing.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentAddResponse {
    private Long commentId;
    private Long userId;
    private Long recipeId;
    private String content;
    private String image;
    private Date date;
}
