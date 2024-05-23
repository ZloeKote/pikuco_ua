package com.pikuco.wishlistservice.service;

import com.pikuco.wishlistservice.entity.Wishlist;
import com.pikuco.wishlistservice.exception.NonAuthorizedException;
import com.pikuco.wishlistservice.repository.WishlistRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    public void deleteQuizWishlistsByQuizId(String quizId) {
        Query matchQuery = new Query(Criteria.where("type").is("quiz")
                .and("wishlisted_id").is(quizId));

        mongoTemplate.findAllAndRemove(matchQuery, "wishlist");
    }

    public List<String> getWishlistedQuizzesIdByUserId(Long userId, int pageNo, int pageSize) {
        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(
                Criteria.where("type").is("quiz"),
                Criteria.where("user_id").is(userId)));
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "wishlistedAt");
        SkipOperation skipOperation = Aggregation.skip((long) (pageNo - 1) * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sortOperation, skipOperation, limitOperation);
        AggregationResults<Wishlist> aggregationResults = mongoTemplate.aggregate(aggregation, "wishlist", Wishlist.class);

        List<String> quizzesIds = new LinkedList<>();
        for (Wishlist wishlist : aggregationResults.getMappedResults()) {
            quizzesIds.add(wishlist.getWishlistedId());
        }
        return quizzesIds;
    }

    public int getNumWishlistedQuizzesByUserId(Long userId) {
        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(
                Criteria.where("type").is("quiz"),
                Criteria.where("user_id").is(userId)));
        CountOperation countOperation = Aggregation.count().as("quantity");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, countOperation);
        HashMap<String, Integer> resultsCountMap = mongoTemplate.aggregate(aggregation, "wishlist", HashMap.class)
                .getUniqueMappedResult();

        assert resultsCountMap != null;
        return resultsCountMap.get("quantity");
    }

    private Wishlist getReadyWishlist(String authHeader, int pseudoId) {
        String quizId = quizAPI.showQuizIdByPseudoId(pseudoId).getBody();

        if (quizId == null) {
            throw new NoSuchElementException("Такої вікторини не існує");
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
        } catch (FeignException | NullPointerException e) {
            throw new NonAuthorizedException("Ви не авторизовані");
        }
    }
}
