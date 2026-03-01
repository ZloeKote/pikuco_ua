package com.pikuco.recommendationservice.calculator;

import com.pikuco.recommendationservice.entity.QuizStatistics;
import org.springframework.stereotype.Component;

/**
 * Calculates quiz popularity based on completion count.
 */
@Component
public class PopularityCalculator {

    /**
     * Calculate popularity score from quiz statistics.
     * 
     * @param stats Quiz statistics entity
     * @return raw completion count (0-200+)
     */
    public double calculate(QuizStatistics stats) {
        if (stats == null) {
            return 0.0;
        }
        
        return (double) stats.getTotalCompletions();
    }
}


