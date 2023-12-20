package com.pikuco.wishlistservice.controller;

import com.pikuco.wishlistservice.dto.CheckIsInWishlistResponse;
import com.pikuco.wishlistservice.dto.WishlistDto;
import com.pikuco.wishlistservice.mapper.WishlistMapper;
import com.pikuco.wishlistservice.service.WishlistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/wishlists")
@AllArgsConstructor
public class WishlistController {
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistDto>> showWishlists() {
        List<WishlistDto> wishlists = wishlistService.getAllWishlists()
                .stream().map(WishlistMapper::mapToWishlistDto).toList();
        return ResponseEntity.ok(wishlists);
    }

    @PostMapping("/quiz/{pseudoId}")
    public ResponseEntity<?> addQuizToWishlist(@RequestHeader(required = false, value = "Authorization") String authHeader,
                                               @PathVariable int pseudoId) {
        wishlistService.addQuizToWishlist(authHeader, pseudoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quiz/{pseudoId}")
    public ResponseEntity<?> deleteQuizFromWishlist(@RequestHeader(required = false, value = "Authorization") String authHeader,
                                                    @PathVariable int pseudoId) {
        wishlistService.deleteQuizFromWishlist(authHeader, pseudoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/quiz/{pseudoId}")
    public ResponseEntity<CheckIsInWishlistResponse> checkIfQuizIsInWishlist(@RequestHeader(required = false, value = "Authorization") String authHeader,
                                                                             @PathVariable int pseudoId) {
        boolean isInWishlist = wishlistService.checkIfQuizIsInWishlist(authHeader, pseudoId);
        return ResponseEntity.ok(new CheckIsInWishlistResponse(isInWishlist));
    }
}
