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

    @PostMapping("/quizzes/{pseudoId}/user")
    public ResponseEntity<?> addQuizToWishlist(@RequestHeader(value = "Authorization") String authHeader,
                                               @PathVariable int pseudoId) {
        wishlistService.addQuizToWishlist(authHeader, pseudoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quizzes/{pseudoId}/user")
    public ResponseEntity<?> deleteQuizFromWishlist(@RequestHeader(value = "Authorization") String authHeader,
                                                    @PathVariable int pseudoId) {
        wishlistService.deleteQuizFromWishlist(authHeader, pseudoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/quizzes/{pseudoId}/user")
    public ResponseEntity<CheckIsInWishlistResponse> checkIfQuizIsInWishlist(@RequestHeader(value = "Authorization") String authHeader,
                                                                             @PathVariable int pseudoId) {
        boolean isInWishlist = wishlistService.checkIfQuizIsInWishlist(authHeader, pseudoId);
        return ResponseEntity.ok(new CheckIsInWishlistResponse(isInWishlist));
    }

    @GetMapping("/quizzes/users/{userId}")
    public ResponseEntity<List<String>> getWishlistedQuizzesIdsByUserId(
            @PathVariable long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", required = false, defaultValue = "4") int pageSize) {
        List<String> quizzesIds = wishlistService.getWishlistedQuizzesIdByUserId(userId, pageNo, pageSize);
        return ResponseEntity.ok(quizzesIds);
    }

    @GetMapping("/quizzes/users/{userId}/count")
    public ResponseEntity<Integer> getNumWishlistedQuizzesByUserId(@PathVariable long userId) {
        return ResponseEntity.ok(wishlistService.getNumWishlistedQuizzesByUserId(userId));
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity<?> deleteQuizWishlistsByQuizId(@PathVariable String quizId) {
        wishlistService.deleteQuizWishlistsByQuizId(quizId);
        return ResponseEntity.ok().build();
    }
}
