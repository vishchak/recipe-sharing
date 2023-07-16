package com.gmail.vishchak.denis.recipesharing.dto.auth;

import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
