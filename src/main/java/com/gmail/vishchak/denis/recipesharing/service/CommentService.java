package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.dto.CommentAddDTO;
import com.gmail.vishchak.denis.recipesharing.model.Comment;

public interface CommentService {
    Comment saveComment(CommentAddDTO commentAddDTO, Long recipeId);
}
