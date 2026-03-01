package com.pikuco.recommendationservice.controller;

import com.pikuco.recommendationservice.dto.RecommendationDto;
import com.pikuco.recommendationservice.service.QuizStatisticsService;
import com.pikuco.recommendationservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for recommendation endpoints.
 * Provides personalized quiz recommendations and similar quiz suggestions.
 */
@RestController
@RequestMapping("/api/v1/recommendations")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final QuizStatisticsService quizStatisticsService;

    /**
     * Get personalized quiz recommendations for the authenticated user.
     * Requires X-User-Id header for authentication.
     */
    @GetMapping("/for-you")
    public ResponseEntity<?> getPersonalizedRecommendations(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam(defaultValue = "10") int limit) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(401)).build();

        if (limit < 1 || limit > 100) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        try {
            List<RecommendationDto> recommendations = recommendationService.getPersonalizedRecommendations(authHeader, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get quizzes similar to the specified quiz.
     * Does not require authentication.
     */
    @GetMapping("/similar/{quizPseudoId}")
    public ResponseEntity<?> getSimilarQuizzes(
            @PathVariable int quizPseudoId,
            @RequestParam(defaultValue = "6") int limit) {

        // Validate limit parameter
        if (limit < 1 || limit > 50) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid parameter");
            error.put("message", "Limit must be between 1 and 50");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        log.info("Fetching {} similar quizzes for quiz {}", limit, quizPseudoId);

        try {
            List<RecommendationDto> similarQuizzes = recommendationService.getSimilarQuizzes(quizPseudoId, limit);

            return ResponseEntity.ok(similarQuizzes);
        } catch (Exception e) {
            log.error("Error fetching similar quizzes for {}: {}", quizPseudoId, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", "Failed to find similar quizzes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/update-statistics")
    public ResponseEntity<Void> updateQuizStatistics() {
        try {
            quizStatisticsService.updateQuizStatistics();
        } catch (Exception e) {
            log.error("Error updating quiz statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "recommendation-service");
        return ResponseEntity.ok(response);
    }
}

