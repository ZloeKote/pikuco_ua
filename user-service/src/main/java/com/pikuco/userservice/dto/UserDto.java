package com.pikuco.userservice.dto;

import java.time.LocalDateTime;
import java.util.Date;

public record UserDto(
    Long id,
    String nickname,
    String email,
    String role,
    String avatar,
    String description,
    Date birthdate,
    LocalDateTime creationDate
) {}
