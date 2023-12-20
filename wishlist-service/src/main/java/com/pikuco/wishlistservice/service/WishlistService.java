package com.pikuco.wishlistservice.service;

import com.pikuco.wishlistservice.entity.Wishlist;
import com.pikuco.wishlistservice.exception.NonAuthorizedException;
import com.pikuco.wishlistservice.repository.WishlistRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
        try {
            mongoTemplate.insert(wishlist);
        } catch (DuplicateKeyException e) {
            return;
        }
    }

    public void deleteQuizFromWishlist(String authHeader, int pseudoId) {
        Wishlist wishlist = getReadyWishlist(authHeader, pseudoId);
        Query query = new Query(Criteria.where("user_id").is(wishlist.getUserId())
                .and("wishlisted_id").is(wishlist.getWishlistedId())
                .and("type").is("quiz"));
        mongoTemplate.findAndRemove(query, Wishlist.class);
    }

    public boolean checkIfQuizIsInWishlist(String authHeader, int pseudoId) {
        Wishlist wishlist = getReadyWishlist(authHeader, pseudoId);
        Query query = new Query(Criteria.where("user_id").is(wishlist.getUserId())
                .and("wishlisted_id").is(wishlist.getWishlistedId())
                .and("type").is("quiz"));
        return mongoTemplate.exists(query, Wishlist.class);
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
