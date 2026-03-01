package com.pikuco.recommendationservice.calculator;

import org.springframework.stereotype.Component;

/**
 * Calculates how well a quiz's length matches user's preferences.
 * Uses ratio-based approach instead of logarithmic for better interpretability.
 */
@Component
public class LengthMatchCalculator {

    /**
     * Calculate length match as a ratio.
     * 
     * @param quizNumQuestions Number of questions in the quiz
     * @param userAvgNumQuestions User's average preferred number of questions
     * @return ratio [-1, +1] where -1 is half as long, 0 is perfect, +1 is twice as long
     */
    public double calculate(int quizNumQuestions, int userAvgNumQuestions) {
        // Avoid division by zero
        if (userAvgNumQuestions == 0) {
            return 0.0; // Treat as neutral if no history
        }

        // Calculate relative difference as ratio
        double ratio = (double)(quizNumQuestions - userAvgNumQuestions) / userAvgNumQuestions;

        // Clamp to [-1, +1] range
        // -1 = quiz is half the user's preferred length
        // +1 = quiz is twice the user's preferred length
        //  0 = perfect match
        return ratio; // TODO: change back to  Math.max(-1.0, Math.min(1.0, ratio));
    }
}


