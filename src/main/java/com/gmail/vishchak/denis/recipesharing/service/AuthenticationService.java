package com.gmail.vishchak.denis.recipesharing.service;

import com.gmail.vishchak.denis.recipesharing.auth.AuthenticationRequest;
import com.gmail.vishchak.denis.recipesharing.auth.AuthenticationResponse;
import com.gmail.vishchak.denis.recipesharing.auth.RegisterRequest;

public interface AuthenticationService {
AuthenticationResponse register(RegisterRequest request);

AuthenticationResponse authenticate(AuthenticationRequest request);
}
