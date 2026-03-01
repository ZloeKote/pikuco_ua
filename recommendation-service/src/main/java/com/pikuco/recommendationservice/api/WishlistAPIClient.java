package com.pikuco.recommendationservice.api;

import com.pikuco.recommendationservice.dto.WishlistDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(url = "http://localhost:9091", value = "WISHLIST-SERVICE")
public interface WishlistAPIClient {
    
    @GetMapping("api/v1/wishlists/users/{userId}")
    ResponseEntity<List<WishlistDto>> getUserWishlist(@PathVariable Long userId);
}


