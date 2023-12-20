package com.pikuco.userservice.controller;

import com.pikuco.userservice.dto.AuthenticationRequest;
import com.pikuco.userservice.dto.AuthenticationResponse;
import com.pikuco.userservice.dto.RegisterRequest;
import com.pikuco.userservice.exception.ObjectNotValidException;
import com.pikuco.userservice.service.AuthenticationService;
import com.pikuco.userservice.service.UserService;
import com.pikuco.userservice.validator.ObjectsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ObjectsValidator<RegisterRequest> registerValidator;
    private final ObjectsValidator<AuthenticationRequest> authenticationValidator;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        registerValidator.validate(request);
        if (request.getBirthdate() != null &&
                (request.getBirthdate().after(new Date(114, Calendar.JANUARY, 1)) ||
                        request.getBirthdate().before(new Date(0, Calendar.JANUARY, 1)))) {
            throw new ObjectNotValidException(new HashSet<String>(List.of("Дата народження не відповідає умовам реєстрації")));
        }
        if (userService.getUserByNickname(request.getUsername()).isPresent())
            throw new ObjectNotValidException(new HashSet<String>(List.of("Користувач з таким нікнеймом вже існує")));
        else if (userService.getUserByEmail(request.getEmail()).isPresent())
            throw new ObjectNotValidException(new HashSet<String>(List.of("Користувач з такою поштою вже існує")));
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request) {
        authenticationValidator.validate(request);
        if (userService.getUserByEmail(request.getEmail()).isEmpty())
            throw new ObjectNotValidException(new HashSet<String>(List.of("Користувача з такою поштою не існує")));
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}