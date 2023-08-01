package com.gmail.vishchak.denis.recipesharing.dto.comment;


public record CommentAddRequest(
        String content,
        String image,
        Long userId
) {
}
