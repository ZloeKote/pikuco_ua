package com.pikuco.userservice.service;

import com.pikuco.sharedComps.userService.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    DBAPIClient apiClient;
    public List<UserDto> getUsers() {
        return apiClient.showUsers().getBody();
    }
}
