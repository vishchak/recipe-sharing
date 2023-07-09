package com.gmail.vishchak.denis.recipesharing.serviceImpl;


import com.gmail.vishchak.denis.recipesharing.exception.custom.NoContentException;
import com.gmail.vishchak.denis.recipesharing.exception.custom.NotFoundException;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.repository.UserRepository;
import com.gmail.vishchak.denis.recipesharing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
