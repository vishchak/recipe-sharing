package com.gmail.vishchak.denis.recipesharing.DTO;

import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
