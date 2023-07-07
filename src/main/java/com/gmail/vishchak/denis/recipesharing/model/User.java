package com.gmail.vishchak.denis.recipesharing.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "custom_user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private String image;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MealPlan mealPlan;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Recipe> recipes;

    @OneToMany(mappedBy = "user")
    private List<Collection> collections;

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<Reply> replies;
    @ManyToMany
    @JoinTable(
            name = "user_recipe_fav",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<Recipe> favorites;
}
