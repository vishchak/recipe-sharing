package com.gmail.vishchak.denis.recipesharing.serviceImpl;

import com.gmail.vishchak.denis.recipesharing.auth.AuthenticationRequest;
import com.gmail.vishchak.denis.recipesharing.auth.AuthenticationResponse;
import com.gmail.vishchak.denis.recipesharing.auth.RegisterRequest;
import com.gmail.vishchak.denis.recipesharing.config.JwtService;
import com.gmail.vishchak.denis.recipesharing.exception.custom.BadRequestException;
import com.gmail.vishchak.denis.recipesharing.model.User;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import com.gmail.vishchak.denis.recipesharing.repository.UserRepository;
import com.gmail.vishchak.denis.recipesharing.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }

        if (repository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        log.info("Saving new user {} to DB", user.getUsername());
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),
                        request.getPassword())
        );

        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();

        log.info("Authenticating user {}", user.getUsername());

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
