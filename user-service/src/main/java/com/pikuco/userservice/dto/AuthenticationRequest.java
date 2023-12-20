package com.pikuco.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotNull(message = "Пошта не може бути порожньою")
    @NotEmpty(message = "Пошта не може бути порожньою")
    @NotBlank(message = "Пошта не може бути порожньою")
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Невірний формат пошти")
    private String email;
    @NotNull(message = "Пошта не може бути порожньою")
    @NotEmpty(message = "Пошта не може бути порожньою")
    @NotBlank(message = "Пошта не може бути порожньою")
    String password;
}
