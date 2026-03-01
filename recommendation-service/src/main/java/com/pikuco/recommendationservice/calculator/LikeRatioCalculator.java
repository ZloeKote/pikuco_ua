package com.pikuco.recommendationservice.calculator;

import com.pikuco.recommendationservice.entity.QuizStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Calculates like ratio using Bayesian Average to handle small sample sizes.
 * This prevents quizzes with 1 like and 0 dislikes from having the same score
 * as quizzes with 100 likes and 0 dislikes.
 */
@Component
public class LikeRatioCalculator {

    // Configurable Bayesian priors
    @Value("${recommendation.prior.likes:5.0}")
    private double priorLikes;

    @Value("${recommendation.prior.dislikes:5.0}")
    private double priorDislikes;

    /**
     * Calculate like ratio with Bayesian averaging and confidence weighting.
     * 
     * @param stats Quiz statistics entity
     * @return like ratio percentage (0-100)
     */
    public double calculate(QuizStatistics stats) {
        if (stats == null) {
            return 50.0; // Neutral
        }

        double likes = stats.getTotalLikes();
        double dislikes = stats.getTotalDislikes();
        double totalVotes = likes + dislikes;

        // Handle edge case: no votes
        if (totalVotes == 0) {
            return 50.0; // Neutral
        }

        // Bayesian average: combines actual ratio with prior assumptions
        double priorTotal = priorLikes + priorDislikes;
        double posteriorLikes = likes + priorLikes;
        double posteriorTotal = totalVotes + priorTotal;
        double bayesianRatio = (posteriorLikes / posteriorTotal) * 100;

        // Confidence weight: more votes = more weight on actual ratio
        // Full confidence at 50+ votes
        double confidence = Math.min(1.0, totalVotes / 50.0);

        // Blend Bayesian (for low votes) with actual ratio (for high votes)
        double actualRatio = (likes / totalVotes) * 100;

        return (confidence * actualRatio) + ((1 - confidence) * bayesianRatio);
    }
}


