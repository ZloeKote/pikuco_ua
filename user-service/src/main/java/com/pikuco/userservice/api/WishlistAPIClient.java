package com.pikuco.userservice.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "http://localhost:9091", value = "WISHLIST-SERVICE")
public interface WishlistAPIClient {
    @DeleteMapping("api/v1/wishlists/users/{userId}")
    ResponseEntity<?> deleteAllWishlistsByUserId(@PathVariable long userId);
}
