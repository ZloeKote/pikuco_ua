package com.pikuco.userservice.service;

import com.pikuco.userservice.entity.Token;
import com.pikuco.userservice.entity.User;
import com.pikuco.userservice.repository.TokenRepository;
import com.pikuco.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByToken(String token) {
        Token foundToken = tokenRepository.findByToken(token).orElse(null);
        if (foundToken == null)
            throw new NoSuchElementException();
        return userRepository.findById(foundToken.getUser().getId()).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<User> getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
