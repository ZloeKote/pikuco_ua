package com.pikuco.recommendationservice.fuzzy.config;

import com.pikuco.recommendationservice.fuzzy.rule.FuzzyRule;
import com.pikuco.recommendationservice.fuzzy.rule.FuzzyRuleBase;
import com.pikuco.recommendationservice.fuzzy.rule.LogicalOperator;
import com.pikuco.recommendationservice.fuzzy.rule.RuleCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration for fuzzy rules.
 * Contains 30 rules including main recommendation rules and fallback rules.
 */
@Configuration
public class FuzzyRulesConfig {

    @Bean
    public FuzzyRuleBase fuzzyRuleBase() {
        FuzzyRuleBase ruleBase = new FuzzyRuleBase();
        
        // === MAIN RULES (High priority, strong patterns) ===
        
        // Rule 1: Perfect match - strong tag similarity with loved content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "STRONG_MATCH"),
                        new RuleCondition("LikeRatio", "LOVED")
                ),
                LogicalOperator.AND,
                "VERY_HIGH",
                1.0
        ));
        
        // Rule 2: Trending content - new, popular with good tags
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "MODERATE_MATCH"),
                        new RuleCondition("QuizPopularity", "POPULAR"),
                        new RuleCondition("TemporalRelevance", "NEW")
                ),
                LogicalOperator.AND,
                "HIGH",
                0.85
        ));
        
        // Rule 3: Favorite creator with decent content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("CreatorAffinity", "FAN"),
                        new RuleCondition("TagSimilarity", "WEAK_MATCH")
                ),
                LogicalOperator.AND,
                "HIGH",
                0.8
        ));
        
        // Rule 4: Complete mismatch with unpopular content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "NO_MATCH"),
                        new RuleCondition("QuizPopularity", "UNPOPULAR")
                ),
                LogicalOperator.AND,
                "VERY_LOW",
                1.0
        ));
        
        // Rule 5: High quality new content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("LikeRatio", "LOVED"),
                        new RuleCondition("TemporalRelevance", "NEW")
                ),
                LogicalOperator.AND,
                "VERY_HIGH",
                0.9
        ));
        
        // Rule 6: Perfect match in all aspects
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "STRONG_MATCH"),
                        new RuleCondition("LengthMatch", "PERFECT_MATCH")
                ),
                LogicalOperator.AND,
                "HIGH",
                0.75
        ));
        
        // Rule 7: Poor length match reduces recommendation
        ruleBase.addRule(new FuzzyRule(
                List.of(
                        new RuleCondition("LengthMatch", "TOO_SHORT")
                ),
                LogicalOperator.OR,
                "LOW",
                0.6
        ));
        
        ruleBase.addRule(new FuzzyRule(
                List.of(
                        new RuleCondition("LengthMatch", "TOO_LONG")
                ),
                LogicalOperator.OR,
                "LOW",
                0.6
        ));
        
        // Rule 8: Viral content with good match
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("QuizPopularity", "VIRAL"),
                        new RuleCondition("TagSimilarity", "MODERATE_MATCH")
                ),
                LogicalOperator.AND,
                "VERY_HIGH",
                0.85
        ));
        
        // Rule 9: Regular creator interaction with liked content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("CreatorAffinity", "REGULAR_USER"),
                        new RuleCondition("LikeRatio", "LIKED")
                ),
                LogicalOperator.AND,
                "HIGH",
                0.7
        ));
        
        // Rule 10: Moderate match with popular content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "MODERATE_MATCH"),
                        new RuleCondition("QuizPopularity", "POPULAR")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.65
        ));
        
        // Rule 11: Strong match but outdated
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "STRONG_MATCH"),
                        new RuleCondition("TemporalRelevance", "OUTDATED")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.55
        ));
        
        // Rule 12: Weak match with viral content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "WEAK_MATCH"),
                        new RuleCondition("QuizPopularity", "VIRAL")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.6
        ));
        
        // Rule 13: Disliked content should not be recommended
        ruleBase.addRule(new FuzzyRule(
                List.of(
                        new RuleCondition("LikeRatio", "DISLIKED")
                ),
                LogicalOperator.AND,
                "VERY_LOW",
                0.9
        ));
        
        // Rule 14: New content with moderate match
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TemporalRelevance", "NEW"),
                        new RuleCondition("TagSimilarity", "MODERATE_MATCH")
                ),
                LogicalOperator.AND,
                "HIGH",
                0.7
        ));
        
        // Rule 15: Recent content with strong match
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TemporalRelevance", "RECENT"),
                        new RuleCondition("TagSimilarity", "STRONG_MATCH")
                ),
                LogicalOperator.AND,
                "HIGH",
                0.75
        ));
        
        // Rule 16: Perfect length with moderate tags
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("LengthMatch", "PERFECT_MATCH"),
                        new RuleCondition("TagSimilarity", "MODERATE_MATCH")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.6
        ));
        
        // Rule 17: Some creator interaction with new content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("CreatorAffinity", "SOME_INTERACTION"),
                        new RuleCondition("TemporalRelevance", "NEW")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.65
        ));
        
        // Rule 18: Moderate popularity with liked content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("QuizPopularity", "MODERATE"),
                        new RuleCondition("LikeRatio", "LIKED")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.6
        ));
        
        // Rule 19: Strong match with neutral rating
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "STRONG_MATCH"),
                        new RuleCondition("LikeRatio", "NEUTRAL")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.55
        ));
        
        // Rule 20: Fan of creator even with weak match
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("CreatorAffinity", "FAN"),
                        new RuleCondition("TagSimilarity", "NO_MATCH")
                ),
                LogicalOperator.AND,
                "LOW",
                0.5
        ));
        
        // Rule 21: Old but loved content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TemporalRelevance", "OLD"),
                        new RuleCondition("LikeRatio", "LOVED")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.5
        ));
        
        // Rule 22: Weak match with moderate popularity
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "WEAK_MATCH"),
                        new RuleCondition("QuizPopularity", "MODERATE")
                ),
                LogicalOperator.AND,
                "LOW",
                0.45
        ));
        
        // Rule 23: Unpopular with neutral rating
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("QuizPopularity", "UNPOPULAR"),
                        new RuleCondition("LikeRatio", "NEUTRAL")
                ),
                LogicalOperator.AND,
                "LOW",
                0.5
        ));
        
        // Rule 24: Moderate match with perfect length
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "MODERATE_MATCH"),
                        new RuleCondition("LengthMatch", "PERFECT_MATCH"),
                        new RuleCondition("LikeRatio", "LIKED")
                ),
                LogicalOperator.AND,
                "HIGH",
                0.8
        ));
        
        // Rule 25: Recent popular content
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TemporalRelevance", "RECENT"),
                        new RuleCondition("QuizPopularity", "POPULAR"),
                        new RuleCondition("TagSimilarity", "WEAK_MATCH")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.6
        ));
        
        // === FALLBACK RULES (Lower weight, safety nets) ===
        
        // Fallback Rule 1: Complete mismatch → low score
        ruleBase.addRule(new FuzzyRule(
                List.of(
                        new RuleCondition("TagSimilarity", "NO_MATCH")
                ),
                LogicalOperator.AND,
                "LOW",
                0.25
        ));
        
        // Fallback Rule 2: All average inputs → medium score
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("TagSimilarity", "MODERATE_MATCH"),
                        new RuleCondition("QuizPopularity", "MODERATE"),
                        new RuleCondition("LikeRatio", "NEUTRAL")
                ),
                LogicalOperator.AND,
                "MEDIUM",
                0.3
        ));
        
        // Fallback Rule 3: Popular but no match → still give some credit
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("QuizPopularity", "VIRAL"),
                        new RuleCondition("TagSimilarity", "NO_MATCH")
                ),
                LogicalOperator.AND,
                "LOW",
                0.4
        ));
        
        // Fallback Rule 4: Outdated content → penalize
        ruleBase.addRule(new FuzzyRule(
                List.of(
                        new RuleCondition("TemporalRelevance", "OUTDATED")
                ),
                LogicalOperator.AND,
                "LOW",
                0.35
        ));
        
        // Fallback Rule 5: No creator history with weak match
        ruleBase.addRule(new FuzzyRule(
                Arrays.asList(
                        new RuleCondition("CreatorAffinity", "NO_HISTORY"),
                        new RuleCondition("TagSimilarity", "WEAK_MATCH")
                ),
                LogicalOperator.AND,
                "LOW",
                0.3
        ));
        
        return ruleBase;
    }
}


