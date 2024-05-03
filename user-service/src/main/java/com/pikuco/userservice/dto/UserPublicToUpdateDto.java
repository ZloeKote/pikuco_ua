package com.pikuco.userservice.dto;

import jakarta.validation.constraints.*;

public record UserPublicToUpdateDto(
        @NotNull(message = "Нікнейм не може бути порожнім")
        @NotEmpty(message = "Нікнейм не може бути порожнім")
        @NotBlank(message = "Нікнейм не може бути порожнім")
        @Pattern(regexp = ".{3,25}", message = "Нікнейм повинен мати не менше 3 символів та не більше 25")
        @Pattern(regexp = "^(?!.*[.,\\\\/'\"`()\\[\\]{}*]).*$",
                message = "Нікнейм не повинен містити такі спеціальні символи як: .,/'\"`()[]{}*")
        String nickname,
        @Size(max = 255, message = "Максимальна довжина опису складає 255 символів")
        String description
) {
}
