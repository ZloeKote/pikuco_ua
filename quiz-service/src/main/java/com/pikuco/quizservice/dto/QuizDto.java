package com.pikuco.quizservice.dto;


import com.pikuco.quizservice.entity.Creator;
import com.pikuco.quizservice.entity.Question;
import com.pikuco.quizservice.entity.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.List;

public record QuizDto (
        String title,
        String description,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Creator creator,
        List<Question> questions,
        int pseudoId,
        boolean isRoughDraft
) {}
