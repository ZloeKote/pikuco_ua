package com.pikuco.userservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Past
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {
    String message() default "Дата повинна відповідати певному діапазону!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String minDate();
    String maxDate();
}
