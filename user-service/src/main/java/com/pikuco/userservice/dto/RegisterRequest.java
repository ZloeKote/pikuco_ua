package com.pikuco.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message = "Нікнейм не може бути порожнім")
    @NotEmpty(message = "Нікнейм не може бути порожнім")
    @NotBlank(message = "Нікнейм не може бути порожнім")
    @Pattern(regexp = ".{3,25}", message = "Нікнейм повинен мати не менше 3 символів та не більше 25")
    @Pattern(regexp = "^(?!.*[.,\\\\/'\"`()\\[\\]{}*]).*$",
            message = "Нікнейм не повинен містити такі спеціальні символи як: .,/'\"`()[]{}*")
    private String username;
    @NotNull(message = "Пошта не може бути порожньою")
    @NotEmpty(message = "Пошта не може бути порожньою")
    @NotBlank(message = "Пошта не може бути порожньою")
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Невірний формат пошти")
    private String email;
    @NotNull(message = "Пароль не може бути порожнім")
    @NotEmpty(message = "Пароль не може бути порожнім")
    @NotBlank(message = "Пароль не може бути порожнім")
    @Pattern(regexp = "^(?=.*[a-zA-Z]).+$", message = "Пароль повинен мати хоча б одну літеру")
    @Pattern(regexp = ".*\\d.*", message = "Пароль повинен мати хоча б одну цифру")
    @Pattern(regexp = ".{6,20}", message = "Пароль повинен мати не менше 6 символів та не більше 20")
    private String password;
    private Date birthdate;
}
