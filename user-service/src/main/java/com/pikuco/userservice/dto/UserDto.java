package com.pikuco.userservice.dto;

import java.sql.Date;
import java.time.LocalDateTime;

public record UserDto(
    int id,
    String nickname,
    String email,
    String role,
    String avatar,
    String description,
    Date birthdate,
    LocalDateTime creationDate
) {}
