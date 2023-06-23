package com.gmail.vishchak.denis.recipesharing.repository;

import com.gmail.vishchak.denis.recipesharing.model.Category;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByTitleContainingIgnoreCase(String title);

    List<Recipe> findByCookingTimeLessThanEqual(int maxCookingTime);

    @Query("""
            SELECT r FROM Recipe r
            JOIN r.categories c
            WHERE c IN :categories
            GROUP BY r
            HAVING COUNT(DISTINCT c) = :categoryCount
            """)
    List<Recipe> findByCategories(List<Category> categories);

    @Query("SELECT r FROM Recipe r JOIN r.ratings rt WHERE rt.value > :rating")
    List<Recipe> findTopRatedRecipesWithRatingGreaterThan(int rating, Pageable pageable);
}
