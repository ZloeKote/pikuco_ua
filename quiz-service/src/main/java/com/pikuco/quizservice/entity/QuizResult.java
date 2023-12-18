package com.pikuco.quizservice.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResult {
    List<QuestionResult> questions;
    int participant_id;
    LocalDateTime passedAt;
}
