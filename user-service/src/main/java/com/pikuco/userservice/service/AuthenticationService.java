package com.pikuco.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikuco.userservice.dto.AuthenticationRequest;
import com.pikuco.userservice.dto.AuthenticationResponse;
import com.pikuco.userservice.dto.RegisterRequest;
import com.pikuco.userservice.entity.Token;
import com.pikuco.userservice.entity.TokenType;
import com.pikuco.userservice.entity.User;
import com.pikuco.userservice.entity.UserRole;
import com.pikuco.userservice.exception.ObjectNotValidException;
import com.pikuco.userservice.repository.TokenRepository;
import com.pikuco.userservice.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        User user = User.builder()
                .nickname(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .avatar("some path")
                .birthdate(request.getBirthdate())
                .creationDate(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);

        setJwtCookie(response, refreshToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .avatar(savedUser.getAvatar())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new ObjectNotValidException(Collections.singleton("Ви ввели невірний пароль"));
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        setJwtCookie(response, refreshToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(HttpHeaders.AUTHORIZATION)) {
                refreshToken = cookie.getValue();
                break;
            }
        }
        final String username;
        if (refreshToken == null) { // means as a user is not logged in | (!authHeader.startsWith("Bearer ") - deprecated
            throw new ObjectNotValidException(Collections.singleton("Refresh token is not provided"));
        }
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            User user = userRepository.findByEmail(username).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .build();
            }
        }
        return new AuthenticationResponse();
    }

    private static void setJwtCookie(HttpServletResponse response, String refreshToken) {
        Cookie jwtCookie = new Cookie(HttpHeaders.AUTHORIZATION, refreshToken);
        Cookie refreshFlag = new Cookie("LoggedIn", "1");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        refreshFlag.setPath("/");
        jwtCookie.setMaxAge(60*24);
        refreshFlag.setMaxAge(60*24);

        response.addCookie(jwtCookie);
        response.addCookie(refreshFlag);
    }

//    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (token != null) {
//            User user = tokenRepository.findByToken(token).orElseThrow().getUser();
//            revokeAllUserTokens(user);
//        }
//        Cookie jwtCookie = new Cookie(HttpHeaders.AUTHORIZATION, "s");
//        Cookie refreshFlag = new Cookie("LoggedIn", "s");
//        jwtCookie.setHttpOnly(true);
//        jwtCookie.setSecure(true);
//        jwtCookie.setPath("/");
//        refreshFlag.setPath("/");
//        jwtCookie.setMaxAge(0);
//        refreshFlag.setMaxAge(0);
//
//        response.addCookie(jwtCookie);
//        response.addCookie(refreshFlag);
//    }
}
