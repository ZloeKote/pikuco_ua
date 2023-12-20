package com.pikuco.userservice.service;


import com.pikuco.userservice.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
        User getUserByToken(String token);
    Optional<User> getUserByNickname(String nickname);
    Optional<User> getUserByEmail(String email);
}
