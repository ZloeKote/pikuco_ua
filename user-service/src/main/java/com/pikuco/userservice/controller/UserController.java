package com.pikuco.userservice.controller;

import com.pikuco.sharedComps.userService.dto.UserDto;
import com.pikuco.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserDto>> showUsers() {
        List<UserDto> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }
}
