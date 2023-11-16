package com.pikuco.sharedComps.quizService.dto;

import com.pikuco.sharedComps.quizService.entity.Question;

import java.time.LocalDateTime;
import java.util.List;

public record QuizDto (
    String id,
    String title,
    String description,
    String type,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Creator creator,
    List<Question> questions
) {}
