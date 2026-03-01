package com.pikuco.recommendationservice.fuzzy.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for all fuzzy rules in the system.
 */
public class FuzzyRuleBase {
    private final List<FuzzyRule> rules;

    public FuzzyRuleBase() {
        this.rules = new ArrayList<>();
    }

    public void addRule(FuzzyRule rule) {
        rules.add(rule);
    }

    public List<FuzzyRule> getRules() {
        return new ArrayList<>(rules);
    }

    public int size() {
        return rules.size();
    }
}


