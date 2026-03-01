package com.pikuco.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvaluationDto {
    private String id;
    private String type;
    private Long userId;
    private String evaluationObjectId;
    private boolean isLiked;
    private LocalDateTime evaluatedAt;
}

