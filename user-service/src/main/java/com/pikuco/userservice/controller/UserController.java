package com.pikuco.userservice.controller;

import com.pikuco.userservice.dto.*;
import com.pikuco.userservice.mapper.UserMapper;
import com.pikuco.userservice.service.UserService;
import com.pikuco.userservice.validator.ObjectsValidator;
import com.pikuco.userservice.validator.PasswordToValidate;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private final ObjectsValidator<UserPublicToUpdateDto> userPublicValidator;
    private final ObjectsValidator<UserPrivacyToUpdateDto> userPrivacyValidator;
    private final ObjectsValidator<PasswordToValidate> passwordValidator;

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> showUsers() {
        List<UserDto> users = userService.getAllUsers()
                .stream().map(UserMapper::mapToUserDto).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping()
    public ResponseEntity<UserDto> showUserByToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        try {
            UserDto user = UserMapper.mapToUserDto(userService.getUserByToken(token));
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        }
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<UserProfileDto> showUserByNickname(@PathVariable String nickname) {
        UserProfileDto user;
        try {
            user = UserMapper.mapToUserProfileDto(userService
                    .getUserByNickname(nickname)
                    .orElseThrow(NoSuchElementException::new));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{nickname}/privacy")
    public ResponseEntity<UserPrivacyDto> showUserPrivacyByNickname(@RequestHeader("Authorization") String authHeader,
                                                                    @PathVariable String nickname) {
        String token = authHeader.substring(7);
        UserPrivacyDto user = UserMapper.mapToUserPrivacyDto(userService.getUserPrivacyByNickname(nickname, token));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id")
    public ResponseEntity<Long> showUserIdByToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        try {
            UserDto user = UserMapper.mapToUserDto(userService.getUserByToken(token));
            return ResponseEntity.ok(user.id());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        }
    }

    @PutMapping("/{nickname}")
    public ResponseEntity<?> editUserPublicInformation(@RequestHeader("Authorization") String authHeader,
                                                       @PathVariable String nickname,
                                                       @RequestBody UserPublicToUpdateDto newUser) {
        userPublicValidator.validate(newUser);
        String token = authHeader.substring(7);
        userService.updateUserPublicByNickname(token, UserMapper.mapToUser(newUser), nickname);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{nickname}/privacy")
    public ResponseEntity<?> editUserPrivacyInformation(@RequestHeader("Authorization") String authHeader,
                                                        @PathVariable String nickname,
                                                        @RequestBody UserPrivacyToUpdateDto newUser) {
        userPrivacyValidator.validate(newUser);
        if (!Objects.equals(newUser.currentPassword(), "") || !Objects.equals(newUser.newPassword(), "")) {
            PasswordToValidate currentPassword = new PasswordToValidate(newUser.currentPassword());
            PasswordToValidate newPassword = new PasswordToValidate(newUser.newPassword());
            passwordValidator.validate(currentPassword);
            passwordValidator.validate(newPassword);
        }
        String token = authHeader.substring(7);
        userService.updateUserPrivacyByNickname(token, newUser, nickname);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<?> deleteUserByNickname(@RequestHeader("Authorization") String authHeader,
                                                  @PathVariable String nickname,
                                                  @RequestParam(name="delete_quizzes",
                                                          required = false,
                                                          defaultValue = "true") boolean deleteQuizzes) {
        String token = authHeader.substring(7);
        userService.deleteUser(token, nickname, deleteQuizzes);
        return ResponseEntity.ok().build();
    }

}
