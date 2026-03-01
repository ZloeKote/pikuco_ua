package com.pikuco.recommendationservice.fuzzy.config;

import com.pikuco.recommendationservice.fuzzy.membership.TrapezoidalMF;
import com.pikuco.recommendationservice.fuzzy.membership.TriangularMF;
import com.pikuco.recommendationservice.fuzzy.variable.FuzzyInputVariable;
import com.pikuco.recommendationservice.fuzzy.variable.FuzzyOutputVariable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for fuzzy variables with membership functions.
 * All input variables operate on normalized [0, 1] range.
 * Membership functions have increased overlaps for smoother transitions.
 */
@Configuration
public class FuzzyVariablesConfig {

    @Bean
    public Map<String, FuzzyInputVariable> inputVariables() {
        Map<String, FuzzyInputVariable> variables = new HashMap<>();
        
        variables.put("TagSimilarity", createTagSimilarity());
        variables.put("QuizPopularity", createQuizPopularity());
        variables.put("LikeRatio", createLikeRatio());
        variables.put("TemporalRelevance", createTemporalRelevance());
        variables.put("CreatorAffinity", createCreatorAffinity());
        variables.put("LengthMatch", createLengthMatch());
        
        return variables;
    }

    @Bean
    public FuzzyOutputVariable outputVariable() {
        return createRecommendationScore();
    }

    /**
     * TagSimilarity: measures how well quiz tags match user preferences (0-1 normalized)
     */
    private FuzzyInputVariable createTagSimilarity() {
        FuzzyInputVariable var = new FuzzyInputVariable("TagSimilarity", 0.0, 1.0);
        
        // Increased overlaps for smooth transitions
        var.addTerm("NO_MATCH", new TrapezoidalMF(0.0, 0.0, 0.10, 0.20));
        var.addTerm("WEAK_MATCH", new TriangularMF(0.10, 0.25, 0.40));
        var.addTerm("MODERATE_MATCH", new TriangularMF(0.35, 0.55, 0.70));
        var.addTerm("STRONG_MATCH", new TrapezoidalMF(0.65, 0.85, 1.0, 1.0));
        
        return var;
    }

    /**
     * QuizPopularity: number of completions normalized (0-1, raw 0-200+)
     */
    private FuzzyInputVariable createQuizPopularity() {
        FuzzyInputVariable var = new FuzzyInputVariable("QuizPopularity", 0.0, 1.0);
        
        var.addTerm("UNPOPULAR", new TrapezoidalMF(0.0, 0.0, 0.025, 0.05));
        var.addTerm("MODERATE", new TriangularMF(0.04, 0.15, 0.25));
        var.addTerm("POPULAR", new TriangularMF(0.20, 0.475, 0.75));
        var.addTerm("VIRAL", new TrapezoidalMF(0.50, 0.75, 1.0, 1.0));
        
        return var;
    }

    /**
     * LikeRatio: percentage of likes normalized (0-1, raw 0-100%)
     */
    private FuzzyInputVariable createLikeRatio() {
        FuzzyInputVariable var = new FuzzyInputVariable("LikeRatio", 0.0, 1.0);
        
        var.addTerm("DISLIKED", new TrapezoidalMF(0.0, 0.0, 0.20, 0.35));
        var.addTerm("NEUTRAL", new TriangularMF(0.25, 0.50, 0.70));
        var.addTerm("LIKED", new TriangularMF(0.60, 0.75, 0.90));
        var.addTerm("LOVED", new TrapezoidalMF(0.85, 0.95, 1.0, 1.0));
        
        return var;
    }

    /**
     * TemporalRelevance: quiz age with exponential decay (0-1 normalized)
     */
    private FuzzyInputVariable createTemporalRelevance() {
        FuzzyInputVariable var = new FuzzyInputVariable("TemporalRelevance", 0.0, 1.0);
        
        var.addTerm("NEW", new TrapezoidalMF(0.0, 0.0, 0.15, 0.30));
        var.addTerm("RECENT", new TriangularMF(0.20, 0.50, 0.75));
        var.addTerm("OLD", new TriangularMF(0.65, 0.85, 0.95));
        var.addTerm("OUTDATED", new TrapezoidalMF(0.90, 0.97, 1.0, 1.0));
        
        return var;
    }

    /**
     * CreatorAffinity: user's affinity to quiz creator (0-1 normalized, raw 0-100%)
     */
    private FuzzyInputVariable createCreatorAffinity() {
        FuzzyInputVariable var = new FuzzyInputVariable("CreatorAffinity", 0.0, 1.0);
        
        var.addTerm("NO_HISTORY", new TrapezoidalMF(0.0, 0.0, 0.05, 0.15));
        var.addTerm("SOME_INTERACTION", new TriangularMF(0.10, 0.35, 0.55));
        var.addTerm("REGULAR_USER", new TriangularMF(0.45, 0.70, 0.90));
        var.addTerm("FAN", new TrapezoidalMF(0.80, 0.92, 1.0, 1.0));
        
        return var;
    }

    /**
     * LengthMatch: how well quiz length matches user preference (0-1 normalized, raw ratio -1 to +1)
     */
    private FuzzyInputVariable createLengthMatch() {
        FuzzyInputVariable var = new FuzzyInputVariable("LengthMatch", 0.0, 1.0);
        
        // -1 (too short) mapped to 0, 0 (perfect) mapped to 0.5, +1 (too long) mapped to 1
        var.addTerm("TOO_SHORT", new TrapezoidalMF(0.0, 0.0, 0.2, 0.35));
        var.addTerm("PERFECT_MATCH", new TriangularMF(0.3, 0.5, 0.7));
        var.addTerm("TOO_LONG", new TrapezoidalMF(0.65, 0.8, 1.0, 1.0));
        
        return var;
    }

    /**
     * RecommendationScore: output variable (0-100)
     * Adjusted for better score distribution - high scores are more selective
     */
    private FuzzyOutputVariable createRecommendationScore() {
        FuzzyOutputVariable var = new FuzzyOutputVariable("RecommendationScore", 0.0, 100.0);
        
        var.addTerm("VERY_LOW", new TrapezoidalMF(0, 0, 10, 20));
        var.addTerm("LOW", new TriangularMF(15, 30, 45));
        var.addTerm("MEDIUM", new TriangularMF(40, 55, 70));
        var.addTerm("HIGH", new TriangularMF(65, 80, 90));
        var.addTerm("VERY_HIGH", new TrapezoidalMF(85, 95, 100, 100));
        
        return var;
    }
}


