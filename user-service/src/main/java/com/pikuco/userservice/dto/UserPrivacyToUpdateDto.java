package com.pikuco.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserPrivacyToUpdateDto(
        @NotNull(message = "Пошта не може бути порожньою")
        @NotEmpty(message = "Пошта не може бути порожньою")
        @NotBlank(message = "Пошта не може бути порожньою")
        @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$", message = "Невірний формат пошти")
        String email,
        String currentPassword,
        String newPassword
) {
}
