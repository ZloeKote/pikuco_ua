package com.pikuco.userservice.controller;

import com.pikuco.userservice.dto.AuthenticationRequest;
import com.pikuco.userservice.dto.AuthenticationResponse;
import com.pikuco.userservice.dto.RegisterRequest;
import com.pikuco.userservice.exception.ObjectNotValidException;
import com.pikuco.userservice.service.AuthenticationService;
import com.pikuco.userservice.service.UserService;
import com.pikuco.userservice.validator.ObjectsValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request,
                                                           HttpServletResponse response) {
        registerValidator.validate(request);
        if (request.getBirthdate() != null &&
                (request.getBirthdate().after(new Date(114, Calendar.JANUARY, 1)) ||
                        request.getBirthdate().before(new Date(0, Calendar.JANUARY, 1)))) {
            throw new ObjectNotValidException(new HashSet<>(List.of("Birthdate doesn't meet the registration requirements")));
        }
        if (userService.getUserByNickname(request.getUsername()).isPresent())
            throw new ObjectNotValidException(new HashSet<>(List.of("A user with the same nickname is already existed")));
        else if (userService.getUserByEmail(request.getEmail()).isPresent())
            throw new ObjectNotValidException(new HashSet<>(List.of("A user with the same email is already existed")));

        return ResponseEntity.ok(authenticationService.register(request, response));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request,
                                                               HttpServletResponse httpResponse) {
        authenticationValidator.validate(request);
        if (userService.getUserByEmail(request.getEmail()).isEmpty())
            throw new ObjectNotValidException(new HashSet<>(List.of("The user with entered email does not exist")));
        return ResponseEntity.ok(authenticationService.authenticate(request, httpResponse));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}
