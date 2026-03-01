package com.pikuco.recommendationservice.fuzzy.inference;

import com.pikuco.recommendationservice.fuzzy.rule.FuzzyRule;
import com.pikuco.recommendationservice.fuzzy.rule.FuzzyRuleBase;
import com.pikuco.recommendationservice.fuzzy.variable.FuzzyInputVariable;
import com.pikuco.recommendationservice.fuzzy.variable.FuzzyOutputVariable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Fuzzy inference engine implementing the Mamdani inference method.
 * Steps: Fuzzification -> Rule Evaluation -> Aggregation -> Defuzzification
 */
@Service
public class FuzzyInferenceEngine {

    private Map<String, FuzzyInputVariable> inputVariables;
    private FuzzyOutputVariable outputVariable;
    private FuzzyRuleBase ruleBase;

    public void initialize(Map<String, FuzzyInputVariable> inputVariables,
                           FuzzyOutputVariable outputVariable,
                           FuzzyRuleBase ruleBase) {
        this.inputVariables = inputVariables;
        this.outputVariable = outputVariable;
        this.ruleBase = ruleBase;
    }

    /**
     * Main inference method.
     * Takes crisp normalized inputs [0,1] and returns a crisp output score.
     */
    public double infer(Map<String, Double> crispInputs) {
        // Step 1: Fuzzification
        Map<String, Map<String, Double>> fuzzifiedInputs = fuzzify(crispInputs);

        // Step 2: Rule evaluation and aggregation
        Map<String, Double> aggregatedOutputs = evaluateAndAggregateRules(fuzzifiedInputs);

        // Step 3: Defuzzification
        return defuzzify(aggregatedOutputs);
    }

    /**
     * Fuzzification: Convert crisp inputs to fuzzy membership degrees.
     */
    private Map<String, Map<String, Double>> fuzzify(Map<String, Double> crispInputs) {
        Map<String, Map<String, Double>> fuzzified = new HashMap<>();

        for (Map.Entry<String, Double> input : crispInputs.entrySet()) {
            String varName = input.getKey();
            double value = input.getValue();

            FuzzyInputVariable variable = inputVariables.get(varName);
            if (variable != null) {
                fuzzified.put(varName, variable.getAllMemberships(value));
            }
        }

        return fuzzified;
    }

    /**
     * Evaluate all rules and aggregate their outputs.
     * Uses MAX aggregation for combining rule outputs.
     */
    private Map<String, Double> evaluateAndAggregateRules(Map<String, Map<String, Double>> fuzzifiedInputs) {
        Map<String, Double> aggregated = new HashMap<>();

        // TODO: REMOVE THESE
        fuzzifiedInputs.computeIfAbsent("TagSimilarity", k -> new HashMap<>())
                .put("NO_MATCH", 1.0);
        fuzzifiedInputs.computeIfAbsent("QuizPopularity", k -> new HashMap<>())
                .put("VIRAL", 1.0);

        for (FuzzyRule rule : ruleBase.getRules()) {
            double firingStrength = rule.evaluate(fuzzifiedInputs);
            String outputTerm = rule.getOutputTerm();

            // Use MAX aggregation across all rules (standard in Mamdani)
            aggregated.merge(outputTerm, firingStrength, Math::max);
        }

        return aggregated;
    }

    /**
     * Defuzzification using the centroid (center of gravity) method.
     * Includes improvements: adaptive step size and zero-division guard.
     */
    private double defuzzify(Map<String, Double> outputMemberships) {
        double min = outputVariable.getMinValue();
        double max = outputVariable.getMaxValue();

        // Adaptive step size: divide range into 200 steps for precision
        double step = (max - min) / 200.0;

        double numerator = 0.0;
        double denominator = 0.0;

        for (double x = min; x <= max; x += step) {
            double membership = calculateAggregatedMembership(x, outputMemberships);
            numerator += x * membership;
            denominator += membership;
        }

        // Edge case: no rules fired (denominator is zero)
        if (denominator < 0.0001) {
            return 50.0; // Return neutral default score
        }

        return numerator / denominator;
    }

    /**
     * Calculate the aggregated membership degree at a point x.
     * Uses Mamdani implication (min) and MAX aggregation.
     */
    private double calculateAggregatedMembership(double x,
                                                 Map<String, Double> outputMemberships) {
        double maxMembership = 0.0;

        for (Map.Entry<String, Double> entry : outputMemberships.entrySet()) {
            String term = entry.getKey();
            double firingStrength = entry.getValue();

            // Get membership degree of x in this term's membership function
            double termMembership = outputVariable.getMembership(term, x);

            // Apply Mamdani implication (min operation)
            double clipped = Math.min(firingStrength, termMembership);

            // Aggregate with max
            maxMembership = Math.max(maxMembership, clipped);
        }

        return maxMembership;
    }
}


