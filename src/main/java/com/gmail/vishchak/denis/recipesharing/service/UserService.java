package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.model.Role;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;

import java.util.List;

public interface UserService {
    User registerUser(String username, String email, String password);

    User getUserByUsername(String username);

    User getUserById(Long id);


    List<User> getAllUsers();

    Role saveRole(Role role);

    void addRoleToUser(String username, UserRole userRole);
}

