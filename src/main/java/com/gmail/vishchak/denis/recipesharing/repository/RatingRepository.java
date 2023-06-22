package com.gmail.vishchak.denis.recipesharing.repository;

import com.gmail.vishchak.denis.recipesharing.model.Rating;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.recipe = :recipe")
    Double findAverageRatingByRecipe(Recipe recipe);
}
