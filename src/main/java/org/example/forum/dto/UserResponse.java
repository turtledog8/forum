package org.example.forum.dto;

import java.time.LocalDateTime;
import org.example.forum.model.Role;
import org.example.forum.model.User;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String username,
        String phoneNumber,
        String profilePhotoUrl,
        Role role,
        boolean blocked,
        LocalDateTime createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getPhoneNumber(),
                user.getProfilePhotoUrl(),
                user.getRole(),
                user.isBlocked(),
                user.getCreatedAt());
    }
}
