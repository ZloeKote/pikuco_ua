package com.pikuco.recommendationservice.fuzzy.membership;

/**
 * Triangular membership function.
 * Forms a triangle with parameters (a, b, c) where:
 * - a: left base point (membership = 0)
 * - b: peak point (membership = 1)
 * - c: right base point (membership = 0)
 */
public class TriangularMF implements MembershipFunction {
    private final double a; // Left base
    private final double b; // Peak
    private final double c; // Right base

    public TriangularMF(double a, double b, double c) {
        if (a > b || b > c) {
            throw new IllegalArgumentException("Parameters must satisfy: a <= b <= c");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double getMembership(double value) {
        if (value <= a || value >= c) {
            return 0.0;
        } else if (value == b) {
            return 1.0;
        } else if (value > a && value < b) {
            // Rising slope
            return (value - a) / (b - a);
        } else { // value > b && value < c
            // Falling slope
            return (c - value) / (c - b);
        }
    }
}


