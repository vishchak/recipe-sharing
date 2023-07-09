package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.model.User;

import java.util.List;

public interface UserService {
    User getUserByUsername(String username);

    User getUserById(Long id);


    List<User> getAllUsers();
}

