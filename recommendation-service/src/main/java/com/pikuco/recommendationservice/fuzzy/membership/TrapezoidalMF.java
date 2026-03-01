package com.pikuco.recommendationservice.fuzzy.membership;

/**
 * Trapezoidal membership function.
 * Forms a trapezoid with parameters (a, b, c, d) where:
 * - a: left base point (membership = 0)
 * - b: left plateau point (membership = 1)
 * - c: right plateau point (membership = 1)
 * - d: right base point (membership = 0)
 */
public class TrapezoidalMF implements MembershipFunction {
    private final double a; // Left base
    private final double b; // Left plateau
    private final double c; // Right plateau
    private final double d; // Right base

    public TrapezoidalMF(double a, double b, double c, double d) {
        if (a > b || b > c || c > d) {
            throw new IllegalArgumentException("Parameters must satisfy: a <= b <= c <= d");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double getMembership(double value) {
        if (value <= a || value >= d) {
            return 0.0;
        } else if (value >= b && value <= c) {
            // Plateau region
            return 1.0;
        } else if (value > a && value < b) {
            // Rising slope
            return (value - a) / (b - a);
        } else { // value > c && value < d
            // Falling slope
            return (d - value) / (d - c);
        }
    }
}


