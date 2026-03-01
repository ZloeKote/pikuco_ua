package com.pikuco.recommendationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferenceProfileDto {
    private Long userId;
    private Map<String, Double> tagAffinities;
    private Map<String, Integer> creatorInteractions;
    private int avgQuizLength;
    private Set<Integer> completedQuizPseudoIds;
    private Set<Integer> evaluatedQuizPseudoIds;
}


