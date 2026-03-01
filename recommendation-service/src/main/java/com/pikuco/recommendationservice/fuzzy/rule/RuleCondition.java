package com.pikuco.recommendationservice.fuzzy.rule;

/**
 * Represents a single condition in a fuzzy rule.
 * Example: "TagSimilarity = STRONG_MATCH"
 */
public class RuleCondition {
    private final String variableName;
    private final String term;

    public RuleCondition(String variableName, String term) {
        this.variableName = variableName;
        this.term = term;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return variableName + "=" + term;
    }
}


