package com.gmail.vishchak.denis.recipesharing.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
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

    @Override
    public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
