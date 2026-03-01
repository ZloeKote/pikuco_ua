package com.pikuco.recommendationservice.fuzzy.rule;

import java.util.List;
import java.util.Map;

/**
 * Represents a fuzzy rule with conditions and output.
 * Example: IF TagSimilarity=STRONG_MATCH AND LikeRatio=LOVED THEN RecommendationScore=VERY_HIGH
 */
public class FuzzyRule {
    private final List<RuleCondition> conditions;
    private final LogicalOperator operator;
    private final String outputTerm;
    private final double weight;

    public FuzzyRule(List<RuleCondition> conditions, LogicalOperator operator, 
                     String outputTerm, double weight) {
        this.conditions = conditions;
        this.operator = operator;
        this.outputTerm = outputTerm;
        this.weight = weight;
    }

    /**
     * Evaluate the rule given the fuzzified inputs.
     * Returns the firing strength (activation level) of the rule.
     * 
     * @param fuzzifiedInputs Map of variable name -> Map of term -> membership degree
     * @return firing strength weighted by rule weight
     */
    public double evaluate(Map<String, Map<String, Double>> fuzzifiedInputs) {
        if (conditions.isEmpty()) {
            return 0.0;
        }

        double result;
        if (operator == LogicalOperator.AND) {
            // Use MIN for AND (standard t-norm)
            result = conditions.stream()
                    .mapToDouble(c -> getMembership(c, fuzzifiedInputs))
                    .min()
                    .orElse(0.0);
        } else { // LogicalOperator.OR
            // Use MAX for OR (standard t-conorm)
            result = conditions.stream()
                    .mapToDouble(c -> getMembership(c, fuzzifiedInputs))
                    .max()
                    .orElse(0.0);
        }

        return result * weight; // Apply rule weight
    }

    /**
     * Get membership degree for a specific condition.
     */
    private double getMembership(RuleCondition condition, 
                                  Map<String, Map<String, Double>> fuzzifiedInputs) {
        Map<String, Double> variableMemberships = fuzzifiedInputs.get(condition.getVariableName());
        if (variableMemberships == null) {
            return 0.0;
        }
        return variableMemberships.getOrDefault(condition.getTerm(), 0.0);
    }

    // Getters
    public String getOutputTerm() {
        return outputTerm;
    }

    public double getWeight() {
        return weight;
    }

    public List<RuleCondition> getConditions() {
        return conditions;
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IF ");
        for (int i = 0; i < conditions.size(); i++) {
            sb.append(conditions.get(i));
            if (i < conditions.size() - 1) {
                sb.append(" ").append(operator).append(" ");
            }
        }
        sb.append(" THEN RecommendationScore=").append(outputTerm);
        sb.append(" (weight: ").append(weight).append(")");
        return sb.toString();
    }
}


