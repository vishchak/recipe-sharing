package com.gmail.vishchak.denis.recipesharing.repository;

import com.gmail.vishchak.denis.recipesharing.model.Meal;
import com.gmail.vishchak.denis.recipesharing.model.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByMealPlan(MealPlan mealPlan);

    List<Meal> findByMealPlanAndDateBetween(MealPlan mealPlan, Date startDate, Date endDate);
}
