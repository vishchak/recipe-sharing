package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.DTO.UserAuthDTO;
import com.gmail.vishchak.denis.recipesharing.DTO.UserDTO;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;

import java.util.List;

public interface UserService {
    UserDTO registerUser(UserAuthDTO userAuthDTO);

    UserDTO getUserById(Long userId);

    UserDTO getUserByUsername(String username);

    UserDTO getUserByEmail(String email);

    UserDTO updateUser(UserDTO userDTO);

    void deleteUser(Long userId);

    void changePassword(Long userId, String currentPassword, String newPassword);

    List<UserDTO> getAllUsers();

    List<UserDTO> getUsersByRole(UserRole role);
}

