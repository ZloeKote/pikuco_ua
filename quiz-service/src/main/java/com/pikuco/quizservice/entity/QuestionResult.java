package com.pikuco.quizservice.entity;

import lombok.*;
import org.springframework.data.annotation.Transient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResult {
    private String title;
    private String description;
    private String url;
    private int place;
    @Transient
    private int score;
}
