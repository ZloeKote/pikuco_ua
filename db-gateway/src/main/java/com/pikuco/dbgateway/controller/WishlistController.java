package com.pikuco.dbgateway.controller;

import com.pikuco.dbgateway.mapper.WishlistMapper;
import com.pikuco.dbgateway.service.WishlistService;
import com.pikuco.sharedComps.wishlistService.dto.WishlistDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/db/wishlists")
public class WishlistController {
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistDto>> showWishlists() {
        List<WishlistDto> wishlists = wishlistService.getAllWishlists()
                .stream()
                .map(WishlistMapper::mapToWishlistDto).toList();
        return ResponseEntity.ok(wishlists);
    }
}
