package com.gmail.vishchak.denis.recipesharing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nutrition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double calories;
    private Double fat;
    private Double saturates;
    private Double carbs;
    private Double sugars;
    private Double fiber;
    private Double protein;
    private Double salt;

    @OneToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}

