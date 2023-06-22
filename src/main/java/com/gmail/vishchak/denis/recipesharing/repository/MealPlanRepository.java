package com.gmail.vishchak.denis.recipesharing.repository;

import com.gmail.vishchak.denis.recipesharing.model.MealPlan;
import com.gmail.vishchak.denis.recipesharing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    MealPlan findByUser(User user);
}
