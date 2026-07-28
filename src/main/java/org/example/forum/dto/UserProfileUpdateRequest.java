package org.example.forum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
        @NotBlank @Size(min = 4, max = 32) String firstName,
        @NotBlank @Size(min = 4, max = 32) String lastName,
        @NotBlank @Email String email,
        String phoneNumber,
        String profilePhotoUrl) {
}
