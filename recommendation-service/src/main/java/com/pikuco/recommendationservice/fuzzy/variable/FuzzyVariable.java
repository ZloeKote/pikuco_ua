package com.pikuco.recommendationservice.fuzzy.variable;

import com.pikuco.recommendationservice.fuzzy.membership.MembershipFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a fuzzy variable with linguistic terms and membership functions.
 */
public abstract class FuzzyVariable {
    private final String name;
    private final double minValue;
    private final double maxValue;
    private final Map<String, MembershipFunction> terms;

    public FuzzyVariable(String name, double minValue, double maxValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.terms = new HashMap<>();
    }

    /**
     * Add a linguistic term with its membership function.
     */
    public void addTerm(String termName, MembershipFunction membershipFunction) {
        terms.put(termName, membershipFunction);
    }

    /**
     * Get the membership degree of a value in a specific term.
     */
    public double getMembership(String termName, double value) {
        MembershipFunction mf = terms.get(termName);
        if (mf == null) {
            throw new IllegalArgumentException("Term not found: " + termName);
        }
        return mf.getMembership(value);
    }

    /**
     * Get all membership degrees for a value across all terms.
     */
    public Map<String, Double> getAllMemberships(double value) {
        Map<String, Double> memberships = new HashMap<>();
        for (Map.Entry<String, MembershipFunction> entry : terms.entrySet()) {
            memberships.put(entry.getKey(), entry.getValue().getMembership(value));
        }
        return memberships;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public Map<String, MembershipFunction> getTerms() {
        return new HashMap<>(terms);
    }
}


