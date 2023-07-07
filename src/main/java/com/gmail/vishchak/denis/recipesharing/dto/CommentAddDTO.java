package com.gmail.vishchak.denis.recipesharing.dto;


public record CommentAddDTO(
        String content,
        String image,
        Long userId
) {
}
