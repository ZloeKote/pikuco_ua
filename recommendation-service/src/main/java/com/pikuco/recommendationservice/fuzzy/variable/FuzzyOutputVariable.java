package com.pikuco.recommendationservice.fuzzy.variable;

/**
 * Represents an output fuzzy variable (consequent in fuzzy rules).
 */
public class FuzzyOutputVariable extends FuzzyVariable {
    
    public FuzzyOutputVariable(String name, double minValue, double maxValue) {
        super(name, minValue, maxValue);
    }
}


