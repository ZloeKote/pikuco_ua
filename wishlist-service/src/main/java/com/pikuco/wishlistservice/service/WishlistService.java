package com.pikuco.wishlistservice.service;

import com.pikuco.wishlistservice.entity.Wishlist;
import com.pikuco.wishlistservice.exception.NonAuthorizedException;
import com.pikuco.wishlistservice.repository.WishlistRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final MongoTemplate mongoTemplate;
    private final WishlistRepository wishlistRepository;
    private final UserAPIClient userAPI;
    private final QuizAPIClient quizAPI;

    public List<Wishlist> getAllWishlists() {
        return wishlistRepository.findAll();
    }

    public void addQuizToWishlist(String authHeader, int pseudoId) {
        Wishlist wishlist = getReadyWishlist(authHeader, pseudoId);
        mongoTemplate.insert(wishlist);
    }

    public void deleteQuizFromWishlist(String authHeader, int pseudoId) {
        Wishlist wishlist = getReadyWishlist(authHeader, pseudoId);
        mongoTemplate.remove(wishlist);
    }

    private Wishlist getReadyWishlist(String authHeader, int pseudoId) {
        String quizId = quizAPI.showQuizIdByPseudoId(pseudoId).getBody();

        if (quizId == null) {
            throw new NonAuthorizedException("Ви не авторизовані");
        }

        ResponseEntity<Long> userResponse;
        try {
            userResponse = userAPI.showUserIdByToken(authHeader);
            Pair<Long, String> quizAndUserId = Pair.of(Objects.requireNonNull(userResponse.getBody()), quizId);
            Wishlist wishlist = new Wishlist();
            wishlist.setType("quiz");
            wishlist.setUserId(quizAndUserId.getFirst());
            wishlist.setWishlistedId(quizAndUserId.getSecond());
            wishlist.setWishlistedAt(LocalDateTime.now());

            return wishlist;
        } catch (FeignException e) {
            throw new NonAuthorizedException("Ви не авторизовані");
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Такої вікторини не існує");
        }
    }
}
