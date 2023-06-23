package com.gmail.vishchak.denis.recipesharing.model;

import com.gmail.vishchak.denis.recipesharing.model.enums.Difficulty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String image;

    private Integer cookingTime;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL)
    private Nutrition nutrition;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "recipe_category",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "recipe_ingredient",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients;

    @OneToMany(mappedBy = "recipe")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "recipe")
    private List<Comment> comments;

    @OneToMany(mappedBy = "recipe")
    private List<Meal> meals;

    @ManyToMany(mappedBy = "favorites")
    private List<User> users;

    @ManyToMany(mappedBy = "recipes")
    private List<Collection> collections;

    public Recipe(String title, String description, String image, Integer cookingTime, Date date, Difficulty difficulty, Nutrition nutrition, User user, Set<Category> categories, List<Ingredient> ingredients) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.cookingTime = cookingTime;
        this.date = date;
        this.difficulty = difficulty;
        this.nutrition = nutrition;
        this.user = user;
        this.categories = categories;
        this.ingredients = ingredients;
    }
}
