package org.example.forum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.forum.model.User;

public record UserRegisterRequest(
        @NotBlank @Size(min = 4, max = 32) String firstName,
        @NotBlank @Size(min = 4, max = 32) String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(max = 64) String username,
        @NotBlank String password) {

    public User toEntity() {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}
