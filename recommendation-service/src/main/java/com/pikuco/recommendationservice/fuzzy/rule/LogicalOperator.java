package com.pikuco.recommendationservice.fuzzy.rule;

/**
 * Logical operators for combining fuzzy rule conditions.
 */
public enum LogicalOperator {
    AND,  // Conjunction (uses MIN t-norm)
    OR    // Disjunction (uses MAX t-conorm)
}


