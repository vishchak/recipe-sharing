package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.dto.comment.CommentAddRequest;
import com.gmail.vishchak.denis.recipesharing.dto.comment.CommentAddResponse;

public interface CommentService {
    CommentAddResponse saveComment(CommentAddRequest request, Long recipeId);
}
