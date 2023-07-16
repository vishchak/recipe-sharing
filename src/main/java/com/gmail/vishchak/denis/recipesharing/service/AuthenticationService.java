package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.dto.auth.AuthenticationRequest;
import com.gmail.vishchak.denis.recipesharing.dto.auth.AuthenticationResponse;
import com.gmail.vishchak.denis.recipesharing.dto.auth.RegisterRequest;
import com.gmail.vishchak.denis.recipesharing.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void saveUserToken(User user, String jwtToken);

    void revokeAllUserTokens(User user);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
