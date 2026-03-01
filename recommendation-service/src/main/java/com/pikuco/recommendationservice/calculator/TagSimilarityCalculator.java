package com.pikuco.recommendationservice.calculator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Calculates tag similarity using weighted Jaccard similarity.
 * Takes into account user's affinity to specific tags.
 */
@Component
public class TagSimilarityCalculator {

    /**
     * Calculate similarity between quiz tags and user's tag preferences.
     * 
     * @param quizTags List of tags on the quiz
     * @param userTagAffinities Map of user's tag preferences (tag -> affinity score)
     * @return similarity percentage (0-100)
     */
    public double calculate(List<String> quizTags, Map<String, Double> userTagAffinities) {
        if (quizTags == null || quizTags.isEmpty() || userTagAffinities == null || userTagAffinities.isEmpty()) {
            return 0.0;
        }

        // Calculate weighted intersection
        double weightedIntersection = 0.0;
        for (String tag : quizTags) {
            Double affinity = userTagAffinities.get(tag);
            if (affinity != null) {
                weightedIntersection += affinity;
            }
        }

        // Calculate union size (total unique tags)
        int unionSize = quizTags.size();
        for (String userTag : userTagAffinities.keySet()) {
            if (!quizTags.contains(userTag)) {
                unionSize++;
            }
        }

        // Weighted Jaccard similarity
        if (unionSize == 0) {
            return 0.0;
        }

        // Normalize by union size and scale to percentage
        double similarity = (weightedIntersection / unionSize) * 100.0;
        
        // Clamp to [0, 100]
        return Math.max(0.0, Math.min(100.0, similarity));
    }
}


