package com.gmail.vishchak.denis.recipesharing.serviceImpl;

import com.gmail.vishchak.denis.recipesharing.dto.CommentAddDTO;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.Comment;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.repository.CommentRepository;
import com.gmail.vishchak.denis.recipesharing.repository.RecipeRepository;
import com.gmail.vishchak.denis.recipesharing.repository.UserRepository;
import com.gmail.vishchak.denis.recipesharing.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
    public Comment saveComment(CommentAddDTO commentAddDTO) {
        // Retrieve the recipe
        Recipe recipe = recipeRepository.findById(commentAddDTO.getRecipeId())
                .orElseThrow(() -> new NotFoundException("Recipe not found with id: " + commentAddDTO.getRecipeId()));

        // Retrieve the user
        User user = userRepository.findById(commentAddDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + commentAddDTO.getUserId()));

        // Create a new comment
        Comment comment = new Comment();
        comment.setContent(commentAddDTO.getContent());
        comment.setImage(commentAddDTO.getImage());
        comment.setDate(commentAddDTO.getDate());
        comment.setRecipe(recipe);
        comment.setUser(user);

        // Save the comment
        return commentRepository.save(comment);
    }

}
