package com.gmail.vishchak.denis.recipesharing.controller;

import com.gmail.vishchak.denis.recipesharing.dto.UserAuthDTO;
import com.gmail.vishchak.denis.recipesharing.dto.UserDTO;
import com.gmail.vishchak.denis.recipesharing.model.Role;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import com.gmail.vishchak.denis.recipesharing.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
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

    @PostMapping("/register")
    public ResponseEntity<UserAuthDTO> registerUser(@RequestBody UserAuthForm form) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/register").toUriString());
        User registeredUser = userService.registerUser(form.username, form.email, form.password);

        return ResponseEntity.created(uri).body(
                modelMapper.map(registeredUser, UserAuthDTO.class));
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/role/save").toUriString());

        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> saveRole(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.username, form.role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        UserDTO userProfile = modelMapper.map(userService.getUserById(userId), UserDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(userProfile);
    }

    record RoleToUserForm(String username,
                          UserRole role) {
    }

    record UserAuthForm(String username,
                        String email,
                        String password) {
    }
}
