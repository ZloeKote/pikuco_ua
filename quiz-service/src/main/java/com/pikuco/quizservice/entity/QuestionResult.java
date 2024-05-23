package com.pikuco.quizservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
