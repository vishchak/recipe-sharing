package com.gmail.vishchak.denis.recipesharing.serviceImpl;

import com.gmail.vishchak.denis.recipesharing.dto.comment.CommentAddRequest;
import com.gmail.vishchak.denis.recipesharing.dto.comment.CommentAddResponse;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.Comment;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.repository.CommentRepository;
import com.gmail.vishchak.denis.recipesharing.repository.RecipeRepository;
import com.gmail.vishchak.denis.recipesharing.repository.UserRepository;
import com.gmail.vishchak.denis.recipesharing.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, RecipeRepository recipeRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentAddResponse saveComment(CommentAddRequest request, Long recipeId) {
        log.info("Saving a new comment for recipe with ID: {}", recipeId);

        // Retrieve the recipe
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found with id: " + recipeId));

        // Retrieve the user
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.userId()));

        // Create a new comment
        Comment comment = Comment.builder()
                .content(request.content())
                .image(request.image())
                .date(new Date())
                .recipe(recipe)
                .user(user)
                .build();

        // Save the comment
        commentRepository.save(comment);
        log.info("Comment saved with ID: {}", comment.getId());

        return CommentAddResponse.builder()
                .commentId(comment.getId())
                .userId(user.getId())
                .recipeId(recipe.getId())
                .content(comment.getContent())
                .image(comment.getImage())
                .date(comment.getDate())
                .build();
    }
}
