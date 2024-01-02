package com.pikuco.userservice.service;

import com.pikuco.userservice.entity.Token;
import com.pikuco.userservice.repository.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) // means as a user is not logged in
            return;
        jwtToken = authHeader.substring(7);

        Token storedToken = tokenRepository.findByToken(jwtToken).orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
        Cookie jwtCookie = new Cookie(HttpHeaders.AUTHORIZATION, null);
        Cookie refreshFlag = new Cookie("LoggedIn", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        refreshFlag.setPath("/");
        jwtCookie.setMaxAge(0);
        refreshFlag.setMaxAge(0);

        response.addCookie(jwtCookie);
        response.addCookie(refreshFlag);
    }
}
