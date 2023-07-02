package com.gmail.vishchak.denis.recipesharing.controller;

import com.gmail.vishchak.denis.recipesharing.dto.RecipeThumbnailDTO;
import com.gmail.vishchak.denis.recipesharing.dto.UserAuthDTO;
import com.gmail.vishchak.denis.recipesharing.dto.UserDTO;
import com.gmail.vishchak.denis.recipesharing.exception.BadRequestException;
import com.gmail.vishchak.denis.recipesharing.exception.NotFoundException;
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
        try {
            UserDTO registeredUser = userService.registerUser(userAuthDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (BadRequestException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        try {
            UserDTO userProfile = userService.getUserDtoById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(userProfile);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/users/{userId}/recipes/submitted")
    public ResponseEntity<List<RecipeThumbnailDTO>> getUserSubmittedRecipes(@PathVariable Long userId) {
        try {
            List<RecipeThumbnailDTO> userRecipes = recipeService.getUserSubmittedRecipes(userId);
            return ResponseEntity.ok(userRecipes);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
