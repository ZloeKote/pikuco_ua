package com.pikuco.userservice.controller;

import com.pikuco.userservice.dto.UserDto;
import com.pikuco.userservice.mapper.UserMapper;
import com.pikuco.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;

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
}
