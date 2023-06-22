package com.gmail.vishchak.denis.recipesharing.repository;

import com.gmail.vishchak.denis.recipesharing.model.Comment;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByRecipe(Recipe recipe);
}
