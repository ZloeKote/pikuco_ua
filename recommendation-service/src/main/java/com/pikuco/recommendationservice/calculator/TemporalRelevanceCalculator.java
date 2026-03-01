package com.pikuco.recommendationservice.calculator;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Calculates temporal relevance using exponential decay.
 * Human perception of "newness" decays exponentially, not linearly.
 * A quiz from 5 vs 10 days ago feels similar, but 5 vs 180 days feels very different.
 */
@Component
public class TemporalRelevanceCalculator {

    // Decay constant: 30 days (can be tuned)
    private static final double TAU = 30.0;

    /**
     * Calculate perceived age of content using exponential decay.
     * 
     * @param createdAt When the quiz was created
     * @return normalized perceived age [0, 1] where 0 is brand new, 1 is very old
     */
    public double calculate(LocalDateTime createdAt) {
        if (createdAt == null) {
            return 1.0; // Treat null as very old
        }

        long daysOld = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());

        // Apply exponential transformation
        // Value approaches 1.0 as content ages
        double perceivedAge = 1.0 - Math.exp(-daysOld / TAU);

        // Clamp to [0, 1] (already normalized)
        return Math.max(0.0, Math.min(1.0, perceivedAge));
    }
}


