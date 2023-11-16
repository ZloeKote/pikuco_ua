package com.pikuco.dbgateway.controller;

import com.pikuco.dbgateway.mapper.UserMapper;
import com.pikuco.dbgateway.service.UserService;
import com.pikuco.sharedComps.userService.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/db/users")
@AllArgsConstructor
public class UserController {
    UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> showUsers() {
        List<UserDto> users = userService.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto).toList();
        return ResponseEntity.ok(users);
    }
}
