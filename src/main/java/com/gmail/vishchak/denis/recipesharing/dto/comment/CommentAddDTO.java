package com.gmail.vishchak.denis.recipesharing.dto.comment;


public record CommentAddDTO(
        String content,
        String image,
        Long userId
) {
}
