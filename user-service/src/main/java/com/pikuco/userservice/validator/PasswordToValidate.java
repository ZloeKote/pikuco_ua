package com.pikuco.userservice.validator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PasswordToValidate (
        @NotNull(message = "Пароль не може бути порожнім")
        @NotEmpty(message = "Пароль не може бути порожнім")
        @NotBlank(message = "Пароль не може бути порожнім")
        @Pattern(regexp = "^(?=.*[a-zA-Z]).+$", message = "Пароль повинен мати хоча б одну літеру")
        @Pattern(regexp = ".*\\d.*", message = "Пароль повинен мати хоча б одну цифру")
        @Pattern(regexp = ".{6,20}", message = "Пароль повинен мати не менше 6 символів та не більше 20")
        String password
) {}
