package com.pikuco.userservice.dto;

import java.time.LocalDateTime;
import java.util.Date;

public record UserProfileDto(
        String nickname,
        String role,
        String avatar,
        String description,
        Date birthdate,
        LocalDateTime creationDate
) {
}
