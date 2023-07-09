package com.gmail.vishchak.denis.recipesharing.controller;

import com.gmail.vishchak.denis.recipesharing.dto.UserDTO;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(userDTOs);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        UserDTO userProfile = modelMapper.map(userService.getUserById(userId), UserDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(userProfile);
    }
}
