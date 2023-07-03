package com.gmail.vishchak.denis.recipesharing.controller;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.dto.UserAuthDTO;
import com.gmail.vishchak.denis.recipesharing.dto.UserDTO;
import com.gmail.vishchak.denis.recipesharing.service.RecipeService;
import com.gmail.vishchak.denis.recipesharing.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RecipeService recipeService;

    public UserController(UserService userService, RecipeService recipeService) {
        this.userService = userService;
        this.recipeService = recipeService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserAuthDTO userAuthDto) {
        UserDTO registeredUser = userService.registerUser(userAuthDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        UserDTO userProfile = userService.getUserDtoById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userProfile);
    }

    @GetMapping("/{userId}/recipes/submitted")
    public ResponseEntity<List<RecipeThumbnailDTO>> getUserSubmittedRecipes(@PathVariable Long userId) {
        List<RecipeThumbnailDTO> userRecipes = recipeService.getUserSubmittedRecipes(userId);
        return ResponseEntity.ok(userRecipes);
    }
}
