package com.pikuco.userservice.service;


import com.pikuco.userservice.dto.UserPrivacyToUpdateDto;
import com.pikuco.userservice.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User getUserByToken(String token);
    Optional<User> getUserByNickname(String nickname);
    User getUserPrivacyByNickname(String nickname, String token);
    Optional<User> getUserByEmail(String email);
    void updateUserPublicByNickname(String token, User newUser, String nickname);
    void updateUserPrivacyByNickname(String token, UserPrivacyToUpdateDto newUser, String nickname);
    void deleteUser(String token, String nickname, boolean deleteQuizzes);
}
