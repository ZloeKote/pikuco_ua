package com.pikuco.quizservice.api;

import com.pikuco.quizservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "http://localhost:9091", value = "USER-SERVICE")
public interface UserAPIClient {
    @GetMapping("api/v1/users")
    ResponseEntity<UserDto> showUserByToken(@RequestHeader("Authorization") String authHeader);

    @GetMapping("api/v1/users/id")
    ResponseEntity<Long> showUserIdByToken(@RequestHeader("Authorization") String authHeader);
}
