package com.pikuco.userservice.service;

//import com.pikuco.sharedComps.userService.dto.UserDto;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "http://localhost:9000", value = "DB-GATEWAY")
public interface DBAPIClient {
//    @GetMapping("api/v1/db/users")
//    ResponseEntity<List<UserDto>> showUsers();
}
