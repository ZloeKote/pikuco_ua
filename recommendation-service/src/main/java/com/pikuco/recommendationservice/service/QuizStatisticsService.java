package com.pikuco.recommendationservice.service;

import com.pikuco.recommendationservice.api.EvaluationAPIClient;
import com.pikuco.recommendationservice.api.QuizAPIClient;
import com.pikuco.recommendationservice.dto.QuizBasicDto;
import com.pikuco.recommendationservice.entity.QuizStatistics;
import com.pikuco.recommendationservice.repository.QuizStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing quiz statistics.
 * Periodically updates statistics from other services.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizStatisticsService {

    private final QuizStatisticsRepository quizStatisticsRepository;
    private final QuizAPIClient quizAPIClient;
    private final EvaluationAPIClient evaluationAPIClient;
    private final MongoTemplate mongoTemplate;

    /**
     * Update quiz statistics every hour.
     * Scheduled with cron expression: 0 0 * * * * (every hour at minute 0)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void updateQuizStatistics() {
        log.info("Starting scheduled quiz statistics update");

        try {
            // Fetch all quizzes
            ResponseEntity<List<QuizBasicDto>> quizzesResponse =
                    quizAPIClient.showQuizzes(1, 1000);

            if (!quizzesResponse.getStatusCode().is2xxSuccessful() ||
                    quizzesResponse.getBody() == null ||
                    quizzesResponse.getBody().isEmpty()) {
                log.error("Failed to fetch quizzes for statistics update");
                return;
            }

            List<QuizBasicDto> quizzes = quizzesResponse.getBody();

            // Use bulk operations for efficient upsert
            BulkOperations bulkOps = mongoTemplate.bulkOps(
                    BulkOperations.BulkMode.UNORDERED,
                    QuizStatistics.class);

            int processedCount = 0;
            for (QuizBasicDto quiz : quizzes) {
                try {
                    if (quiz.isRoughDraft()) {
                        continue; // Skip drafts
                    }

                    QuizStatistics stats = calculateQuizStatistics(quiz.pseudoId());
                    // Create query to find existing document by quizPseudoId
                    Query query = new Query(Criteria.where("quiz_pseudo_id")
                            .is(stats.getQuizPseudoId()));

                    // Create update operation with all fields
                    Update update = new Update()
                            .set("quiz_pseudo_id", stats.getQuizPseudoId())
                            .set("total_completions", stats.getTotalCompletions())
                            .set("total_likes", stats.getTotalLikes())
                            .set("total_dislikes", stats.getTotalDislikes())
                            .set("like_ratio", stats.getLikeRatio())
                            .set("popularity_score", stats.getPopularityScore())
                            .set("last_updated", stats.getLastUpdated());

                    // Add upsert operation (update if exists, insert if not)
                    bulkOps.upsert(query, update);
                    processedCount++;
                } catch (Exception e) {
                    log.warn("Error preparing statistics for quiz {}: {}",
                            quiz.pseudoId(), e.getMessage());
                }
            }

            // Execute all operations in a single batch
            if (processedCount > 0) {
                var result = bulkOps.execute();
                log.info("Successfully updated statistics for {} quizzes " +
                                "(modified: {}, inserted: {})",
                        processedCount,
                        result.getModifiedCount(),
                        result.getInsertedCount());
            } else {
                log.info("No quizzes to update");
            }

        } catch (Exception e) {
            log.error("Error during quiz statistics update: {}", e.getMessage(), e);
        }
    }

    /**
     * Calculate statistics for a single quiz without database operations.
     */
    private QuizStatistics calculateQuizStatistics(int pseudoId) {
        // Get completion count
        long completions = 0;
        try {
            ResponseEntity<Integer> completionsResponse =
                    quizAPIClient.getQuizCompletionCount(pseudoId);
            if (completionsResponse.getStatusCode().is2xxSuccessful() &&
                    completionsResponse.getBody() != null) {
                completions = completionsResponse.getBody().longValue();
            }
        } catch (Exception e) {
            log.debug("Could not fetch completions for quiz {}: {}", pseudoId, e.getMessage());
        }

        // Get likes count
        long likes = 0;
        try {
            ResponseEntity<Long> likesResponse =
                    evaluationAPIClient.getQuizEvaluationCount(pseudoId, true);
            if (likesResponse.getStatusCode().is2xxSuccessful() &&
                    likesResponse.getBody() != null) {
                likes = likesResponse.getBody();
            }
        } catch (Exception e) {
            log.debug("Could not fetch likes for quiz {}: {}", pseudoId, e.getMessage());
        }

        // Get dislikes count
        long dislikes = 0;
        try {
            ResponseEntity<Long> dislikesResponse =
                    evaluationAPIClient.getQuizEvaluationCount(pseudoId, false);
            if (dislikesResponse.getStatusCode().is2xxSuccessful() &&
                    dislikesResponse.getBody() != null) {
                dislikes = dislikesResponse.getBody();
            }
        } catch (Exception e) {
            log.debug("Could not fetch dislikes for quiz {}: {}", pseudoId, e.getMessage());
        }

        // Calculate like ratio
        double likeRatio = 0.0;
        if (likes + dislikes > 0) {
            likeRatio = ((double) likes / (likes + dislikes)) * 100.0;
        }

        // Build statistics entity (without DB lookup for bulk operation)
        QuizStatistics stats = new QuizStatistics();
        stats.setQuizPseudoId(pseudoId);
        stats.setTotalCompletions(completions);
        stats.setTotalLikes(likes);
        stats.setTotalDislikes(dislikes);
        stats.setLikeRatio(likeRatio);
        stats.setPopularityScore((double) completions);
        stats.setLastUpdated(LocalDateTime.now());

        return stats;
    }

    /**
     * Get quiz statistics by pseudoId.
     */
    public QuizStatistics getQuizStatistics(int pseudoId) {
        return quizStatisticsRepository.findByQuizPseudoId(pseudoId)
                .orElse(createDefaultStatistics(pseudoId));
    }

    /**
     * Create default statistics for quizzes without data.
     */
    private QuizStatistics createDefaultStatistics(int pseudoId) {
        return QuizStatistics.builder()
                .quizPseudoId(pseudoId)
                .totalCompletions(0)
                .totalLikes(0)
                .totalDislikes(0)
                .likeRatio(50.0) // Neutral
                .popularityScore(0.0)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}


