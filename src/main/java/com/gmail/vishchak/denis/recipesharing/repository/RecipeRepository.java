package com.gmail.vishchak.denis.recipesharing.repository;

import com.gmail.vishchak.denis.recipesharing.model.Category;
import com.gmail.vishchak.denis.recipesharing.model.Ingredient;
import com.gmail.vishchak.denis.recipesharing.model.Recipe;
import com.gmail.vishchak.denis.recipesharing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByTitleContainingIgnoreCase(String title);

    List<Recipe> findByUser(User user);

    List<Recipe> findByCookingTimeLessThanEqual(int maxCookingTime);

    @Query("""
            SELECT r FROM Recipe r
            JOIN r.categories c
            WHERE c IN :categories
            GROUP BY r
            HAVING COUNT(DISTINCT c) = :categoryCount
            """)
    List<Recipe> findByCategories(List<Category> categories);

    @Query("SELECT r FROM Recipe r JOIN r.ingredients i WHERE i = ?1")
    List<Recipe> findByIngredient(Ingredient ingredient);
}
