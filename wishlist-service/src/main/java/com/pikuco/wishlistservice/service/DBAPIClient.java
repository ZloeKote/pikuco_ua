package com.pikuco.wishlistservice.service;

import com.pikuco.sharedComps.wishlistService.dto.WishlistDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(url = "http://localhost:9000", value = "DB-GATEWAY")
public interface DBAPIClient {

    @GetMapping("api/v1/db/wishlists")
    ResponseEntity<List<WishlistDto>> showWishlists();
}
