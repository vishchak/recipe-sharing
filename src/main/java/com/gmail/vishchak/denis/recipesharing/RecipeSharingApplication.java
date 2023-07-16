package com.gmail.vishchak.denis.recipesharing;

import com.gmail.vishchak.denis.recipesharing.dto.auth.RegisterRequest;
import com.gmail.vishchak.denis.recipesharing.model.enums.UserRole;
import com.gmail.vishchak.denis.recipesharing.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RecipeSharingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeSharingApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service
    ) {
        return args -> {
            var user = RegisterRequest.builder()
                    .username("User")
                    .email("user@mail.com")
                    .password("password")
                    .role(UserRole.USER)
                    .build();
            System.out.println("User token: " + service.register(user).getAccessToken());

            var admin = RegisterRequest.builder()
                    .username("Admin")
                    .email("admin@mail.com")
                    .password("password")
                    .role(UserRole.ADMIN)
                    .build();
            System.out.println("Admin token: " + service.register(admin).getAccessToken());
        };
    }
}
