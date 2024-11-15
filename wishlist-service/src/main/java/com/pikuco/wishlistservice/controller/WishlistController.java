package com.pikuco.wishlistservice.controller;

import com.pikuco.sharedComps.wishlistService.dto.WishlistDto;
import com.pikuco.wishlistservice.service.WishlistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/wishlists")
@AllArgsConstructor
public class WishlistController {
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistDto>> showWishlists() {
        List<WishlistDto> wishlists = wishlistService.getWishlists();
        return ResponseEntity.ok(wishlists);
    }
}
