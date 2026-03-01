package com.pikuco.recommendationservice.service;

import com.pikuco.recommendationservice.api.EvaluationAPIClient;
import com.pikuco.recommendationservice.api.QuizAPIClient;
import com.pikuco.recommendationservice.api.WishlistAPIClient;
import com.pikuco.recommendationservice.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for building user preference profiles based on their activity.
 * Results are cached for 15 minutes to reduce load on other services.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceService {

    private final EvaluationAPIClient evaluationAPIClient;
    private final WishlistAPIClient wishlistAPIClient;
    private final QuizAPIClient quizAPIClient;

    /**
     * Build a comprehensive user preference profile.
     * Cached for 15 minutes.
     */
//    @Cacheable(value = "userPreferences", key = "#userId")
    public UserPreferenceProfileDto buildUserPreferenceProfile(Long userId) {
        log.debug("Building preference profile for user {}", userId);

        Map<String, Double> tagAffinities = new HashMap<>();
        Map<String, Integer> creatorInteractions = new HashMap<>();
        Set<Integer> completedQuizPseudoIds = new HashSet<>();
        Set<Integer> evaluatedQuizPseudoIds = new HashSet<>();
        List<Integer> completedQuizLengths = new ArrayList<>();

        try {
            // Fetch user's evaluations (likes/dislikes)
            ResponseEntity<List<EvaluationDto>> evaluationsResponse = 
                    evaluationAPIClient.getUserEvaluations(userId);
            if (evaluationsResponse.getStatusCode().is2xxSuccessful() && 
                evaluationsResponse.getBody() != null) {
                processEvaluations(evaluationsResponse.getBody(), tagAffinities, 
                        creatorInteractions, evaluatedQuizPseudoIds);
            }

            // Fetch user's wishlist
            ResponseEntity<List<WishlistDto>> wishlistResponse = 
                    wishlistAPIClient.getUserWishlist(userId);
            if (wishlistResponse.getStatusCode().is2xxSuccessful() && 
                wishlistResponse.getBody() != null) {
                processWishlist(wishlistResponse.getBody(), tagAffinities, 
                        creatorInteractions);
            }

            // Fetch user's completed quizzes (quiz results)
            try {
                ResponseEntity<List<Integer>> completedResponse = 
                        quizAPIClient.getCompletedQuizzesPseudoIds(userId);
                if (completedResponse.getStatusCode().is2xxSuccessful() && 
                    completedResponse.getBody() != null) {
                    List<Integer> completedPseudoIds = completedResponse.getBody();
                    completedQuizPseudoIds.addAll(completedPseudoIds);
                    
                    // Fetch quiz details to get lengths
                    for (Integer pseudoId : completedPseudoIds) {
                        try {
                            ResponseEntity<QuizBasicDto> quizResponse = 
                                    quizAPIClient.getQuizByPseudoId(pseudoId);
                            if (quizResponse.getStatusCode().is2xxSuccessful() && 
                                quizResponse.getBody() != null) {
                                completedQuizLengths.add(quizResponse.getBody().numQuestions());
                            }
                        } catch (Exception e) {
                            log.debug("Could not fetch quiz details for pseudoId {}: {}", 
                                    pseudoId, e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Error fetching completed quizzes for user {}: {}", 
                        userId, e.getMessage());
            }

        } catch (Exception e) {
            log.error("Error building user preference profile for user {}: {}", 
                    userId, e.getMessage());
        }

        // Normalize tag affinities
        normalizeTagAffinities(tagAffinities);

        // Calculate average quiz length
        int avgQuizLength = completedQuizLengths.isEmpty() ? 0 : 
                (int) completedQuizLengths.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0);

        return UserPreferenceProfileDto.builder()
                .userId(userId)
                .tagAffinities(tagAffinities)
                .creatorInteractions(creatorInteractions)
                .avgQuizLength(avgQuizLength)
                .completedQuizPseudoIds(completedQuizPseudoIds)
                .evaluatedQuizPseudoIds(evaluatedQuizPseudoIds)
                .build();
    }

    /**
     * Process user's evaluations to extract preferences.
     */
    private void processEvaluations(List<EvaluationDto> evaluations,
                                    Map<String, Double> tagAffinities,
                                    Map<String, Integer> creatorInteractions,
                                    Set<Integer> evaluatedQuizPseudoIds) {
        for (EvaluationDto evaluation : evaluations) {
            try {
                // Get quiz details using actual quiz ID
                String quizId = evaluation.evaluationObjectId();
                
                ResponseEntity<QuizBasicDto> quizResponse = 
                        quizAPIClient.getQuizById(quizId);
                if (quizResponse.getStatusCode().is2xxSuccessful() && 
                    quizResponse.getBody() != null) {
                    QuizBasicDto quiz = quizResponse.getBody();
                    
                    // Track evaluated quiz pseudoId
                    evaluatedQuizPseudoIds.add(quiz.pseudoId());

                    // Weight: like = 3, dislike = -1
                    double weight = evaluation.isLiked() ? 3.0 : -1.0;

                    // Aggregate tag affinities
                    if (quiz.tags() != null) {
                        for (String tag : quiz.tags()) {
                            tagAffinities.merge(tag, weight, Double::sum);
                        }
                    }

                    // Track creator interactions
                    if (quiz.creator() != null && quiz.creator().nickname() != null) {
                        String creatorNickname = quiz.creator().nickname();
                        creatorInteractions.merge(creatorNickname, 1, Integer::sum);
                    }
                }
            } catch (Exception e) {
                log.warn("Error processing evaluation: {}", e.getMessage());
            }
        }
    }

    /**
     * Process user's wishlist to extract preferences.
     */
    private void processWishlist(List<WishlistDto> wishlist,
                                Map<String, Double> tagAffinities,
                                Map<String, Integer> creatorInteractions) {
        for (WishlistDto item : wishlist) {
            try {
                // Get quiz details using actual quiz ID
                String quizId = item.wishlistedId();

                ResponseEntity<QuizBasicDto> quizResponse = 
                        quizAPIClient.getQuizById(quizId);
                if (quizResponse.getStatusCode().is2xxSuccessful() && 
                    quizResponse.getBody() != null) {
                    QuizBasicDto quiz = quizResponse.getBody();

                    // Weight: wishlist = 2
                    double weight = 2.0;

                    // Aggregate tag affinities
                    if (quiz.tags() != null) {
                        for (String tag : quiz.tags()) {
                            tagAffinities.merge(tag, weight, Double::sum);
                        }
                    }

                    // Track creator interactions
                    if (quiz.creator() != null && quiz.creator().nickname() != null) {
                        String creatorNickname = quiz.creator().nickname();
                        creatorInteractions.merge(creatorNickname, 1, Integer::sum);
                    }
                }
            } catch (Exception e) {
                log.warn("Error processing wishlist item: {}", e.getMessage());
            }
        }
    }

    /**
     * Normalize tag affinities to [0, 100] range.
     */
    private void normalizeTagAffinities(Map<String, Double> tagAffinities) {
        if (tagAffinities.isEmpty()) {
            return;
        }

        // Remove negative affinities (disliked tags)
        tagAffinities.entrySet().removeIf(entry -> entry.getValue() <= 0);

        if (tagAffinities.isEmpty()) {
            return;
        }

        // Find max value for normalization
        double maxAffinity = tagAffinities.values().stream()
                .max(Double::compareTo)
                .orElse(1.0);

        // Normalize to [0, 100]
        for (Map.Entry<String, Double> entry : tagAffinities.entrySet()) {
            double normalized = (entry.getValue() / maxAffinity) * 100.0;
            tagAffinities.put(entry.getKey(), normalized);
        }
    }
}


