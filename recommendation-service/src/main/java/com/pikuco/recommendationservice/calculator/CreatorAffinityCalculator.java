package com.pikuco.recommendationservice.calculator;

import com.pikuco.recommendationservice.dto.UserPreferenceProfileDto;
import org.springframework.stereotype.Component;

/**
 * Calculates user's affinity to a quiz creator.
 * Based on past interactions (likes, completions) with the creator's quizzes.
 */
@Component
public class CreatorAffinityCalculator {

    /**
     * Calculate creator affinity percentage.
     * 
     * @param creatorNickname nickname of the quiz creator
     * @param profile User's preference profile
     * @return affinity percentage (0-100)
     */
    public double calculate(String creatorNickname, UserPreferenceProfileDto profile) {
        if (creatorNickname == null || profile == null || profile.getCreatorInteractions() == null) {
            return 0.0;
        }

        Integer interactions = profile.getCreatorInteractions().get(creatorNickname);

        if (interactions == null || interactions == 0) {
            return 0.0;
        }

        // Calculate percentage based on interaction count
        // Each interaction = 10 points, capped at 100
        // This preserves absolute preference strength
        return Math.min(interactions * 10.0, 100.0);
    }
}


