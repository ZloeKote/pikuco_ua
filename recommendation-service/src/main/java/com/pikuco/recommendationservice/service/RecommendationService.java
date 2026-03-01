package com.pikuco.recommendationservice.service;

import com.pikuco.recommendationservice.api.QuizAPIClient;
import com.pikuco.recommendationservice.api.UserAPIClient;
import com.pikuco.recommendationservice.calculator.*;
import com.pikuco.recommendationservice.dto.QuizBasicDto;
import com.pikuco.recommendationservice.dto.RecommendationDto;
import com.pikuco.recommendationservice.dto.UserPreferenceProfileDto;
import com.pikuco.recommendationservice.entity.QuizStatistics;
import com.pikuco.recommendationservice.exception.NonAuthorizedException;
import com.pikuco.recommendationservice.fuzzy.inference.FuzzyInferenceEngine;
import com.pikuco.recommendationservice.fuzzy.rule.FuzzyRuleBase;
import com.pikuco.recommendationservice.fuzzy.variable.FuzzyInputVariable;
import com.pikuco.recommendationservice.fuzzy.variable.FuzzyOutputVariable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core recommendation service that generates personalized quiz recommendations
 * using fuzzy logic inference.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final UserPreferenceService userPreferenceService;
    private final QuizStatisticsService quizStatisticsService;
    private final QuizAPIClient quizAPIClient;
    private final UserAPIClient userAPIClient;

    // Metric calculators
    private final TagSimilarityCalculator tagSimilarityCalculator;
    private final PopularityCalculator popularityCalculator;
    private final LikeRatioCalculator likeRatioCalculator;
    private final TemporalRelevanceCalculator temporalRelevanceCalculator;
    private final CreatorAffinityCalculator creatorAffinityCalculator;
    private final LengthMatchCalculator lengthMatchCalculator;
    private final InputNormalizer inputNormalizer;

    // Fuzzy logic components
    private final FuzzyInferenceEngine fuzzyInferenceEngine;
    private final Map<String, FuzzyInputVariable> inputVariables;
    private final FuzzyOutputVariable outputVariable;
    private final FuzzyRuleBase ruleBase;

    @PostConstruct
    public void initialize() {
        // Initialize fuzzy inference engine with variables and rules
        fuzzyInferenceEngine.initialize(inputVariables, outputVariable, ruleBase);
        log.info("Fuzzy inference engine initialized with {} input variables and {} rules",
                inputVariables.size(), ruleBase.size());
    }

    /**
     * Get personalized quiz recommendations for a user.
     * Results are cached for 5 minutes.
     */
    @Cacheable(value = "recommendations", key = "#authHeader + '_' + #limit")
    public List<RecommendationDto> getPersonalizedRecommendations(String authHeader, int limit) {
        ResponseEntity<Long> responseEntity = userAPIClient.showUserIdByToken(authHeader);
        if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(403))
            throw new NonAuthorizedException("Ви не авторизовані");
        Long userId = Objects.requireNonNull(responseEntity.getBody());

        try {
            // Step 1: Build user preference profile
            UserPreferenceProfileDto profile = userPreferenceService.buildUserPreferenceProfile(userId);

            // Step 2: Fetch all active quizzes
            ResponseEntity<List<QuizBasicDto>> quizzesResponse = 
                    quizAPIClient.showQuizzes(1, 1000);

            if (!quizzesResponse.getStatusCode().is2xxSuccessful() || 
                quizzesResponse.getBody() == null) {
                log.error("Failed to fetch quizzes for recommendations");
                return Collections.emptyList();
            }

            List<QuizBasicDto> allQuizzes = quizzesResponse.getBody();

            // Step 3: Filter out unwanted quizzes
            List<QuizBasicDto> candidateQuizzes = allQuizzes.stream()
                    .filter(quiz -> !quiz.isRoughDraft())
                    .filter(quiz -> !profile.getCompletedQuizPseudoIds().contains(quiz.pseudoId()))
                    .filter(quiz -> !profile.getEvaluatedQuizPseudoIds().contains(quiz.pseudoId()))
                    .toList();

            // Step 4: Calculate recommendation scores for each candidate
            List<RecommendationDto> recommendations = new ArrayList<>();
            for (QuizBasicDto quiz : candidateQuizzes) {
                try {
                    double score = calculateRecommendationScore(quiz, profile);
                    recommendations.add(new RecommendationDto(quiz, score));
                } catch (Exception e) {
                    log.warn("Error calculating score for quiz {}: {}", 
                            quiz.pseudoId(), e.getMessage());
                }
            }

            // Step 5: Sort by score descending and return top N
            return recommendations.stream()
                    .sorted((r1, r2) -> Double.compare(r2.score(), r1.score()))
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error generating recommendations for user {}: {}", 
                    userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Get similar quizzes based on tag overlap and other factors.
     */
    public List<RecommendationDto> getSimilarQuizzes(int quizPseudoId, int limit) {
        log.debug("Finding similar quizzes for quiz {}", quizPseudoId);

        try {
            // Fetch target quiz
            ResponseEntity<QuizBasicDto> targetQuizResponse = 
                    quizAPIClient.getQuizByPseudoId(quizPseudoId);

            if (!targetQuizResponse.getStatusCode().is2xxSuccessful() || 
                targetQuizResponse.getBody() == null) {
                log.error("Failed to fetch target quiz {}", quizPseudoId);
                return Collections.emptyList();
            }

            QuizBasicDto targetQuiz = targetQuizResponse.getBody();

            // Fetch all quizzes
            ResponseEntity<List<QuizBasicDto>> quizzesResponse = 
                    quizAPIClient.showQuizzes(1, 1000);

            if (!quizzesResponse.getStatusCode().is2xxSuccessful() || 
                quizzesResponse.getBody() == null) {
                return Collections.emptyList();
            }

            List<QuizBasicDto> allQuizzes = quizzesResponse.getBody();

            // Calculate similarity scores
            List<RecommendationDto> similarQuizzes = new ArrayList<>();
            for (QuizBasicDto quiz : allQuizzes) {
                if (quiz.pseudoId() == quizPseudoId || quiz.isRoughDraft()) {
                    continue; // Skip self and drafts
                }

                double similarityScore = calculateSimilarityScore(targetQuiz, quiz);
                if (similarityScore > 0.3) { // Threshold for inclusion
                    similarQuizzes.add(new RecommendationDto(quiz, similarityScore * 100));
                }
            }

            // Sort by score and return top N
            return similarQuizzes.stream()
                    .sorted((r1, r2) -> Double.compare(r2.score(), r1.score()))
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error finding similar quizzes for {}: {}", 
                    quizPseudoId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Calculate recommendation score for a quiz using fuzzy logic.
     */
    private double calculateRecommendationScore(QuizBasicDto quiz, 
                                               UserPreferenceProfileDto profile) {
        // Get quiz statistics
        QuizStatistics stats = quizStatisticsService.getQuizStatistics(quiz.pseudoId());

        // Calculate raw metric values
        double tagSimilarity = tagSimilarityCalculator.calculate(
                quiz.tags(), profile.getTagAffinities());
        double popularity = popularityCalculator.calculate(stats);
        double likeRatio = likeRatioCalculator.calculate(stats);
        double temporalRelevance = temporalRelevanceCalculator.calculate(quiz.createdAt());
        double creatorAffinity = creatorAffinityCalculator.calculate(
                quiz.creator() != null ? quiz.creator().nickname() : null, profile);
        double lengthMatch = lengthMatchCalculator.calculate(
                quiz.numQuestions(), profile.getAvgQuizLength());

        // Normalize all inputs to [0, 1]
        Map<String, Double> normalizedInputs = new HashMap<>();
        normalizedInputs.put("TagSimilarity", inputNormalizer.normalizeTagSimilarity(tagSimilarity));
        normalizedInputs.put("QuizPopularity", inputNormalizer.normalizePopularity(popularity));
        normalizedInputs.put("LikeRatio", inputNormalizer.normalizeLikeRatio(likeRatio));
        normalizedInputs.put("TemporalRelevance", inputNormalizer.normalizeTemporalRelevance(temporalRelevance));
        normalizedInputs.put("CreatorAffinity", inputNormalizer.normalizeCreatorAffinity(creatorAffinity));
        normalizedInputs.put("LengthMatch", inputNormalizer.normalizeLengthMatch(lengthMatch));

        // Run fuzzy inference
        double score = fuzzyInferenceEngine.infer(normalizedInputs);

        log.debug("Quiz {} score: {} (tagSim={}, pop={}, like={}, temp={}, creator={}, length={})",
                quiz.pseudoId(), score, tagSimilarity, popularity, likeRatio, 
                temporalRelevance, creatorAffinity, lengthMatch);

        return score;
    }

    /**
     * Calculate similarity score between two quizzes.
     * Based on tag overlap, same creator, similar length, and popularity.
     */
    private double calculateSimilarityScore(QuizBasicDto target, QuizBasicDto candidate) {
        double score = 0.0;

        // Tag similarity (Jaccard coefficient)
        if (target.tags() != null && candidate.tags() != null && 
            !target.tags().isEmpty() && !candidate.tags().isEmpty()) {
            Set<String> targetTags = new HashSet<>(target.tags());
            Set<String> candidateTags = new HashSet<>(candidate.tags());
            
            Set<String> intersection = new HashSet<>(targetTags);
            intersection.retainAll(candidateTags);
            
            Set<String> union = new HashSet<>(targetTags);
            union.addAll(candidateTags);
            
            double tagSimilarity = union.isEmpty() ? 0.0 : 
                    (double) intersection.size() / union.size();
            score += tagSimilarity * 0.6; // 60% weight
        }

        // Same creator bonus (using creator nickname)
        if (target.creator() != null && candidate.creator() != null) {
            String targetCreator = target.creator().nickname();
            String candidateCreator = candidate.creator().nickname();
            if (targetCreator != null && targetCreator.equalsIgnoreCase(candidateCreator)) {
                score += 0.2; // 20% weight
            }
        }

        // Similar length
        if (target.numQuestions() > 0 && candidate.numQuestions() > 0) {
            double lengthRatio = Math.min(target.numQuestions(), candidate.numQuestions()) / 
                    (double) Math.max(target.numQuestions(), candidate.numQuestions());
            score += lengthRatio * 0.1; // 10% weight
        }

        // Popularity factor (small bonus)
        QuizStatistics stats = quizStatisticsService.getQuizStatistics(candidate.pseudoId());
        if (stats.getTotalCompletions() > 10) {
            score += 0.1; // 10% weight
        }

        return Math.min(score, 1.0);
    }
}


