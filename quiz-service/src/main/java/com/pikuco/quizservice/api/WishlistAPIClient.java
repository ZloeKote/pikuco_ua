package com.pikuco.quizservice.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(url = "http://localhost:9091", value = "WISHLIST-SERVICE")
public interface WishlistAPIClient {
    @GetMapping("api/v1/wishlists/quizzes/users/{userId}")
    ResponseEntity<List<String>> getWishlistedQuizzesIdsByUserId(
            @PathVariable long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", required = false, defaultValue = "4") int pageSize);

    @GetMapping("api/v1/wishlists/quizzes/users/{userId}/count")
    ResponseEntity<Integer> getNumWishlistedQuizzesByUserId(@PathVariable long userId);

    @DeleteMapping("api/v1/wishlists/quizzes/{quizId}")
    ResponseEntity<?> deleteQuizWishlistsByQuizId(@PathVariable String quizId);
}
