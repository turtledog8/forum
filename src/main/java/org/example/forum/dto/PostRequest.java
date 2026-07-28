package org.example.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequest(
        @NotBlank @Size(min = 16, max = 64) String title,
        @NotBlank @Size(min = 32, max = 8192) String content) {
}
