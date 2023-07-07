package com.gmail.vishchak.denis.recipesharing.serviceImpl;


import com.gmail.vishchak.denis.recipesharing.exception.custom.BadRequestException;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NoContentException;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.Role;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import com.gmail.vishchak.denis.recipesharing.repository.RoleRepository;
import com.gmail.vishchak.denis.recipesharing.repository.UserRepository;
import com.gmail.vishchak.denis.recipesharing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already taken");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username is already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        log.info("Saving new user {} to DB", user.getUsername());
        return savedUser;
    }


    @Override
    public User getUserByUsername(String username) {
        log.info("Retrieving user {} from DB", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User getUserById(Long id) {
        log.info("Retrieving user {} from DB", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Retrieving all users");

        List<User> userList = userRepository.findAll();

        if (userList.isEmpty()) {
            throw new NoContentException("No users found");
        }

        return userList;
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to DB", role.getRole());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, UserRole userRole) {
        log.info("Adding role {} to user {}", userRole, username);
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new NotFoundException("User not found"));
        Role role = roleRepository.findByRole(userRole);

        user.getRoles().add(role);
    }


}
