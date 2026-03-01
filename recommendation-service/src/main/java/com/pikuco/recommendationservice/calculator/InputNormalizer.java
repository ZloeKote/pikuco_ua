package com.pikuco.recommendationservice.calculator;

import org.springframework.stereotype.Component;

/**
 * Normalizes all inputs to [0, 1] range for consistent fuzzy logic processing.
 * This prevents scale differences from causing some variables to dominate others.
 */
@Component
public class InputNormalizer {

    /**
     * Normalize quiz popularity (raw: 0-200+ completions)
     */
    public double normalizePopularity(double rawValue) {
        return Math.min(rawValue / 200.0, 1.0);
    }

    /**
     * Normalize tag similarity (raw: 0-100%)
     */
    public double normalizeTagSimilarity(double percentage) {
        return percentage / 100.0;
    }

    /**
     * Normalize like ratio (raw: 0-100%)
     */
    public double normalizeLikeRatio(double percentage) {
        return percentage / 100.0;
    }

    /**
     * Temporal relevance calculator already returns normalized [0, 1]
     * due to exponential decay transformation
     */
    public double normalizeTemporalRelevance(double normalizedAge) {
        return normalizedAge; // Already normalized
    }

    /**
     * Normalize creator affinity (raw: 0-100%)
     */
    public double normalizeCreatorAffinity(double percentage) {
        return percentage / 100.0;
    }

    /**
     * Normalize length match (raw: -1 to +1 ratio)
     * Maps to [0, 1] where 0.5 is perfect match
     */
    public double normalizeLengthMatch(double ratio) {
        // Map from [-1, +1] to [0, 1]
        return (ratio + 1.0) / 2.0;
    }
}


