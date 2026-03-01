package com.pikuco.recommendationservice.fuzzy.membership;

/**
 * Interface for fuzzy membership functions.
 * A membership function maps a crisp input value to a degree of membership [0, 1]
 * in a fuzzy set.
 */
public interface MembershipFunction {
    /**
     * Calculate the membership degree of a given value.
     * 
     * @param value the crisp input value
     * @return membership degree in range [0, 1]
     */
    double getMembership(double value);
}


