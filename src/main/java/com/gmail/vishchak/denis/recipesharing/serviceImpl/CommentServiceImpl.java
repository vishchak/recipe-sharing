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

import java.util.Date;

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
    public Comment saveComment(CommentAddDTO commentAddDTO, Long recipeId) {
        // Retrieve the recipe
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("Recipe not found with id: " + recipeId));

        // Retrieve the user
        User user = userRepository.findById(commentAddDTO.userId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + commentAddDTO.userId()));

        // Create a new comment
        Comment comment = new Comment();
        comment.setContent(commentAddDTO.content());
        comment.setImage(commentAddDTO.image());
        comment.setDate(new Date());
        comment.setRecipe(recipe);
        comment.setUser(user);

        // Save the comment
        return commentRepository.save(comment);
    }

}
