package com.pikuco.recommendationservice.fuzzy.variable;

/**
 * Represents an input fuzzy variable (antecedent in fuzzy rules).
 */
public class FuzzyInputVariable extends FuzzyVariable {
    
    public FuzzyInputVariable(String name, double minValue, double maxValue) {
        super(name, minValue, maxValue);
    }
}


